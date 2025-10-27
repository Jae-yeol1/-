package com.cardgame.blackjack;

import com.cardgame.card.*;
import java.util.*;

public class BlackJack implements Game {

    @Override
    public void playOneRound(Scanner in, Deck deck) {
        List<Card> player = new ArrayList<>();
        List<Card> dealer = new ArrayList<>();

        // 초기 2장씩
        player.add(deck.draw()); player.add(deck.draw());
        dealer.add(deck.draw()); dealer.add(deck.draw());

        System.out.println("초기 손패: " + handStr(player) + " (합=" + value(player) + ")");

        // 플레이어 턴
        while (value(player) <= 21) {
            System.out.print("1: HIT, 2: STAND > ");
            String s = in.nextLine().trim();
            if ("1".equals(s)) {
                player.add(deck.draw());
                System.out.println("손패: " + handStr(player) + " (합=" + value(player) + ")");
                if (value(player) > 21) {
                    System.out.println("버스트! 패배");
                    return; // 딜러 진행 없이 종료
                }
            } else if ("2".equals(s)) {
                System.out.println("스탠드. 최종 합=" + value(player));
                break;
            } else {
                System.out.println("1 또는 2를 입력해주세요.");
            }
        }

        // 딜러 턴(S17)
        while (value(dealer) < 17) {
            dealer.add(deck.draw());
            if (value(dealer) > 21) {
                System.out.println("딜러: " + handStr(dealer) + " (합=" + value(dealer) + ")");
                System.out.println("딜러 버스트! 승리");
                return;
            }
        }

        // 최종 비교
        int pv = value(player), dv = value(dealer);
        System.out.println("딜러: " + handStr(dealer) + " (합=" + dv + ")");
        if (pv > dv)      System.out.println("유저 승리!");
        else if (pv < dv) System.out.println("유저 패배");
        else              System.out.println("무승부(Push)");
    }

    // ===== 블랙잭 점수 계산(계산 시 A=11→1 보정) =====
    static int value(List<Card> hand) {
        int sum = 0, aces = 0;
        for (Card c : hand) {
            switch (c.rank()) {
                case ACE -> { sum += 11; aces++; }
                case TEN, JACK, QUEEN, KING -> sum += 10;
                default -> sum += c.rank().ordinal() + 1; // TWO=1 → +1 = 2
            }
        }
        while (sum > 21 && aces-- > 0) sum -= 10;
        return sum;
    }

    // 표시용: A,2..10,J,Q,K
    static String handStr(List<Card> h) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < h.size(); i++) {
            var r = h.get(i).rank();
            sb.append(switch (r) {
                case ACE -> "A"; case JACK -> "J"; case QUEEN -> "Q"; case KING -> "K";
                case TEN -> "10";
                default -> String.valueOf(r.ordinal() + 1);
            });
            if (i < h.size() - 1) sb.append(", ");
        }
        return sb.append("]").toString();
    }
}