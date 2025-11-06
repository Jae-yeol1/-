package com.cardgame.api;

public class ApiResponse<T> {
    public boolean ok;
    public int delta;
    public int balance;
    public T detail; // 라운드 상세

    public ApiResponse(boolean ok, int delta, int balance, T detail) {
        this.ok = ok; this.delta = delta; this.balance = balance; this.detail = detail;
    }
}
