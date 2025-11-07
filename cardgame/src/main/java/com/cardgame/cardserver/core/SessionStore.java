package com.cardgame.cardserver.core;
import java.util.Map; import java.util.concurrent.ConcurrentHashMap;
/** TODO(DB): replace with persistent store */
public class SessionStore {
    private static final Map<String,Integer> BAL = new ConcurrentHashMap<>();
    public static int get(String user){ return BAL.getOrDefault(user, 10000); }
    public static int set(String user, int val){ BAL.put(user,val); return val; }
    public static int add(String user, int delta){ int v=get(user)+delta; BAL.put(user,v); return v; }
}
