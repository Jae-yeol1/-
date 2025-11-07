package com.cardgame.cardserver.core;
import java.util.*;
public class SevenPokerGame {
    public static class Side { public final List<Card> cards=new ArrayList<>(); }
    public static class State {
        public boolean inProgress; public int stage;
        public int pot; public Map<String,Integer> bets = new LinkedHashMap<>();
        public final Map<String,Side> players = new LinkedHashMap<>();
        public String lastActor;
    }
    private final Deck deck = new Deck(1);
    public State start(State s, Collection<String> users, int ante){
        s.inProgress=true; s.stage=0; s.pot=0; s.bets.clear(); s.players.clear();
        for(String u: users){ s.players.put(u, new Side()); s.bets.put(u, 0); }
        for(String u: users){ draw(s.players.get(u), 3); }
        for(String u: users){ s.pot += ante; s.bets.put(u, ante); }
        return s;
    }
    public State next(State s){
        if(!s.inProgress) return s;
        if(s.stage<4){
            for(String u: s.players.keySet()){
                s.players.get(u).cards.add(deck.draw());
            }
            s.stage++;
        } else { s.inProgress=false; }
        return s;
    }
    public State bet(State s, String user, int amount){
        s.pot += amount; s.bets.put(user, s.bets.getOrDefault(user,0)+amount); s.lastActor=user; return s;
    }
    public State check(State s, String user){ s.lastActor=user; return s; }
    public State fold(State s, String user){ s.players.remove(user); s.bets.remove(user); s.lastActor=user; if(s.players.size()<=1) s.inProgress=false; return s; }
    private void draw(Side side, int n){ for(int i=0;i<n;i++) side.cards.add(deck.draw()); }
}
