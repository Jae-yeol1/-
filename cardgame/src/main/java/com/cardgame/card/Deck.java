package com.cardgame.card;

public interface Deck {
    Card draw(); // 뽑기
    boolean isEmpty(); // 다 뽑았나?
    int remaining(); // 남은 장수
}
