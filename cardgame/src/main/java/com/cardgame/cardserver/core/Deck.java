package com.cardgame.cardserver.core;
import java.util.*;
public class Deck {
    private final Deque<Card> q = new ArrayDeque<>();
    public Deck(int decks){
        List<Card> all=new ArrayList<>();
        for(int d=0; d<Math.max(1,decks); d++){
            for(Card.Suit s: Card.Suit.values()){
                for(Card.Rank r: Card.Rank.values()){
                    all.add(new Card(r,s));
                }
            }
        }
        Collections.shuffle(all, new Random());
        q.addAll(all);
    }
    public Card draw(){ if(q.isEmpty()) throw new RuntimeException("empty deck"); return q.removeFirst(); }
    public int left(){ return q.size(); }
}
