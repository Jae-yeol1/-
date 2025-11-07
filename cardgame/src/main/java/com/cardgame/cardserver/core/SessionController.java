package com.cardgame.cardserver.core;
/** Legacy shim */
public final class SessionController {
    private SessionController(){}
    public static int get(String u){ return SessionStore.get(u); }
    public static int set(String u,int v){ return SessionStore.set(u,v); }
    public static int add(String u,int d){ return SessionStore.add(u,d); }
}
