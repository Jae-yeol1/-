package com.cardgame.card;


public record Card(Suit suit, Rank rank) {
        public enum Suit {DIAMOND, CLUB, HEART, SPADE}
        public enum Rank {ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING}
    }

