package com.cardgame.cardserver.core;

import java.util.*;

public class BlackjackRoom {
    public static class Hand {
        public final List<Card> cards = new ArrayList<>();
        public int total;
        public boolean done;
        public boolean bust;
    }
    public static class State {
        public boolean inProgress;
        public int bet;                     // 모두 동일 베팅(간단화). 필요시 per-user로 확장
        public Map<String,Hand> hands = new LinkedHashMap<>();
        public Hand dealer = new Hand();
        public int decks;
        public Set<String> finished = new HashSet<>();
        public int deltaTotal;              // 정산 총합(방 입장자 sum)
    }

    private final Deck deck;
    public final State s = new State();

    public BlackjackRoom(int decks){
        this.deck = new Deck(Math.max(1, decks));
        s.decks = decks;
    }

    private static int bestTotal(List<Card> cs){
        int sum=0, aces=0;
        for(Card c: cs){ sum += c.bjValue(); if(c.rank()== Card.Rank.ACE) aces++; }
        while(sum>21 && aces>0){ sum -= 10; aces--; }
        return sum;
    }

    public State start(Collection<String> users, int bet){
        s.inProgress=true; s.bet=bet; s.deltaTotal=0;
        s.hands.clear(); s.finished.clear();
        s.dealer = new Hand();
        for(String u: users){
            Hand h = new Hand();
            h.cards.add(deck.draw()); h.cards.add(deck.draw());
            h.total = bestTotal(h.cards);
            s.hands.put(u, h);
        }
        s.dealer.cards.add(deck.draw()); s.dealer.cards.add(deck.draw());
        s.dealer.total = bestTotal(s.dealer.cards);
        return s;
    }

    public State hit(String user){
        if(!s.inProgress) return s;
        Hand h = s.hands.get(user);
        if(h==null || h.done) return s;
        h.cards.add(deck.draw());
        h.total = bestTotal(h.cards);
        if(h.total>21){ h.bust=true; h.done=true; s.finished.add(user); settleMaybe(); }
        return s;
    }

    public State stand(String user){
        if(!s.inProgress) return s;
        Hand h = s.hands.get(user);
        if(h==null || h.done) return s;
        h.done=true;
        s.finished.add(user);
        settleMaybe();
        return s;
    }

    private void settleMaybe(){
        if(s.finished.size() < s.hands.size()) return;
        // 모든 플레이어가 끝났으면 딜러 진행
        while(s.dealer.total < 17){
            s.dealer.cards.add(deck.draw());
            s.dealer.total = bestTotal(s.dealer.cards);
        }
        // 정산
        int total=0;
        for(Hand h: s.hands.values()){
            int d=0;
            if(h.total>21) d = -s.bet;
            else if(s.dealer.total>21) d = s.bet;
            else if(h.total > s.dealer.total) d = s.bet;
            else if(h.total < s.dealer.total) d = -s.bet;
            total += d;
        }
        s.deltaTotal = total;
        s.inProgress=false;
    }
}
