package com.cardgame.card;
import java.util.*;


public class Shoe implements Deck {
    private final List<Card> cards = new ArrayList<>(); // 덱 리스트 선언
    private final int decks; // n개의 덱
    private final Random rand = new Random(); // 셔플용 난수

    public Shoe(int decks){
        if(decks < 1) throw new IllegalArgumentException("decks must be >= 1");
        this.decks = decks;
        reshuffle();
    }

    private void reshuffle() {
        cards.clear();
        for (int d = 0; d < decks; d++) {
            for (Card.Suit s : Card.Suit.values()) {
                for (Card.Rank r : Card.Rank.values()) {
                    cards.add(new Card(s, r));
                }
            }
        }
        Collections.shuffle(cards, rand);
    }

    @Override
    public Card draw() {
        if(cards.isEmpty()) reshuffle();
        return cards.remove(cards.size()-1);
    }

    @Override
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    @Override
    public int remaining() {
        return cards.size();
    }
}