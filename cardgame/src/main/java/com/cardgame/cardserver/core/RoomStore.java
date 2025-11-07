package com.cardgame.cardserver.core;
import java.util.*; import java.util.concurrent.ConcurrentHashMap;
public class RoomStore {
    private static final Map<String,Room> ROOMS = new ConcurrentHashMap<>();
    public static Room create(Room.Game g, int decks, String host){
        String id = String.valueOf(System.currentTimeMillis()).substring(8) + new Random().nextInt(90);
        Room r = new Room(id, g, decks, host);
        ROOMS.put(id, r); return r;
    }
    public static Collection<Room> list(){ return ROOMS.values(); }
    public static Room get(String id){ return ROOMS.get(id); }
    public static boolean join(String id, String user){ Room r=get(id); if(r==null) return false; r.players.add(user); return true; }
    public static void leave(String id, String user){ Room r=get(id); if(r!=null) r.players.remove(user); }
}
