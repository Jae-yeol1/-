package com.cardgame.app;

import com.cardgame.blackjack.BlackJack;
import com.cardgame.card.Deck;
import com.cardgame.card.Game;
import com.cardgame.card.Shoe;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        var sc = new Scanner(System.in);

        int decks = askInt(sc, "덱 수 입력 (예: 2 또는 3) > ", 1, 8);
        Deck shoe = new Shoe(decks);

        while (true) {
            System.out.println("\n== 게임 선택 ==");
            System.out.println("1) 블랙잭");
            System.out.println("2) 바카라");
            System.out.println("3) 인디언포커");
            System.out.println("0) 종료");
            int sel = askInt(sc, "> ", 0, 3);

            if (sel == 0) {
                System.out.println("종료합니다.");
                return;
            }

            Game game = switch (sel) {
                case 1 -> new BlackJack();     // 네가 이미 만든 룰 클래스
//                case 2 -> new Baccarat();
//                case 3 -> new IndianPoker();
                default -> null;
            };

            if (game != null) game.playOneRound(sc, shoe);
        }
    }

    private static int askInt(Scanner sc, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try {
                int v = Integer.parseInt(s);
                if (v < min || v > max) throw new NumberFormatException();
                return v;
            } catch (NumberFormatException e) {
                System.out.println("숫자 " + min + "~" + max + " 범위로 다시 입력해주세요.");
            }
        }
    }
}
