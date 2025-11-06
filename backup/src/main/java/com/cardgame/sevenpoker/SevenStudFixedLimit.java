package com.cardgame.sevenpoker;

import com.cardgame.card.*;
import com.cardgame.card.Game;
import com.cardgame.sevenpoker.PokerEval.*;

import java.util.*;
import static com.cardgame.card.CardStrings.*;

public class SevenStudFixedLimit implements Game {

    /* ====================== 디버그/성향 프로필 ====================== */
    private static final boolean DEBUG = false;
    private static void dbg(String s){ if (DEBUG) System.out.println(s); }

    // 딜러 성향(판마다 랜덤 생성, 플레이어에게 비공개)
    private static final class DealerProfile {
        final String name;
        final double bluffBase;   // 블러핑 기저
        final double betAgg;      // 베팅 성향
        final double raiseAgg;    // 레이즈 성향
        final double callTight;   // 콜 보수성(클수록 콜 적음)
        final double semiBluff;   // 드로우일 때 공격 가산
        final double[] streetMult; // 3~7 스트리트 가중치

        DealerProfile(String name, double bluffBase, double betAgg, double raiseAgg,
                      double callTight, double semiBluff, double[] streetMult) {
            this.name = name; this.bluffBase = bluffBase; this.betAgg = betAgg;
            this.raiseAgg = raiseAgg; this.callTight = callTight;
            this.semiBluff = semiBluff; this.streetMult = streetMult;
        }
        static DealerProfile random(java.util.Random r) {
            double p = r.nextDouble();
            if (p < 0.20) return new DealerProfile("Nit", 0.02, 0.25, 0.15, 0.70, 0.05,
                    new double[]{1.0, 0.9, 0.8, 0.7, 0.6});
            else if (p < 0.50) return new DealerProfile("TAG", 0.05, 0.45, 0.35, 0.45, 0.15,
                    new double[]{1.0, 1.0, 0.9, 0.9, 0.8});
            else if (p < 0.80) return new DealerProfile("LAG", 0.08, 0.65, 0.55, 0.35, 0.25,
                    new double[]{1.1, 1.1, 1.0, 0.95, 0.9});
            else if (p < 0.95) return new DealerProfile("Maniac", 0.12, 0.85, 0.75, 0.20, 0.30,
                    new double[]{1.2, 1.2, 1.1, 1.0, 0.95});
            else return new DealerProfile("Balanced", 0.06, 0.50, 0.40, 0.40, 0.20,
                        new double[]{1.0, 1.0, 1.0, 1.0, 1.0});
        }
    }
    private final java.util.Random rng = new java.util.Random();

    /* ========================== 베팅 정산 구조 ========================== */
    private static final class StreetPaid {
        final int player, dealer;
        final boolean playerFolded, playerAllIn;
        StreetPaid(int player, int dealer, boolean playerFolded, boolean playerAllIn){
            this.player = player; this.dealer = dealer;
            this.playerFolded = playerFolded; this.playerAllIn = playerAllIn;
        }
    }

    @Override
    public void playOneRound(Scanner in, Deck deck) {
        playOneRoundForBet(in, deck, 0, Integer.MAX_VALUE);
    }

    /** ante: 기본 베팅(메인에서 잔액 10% 이내로 보정)
     *  stackForHand: 이 핸드에서 추가로 투자 가능한 총액(현재 잔액)
     */
    public int playOneRoundForBet(Scanner in, Deck deck, int ante, int stackForHand) {
        final int small = Math.max(ante, 1);
        final int big   = Math.max(small * 2, small + 1);

        // ★판마다 딜러 성향 생성(비공개)
        DealerProfile profile = DealerProfile.random(rng);
        dbg("딜러 성향: " + profile.name);

        List<Card> pDown = new ArrayList<>(3), pUp = new ArrayList<>(4);
        List<Card> dDown = new ArrayList<>(3), dUp = new ArrayList<>(4);

        // 초기 배분: 2down+1up
        pDown.add(deck.draw()); dDown.add(deck.draw());
        pDown.add(deck.draw()); dDown.add(deck.draw());
        pUp.add(deck.draw());   dUp.add(deck.draw());

        int pot = 0, delta = 0;
        boolean playerAllInEver = false;
        int totalP = 0, totalD = 0;

        // Ante 납부(플레이어만, 스택 차감)
        int maxPay = Math.min(ante, stackForHand);
        pot += maxPay; delta -= maxPay; totalP += maxPay;
        stackForHand -= maxPay;
        if (maxPay < ante) {
            System.out.println("※ 잔액 부족으로 Ante는 올인 처리(" + maxPay + ")");
            playerAllInEver = true;
        }

        banner("Seven-Card Stud (Fixed-Limit)");
        printState(pDown, pUp, dDown, dUp);
        printPotAndBet(pot, small);

        // 3rd
        int[] stackRef = new int[]{ stackForHand };
        StreetPaid r3 = bettingRound(in, "3rd", small, pUp, dUp, stackRef, profile, 3);
        pot += r3.player + r3.dealer; delta -= r3.player; totalP += r3.player; totalD += r3.dealer;
        playerAllInEver |= r3.playerAllIn;
        System.out.printf("[정산] 3rd: P=%+d, D=%+d → Pot=%,d%n", r3.player, r3.dealer, pot);
        if (r3.playerFolded) { System.out.println("플레이어 폴드"); return delta; }

        // 4th
        pUp.add(deck.draw()); dUp.add(deck.draw());
        banner("4th Street - 카드 배분");
        printState(pDown, pUp, dDown, dUp);
        printPotAndBet(pot, small);
        StreetPaid r4 = bettingRound(in, "4th", small, pUp, dUp, stackRef, profile, 4);
        pot += r4.player + r4.dealer; delta -= r4.player; totalP += r4.player; totalD += r4.dealer;
        playerAllInEver |= r4.playerAllIn;
        System.out.printf("[정산] 4th: P=%+d, D=%+d → Pot=%,d%n", r4.player, r4.dealer, pot);
        if (r4.playerFolded) { System.out.println("플레이어 폴드"); return delta; }

        // 5th
        pUp.add(deck.draw()); dUp.add(deck.draw());
        banner("5th Street - 카드 배분");
        printState(pDown, pUp, dDown, dUp);
        printPotAndBet(pot, big);
        StreetPaid r5 = bettingRound(in, "5th", big, pUp, dUp, stackRef, profile, 5);
        pot += r5.player + r5.dealer; delta -= r5.player; totalP += r5.player; totalD += r5.dealer;
        playerAllInEver |= r5.playerAllIn;
        System.out.printf("[정산] 5th: P=%+d, D=%+d → Pot=%,d%n", r5.player, r5.dealer, pot);
        if (r5.playerFolded) { System.out.println("플레이어 폴드"); return delta; }

        // 6th
        pUp.add(deck.draw()); dUp.add(deck.draw());
        banner("6th Street - 카드 배분");
        printState(pDown, pUp, dDown, dUp);
        printPotAndBet(pot, big);
        StreetPaid r6 = bettingRound(in, "6th", big, pUp, dUp, stackRef, profile, 6);
        pot += r6.player + r6.dealer; delta -= r6.player; totalP += r6.player; totalD += r6.dealer;
        playerAllInEver |= r6.playerAllIn;
        System.out.printf("[정산] 6th: P=%+d, D=%+d → Pot=%,d%n", r6.player, r6.dealer, pot);
        if (r6.playerFolded) { System.out.println("플레이어 폴드"); return delta; }

        // 7th
        pDown.add(deck.draw()); dDown.add(deck.draw());
        banner("7th Street - 다운카드 배분");
        System.out.println("다운카드가 배분되었습니다. (딜러 다운은 가림)");
        printState(pDown, pUp, dDown, dUp);
        printPotAndBet(pot, big);
        StreetPaid r7 = bettingRound(in, "7th", big, pUp, dUp, stackRef, profile, 7);
        pot += r7.player + r7.dealer; delta -= r7.player; totalP += r7.player; totalD += r7.dealer;
        playerAllInEver |= r7.playerAllIn;
        System.out.printf("[정산] 7th: P=%+d, D=%+d → Pot=%,d%n", r7.player, r7.dealer, pot);
        if (r7.playerFolded) { System.out.println("플레이어 폴드"); return delta; }

        // 쇼다운
        List<Card> pAll = new ArrayList<>(pDown); pAll.addAll(pUp);
        List<Card> dAll = new ArrayList<>(dDown); dAll.addAll(dUp);

        banner("쇼다운 (딜러 히든 3장 공개)");
        System.out.println("플레이어 전체: " + pAll.stream().map(c -> card(c, true)).toList());
        System.out.println("딜러    전체: " + dAll.stream().map(c -> card(c, true)).toList());

        HandScore ps = PokerEval.evaluate(best5(pAll));
        HandScore ds = PokerEval.evaluate(best5(dAll));
        System.out.println("플레이어 족보: " + ps);
        System.out.println("딜러   족보 : " + ds);

        int cmp = ps.compareTo(ds);
        if (cmp > 0) {
            System.out.println("플레이어 승 → 팟 획득 +" + pot);
            delta += pot;
        } else if (cmp < 0) {
            System.out.println("딜러 승");
        } else {
            System.out.println("무승부 → 팟 반분");
            delta += pot / 2;
        }
        return delta;
    }

    /* ----------------- 레이즈 캡 3 + 올인 + 성향 기반 딜러 ----------------- */
    private StreetPaid bettingRound(Scanner in, String street, int betSize,
                                    List<Card> pUp, List<Card> dUp, int[] stackRef,
                                    DealerProfile profile, int streetNo) {
        int payP = 0, payD = 0;
        int toCall = 0;
        int raises = 0;                   // 스트리트당 총 레이즈 수(플레이어/딜러 합산, 캡=3)
        boolean bettingOpen = true;
        boolean playerAllIn = false;

        System.out.printf("%n[%s] 베팅 사이즈 %,d — 당신 선행%n", street, betSize);
        int pMax = upMaxRank(pUp), dMax = upMaxRank(dUp);
        System.out.printf("업카드 요약: P-Max=%d vs D-Max=%d%n", pMax, dMax);

        // --- 플레이어 선행 ---
        if (toCall == 0 && bettingOpen) {
            int choice = ask(in,
                    (raises < 3 ? "1) 체크  2) 베트(" + betSize + ")" : "1) 체크") + "  3) 폴드 > ",
                    1, 3);
            if (choice == 1) {
                // 체크
            } else if (choice == 2 && raises < 3) {
                int need = betSize;
                int pay = Math.min(need, stackRef[0]);
                payP += pay; stackRef[0] -= pay;
                toCall = pay; bettingOpen = false;
                if (pay < need) { playerAllIn = true; } else { raises++; }
            } else if (choice == 3) {
                return new StreetPaid(payP, payD, true, playerAllIn);
            }
        }

        // ===== 딜러 의사결정 (프로필/보드상황 반영) =====
        boolean dPairUp    = hasUpPair(dUp);
        boolean dFlushDraw = hasFlushDrawUp(dUp);
        boolean dStrDraw   = hasStraightDrawUp(dUp);
        double streetMul   = profile.streetMult[Math.max(0, Math.min(4, streetNo-3))];

        if (toCall == 0 && bettingOpen) {
            // 플레이어 체크 → 딜러가 베팅할지
            double base = profile.betAgg * streetMul;
            base += (dMax > pMax ? 0.20 : dMax == pMax ? 0.05 : -0.10);
            if (dPairUp)    base += 0.25;
            if (dFlushDraw) base += profile.semiBluff;
            if (dStrDraw)   base += profile.semiBluff * 0.7;
            base += profile.bluffBase;

            boolean bet = rng.nextDouble() < clamp01(base);
            if (bet && raises < 3) {
                System.out.println("딜러: 베트(" + betSize + ")");
                payD += betSize; toCall = betSize; bettingOpen = false; raises++;
            } else {
                System.out.println("딜러: 체크");
                return new StreetPaid(payP, payD, false, playerAllIn);
            }

        } else if (toCall > 0) {
            // 플레이어가 베팅/올인 → 딜러 대응
            boolean allowRaise = (!playerAllIn && raises < 3);

            double rBase = profile.raiseAgg * streetMul;
            rBase += (dMax > pMax ? 0.20 : dMax == pMax ? 0.05 : -0.10);
            if (dPairUp)    rBase += 0.20;
            if (dFlushDraw) rBase += profile.semiBluff * 0.8;
            if (dStrDraw)   rBase += profile.semiBluff * 0.6;
            rBase -= profile.callTight * 0.15;
            rBase += profile.bluffBase * 0.5;

            double cBase = 0.55 - profile.callTight * 0.15;
            cBase += (dMax >= pMax ? 0.10 : -0.05);
            cBase += (streetNo <= 4 ? 0.08 : -0.05);

            if (!allowRaise) {
                // 올인/캡 → 콜/폴드만 (여기선 콜로 단순화)
                System.out.println("딜러: 콜(" + toCall + ")");
                payD += toCall; toCall = 0;
            } else {
                double rv = rng.nextDouble();
                if (rv < clamp01(rBase)) {
                    System.out.println("딜러: 레이즈(" + (toCall + betSize) + ")");
                    payD += toCall + betSize; toCall = betSize; raises++;
                } else if (rv < clamp01(rBase + cBase)) {
                    System.out.println("딜러: 콜(" + toCall + ")");
                    payD += toCall; toCall = 0;
                } else {
                    System.out.println("딜러: 콜(" + toCall + ")");
                    payD += toCall; toCall = 0;
                }
            }
        }

        // --- 플레이어 마무리(딜러가 베팅/레이즈했다면) ---
        if (toCall > 0) {
            boolean canReRaise = (!playerAllIn && raises < 3);
            String menu = canReRaise
                    ? "1) 콜(" + toCall + ")  2) 리레이즈(" + (toCall + betSize) + ")  3) 폴드 > "
                    : "1) 콜(" + toCall + ")  3) 폴드 > ";
            int fin = ask(in, menu, 1, 3);

            if (fin == 1) { // 콜 (올인 보정)
                int need = toCall;
                int pay = Math.min(need, stackRef[0]);
                payP += pay; stackRef[0] -= pay; toCall -= pay;
                if (pay < need) { playerAllIn = true; toCall = 0; }
            } else if (fin == 2 && canReRaise) { // 리레이즈 (올인 보정)
                int need = toCall + betSize;
                int pay = Math.min(need, stackRef[0]);
                payP += pay; stackRef[0] -= pay;
                if (pay < need) { // 올인 리레이즈 → 딜러는 콜만
                    playerAllIn = true; toCall = pay - (need - betSize);
                    if (toCall < 0) toCall = 0;
                } else {
                    toCall = betSize; raises++;
                }
                if (toCall > 0) { // 딜러 최종 콜
                    System.out.println("딜러: 콜(" + toCall + ")");
                    payD += toCall; toCall = 0;
                }
            } else {
                return new StreetPaid(payP, payD, true, playerAllIn);
            }
        }

        return new StreetPaid(payP, payD, false, playerAllIn);
    }

    /* ----------------- 표시/보조 ----------------- */
    private void banner(String title) { System.out.println("\n==== " + title + " ===="); }

    private void printPotAndBet(int pot, int betSize) {
        System.out.printf("Pot: %,d   BetSize: %,d%n", pot, betSize);
    }

    private void printState(List<Card> pDown, List<Card> pUp,
                            List<Card> dDown, List<Card> dUp) {
        List<String> pAll = new ArrayList<>(pDown.size() + pUp.size());
        for (Card c : pDown) pAll.add(card(c, true));
        for (Card c : pUp)   pAll.add(card(c, true));
        System.out.println("플레이어: " + pAll);

        String dealerLine = "[" + maskList(dDown.size())
                + (dUp.isEmpty() ? "]" : (dDown.isEmpty() ? "" : ", ") + upList(dUp) + "]");
        System.out.println("딜러    : " + dealerLine);
    }

    private String upList(List<Card> ups) {
        List<String> s = new ArrayList<>(ups.size());
        for (Card c : ups) s.add(card(c, true));
        return String.join(", ", s);
    }
    private String maskList(int n) {
        if (n <= 0) return "";
        String[] arr = new String[n]; Arrays.fill(arr, "□");
        return String.join(", ", arr);
    }

    private int upMaxRank(List<Card> up) {
        int best = 0;
        for (Card c : up) {
            int v = switch (c.rank()) {
                case ACE -> 14;
                case KING -> 13; case QUEEN -> 12; case JACK -> 11; case TEN -> 10;
                case NINE -> 9; case EIGHT -> 8; case SEVEN -> 7; case SIX -> 6; case FIVE -> 5;
                case FOUR -> 4; case THREE -> 3; case TWO -> 2;
            };
            best = Math.max(best, v);
        }
        return best;
    }

    // 업카드 특징
    private boolean hasUpPair(List<Card> up) {
        boolean[] seen = new boolean[15];
        for (Card c : up) {
            int rv = switch (c.rank()) {
                case ACE -> 14; case KING -> 13; case QUEEN -> 12; case JACK -> 11; case TEN -> 10;
                case NINE -> 9; case EIGHT -> 8; case SEVEN -> 7; case SIX -> 6; case FIVE -> 5;
                case FOUR -> 4; case THREE -> 3; case TWO -> 2;
            };
            if (seen[rv]) return true;
            seen[rv] = true;
        }
        return false;
    }
    private boolean hasFlushDrawUp(List<Card> up) {
        int[] sc = new int[4];
        for (Card c : up) sc[c.suit().ordinal()]++;
        for (int s : sc) if (s >= 3) return true;
        return false;
    }
    private boolean hasStraightDrawUp(List<Card> up) {
        boolean[] have = new boolean[15];
        for (Card c : up) {
            int rv = switch (c.rank()) {
                case ACE -> 14; case KING -> 13; case QUEEN -> 12; case JACK -> 11; case TEN -> 10;
                case NINE -> 9; case EIGHT -> 8; case SEVEN -> 7; case SIX -> 6; case FIVE -> 5;
                case FOUR -> 4; case THREE -> 3; case TWO -> 2;
            };
            have[rv] = true;
        }
        int run = 0;
        for (int v = 2; v <= 14; v++) {
            run = have[v] ? run + 1 : 0;
            if (run >= 3) return true;
        }
        return (have[14] && have[2] && have[3]); // A-로우 느슨 허용
    }
    private double clamp01(double x) { return x < 0 ? 0 : (x > 1 ? 1 : x); }

    // 7장 중 최적 5장
    private List<Card> best5(List<Card> seven) {
        if (seven.size() != 7) throw new IllegalArgumentException("need 7 cards");
        List<Card> best = null;
        for (int a = 0; a < 3; a++)
            for (int b = a+1; b < 4; b++)
                for (int c = b+1; c < 5; c++)
                    for (int d = c+1; d < 6; d++)
                        for (int e = d+1; e < 7; e++) {
                            var five = List.of(seven.get(a), seven.get(b), seven.get(c), seven.get(d), seven.get(e));
                            var ps = PokerEval.evaluate(five);
                            if (best == null) { best = new ArrayList<>(five); }
                            else {
                                var bs = PokerEval.evaluate(best);
                                if (ps.compareTo(bs) > 0) best = new ArrayList<>(five);
                            }
                        }
        return best;
    }

    private int ask(Scanner sc, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try {
                int v = Integer.parseInt(s);
                if (v < min || v > max) throw new NumberFormatException();
                return v;
            } catch (NumberFormatException e) {
                System.out.println("숫자 " + min + "~" + max + " 범위로 다시 입력");
            }
        }
    }
}
