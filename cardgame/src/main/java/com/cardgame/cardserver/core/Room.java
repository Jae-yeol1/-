package com.cardgame.cardserver.core;
import java.util.*;
public class Room {
    public enum Game { BLACKJACK, BACCARAT, SEVEN }
    public final String id; public final Game game; public final int decks;
    public final Set<String> players = new LinkedHashSet<>(); public String host;
    public final Map<String,Object> state = new HashMap<>();
    public Room(String id, Game game, int decks, String host){
        this.id=id; this.game=game; this.decks=Math.max(1,decks); this.host=host; players.add(host);
    }
}
