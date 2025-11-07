package com.cardgame.cardserver.core;
public record Card(Rank rank, Suit suit){
    public enum Suit { C, D, H, S }
    public enum Rank { TWO,THREE,FOUR,FIVE,SIX,SEVEN,EIGHT,NINE,TEN,JACK,QUEEN,KING,ACE }
    public int bjValue(){ return switch(rank){
        case ACE -> 11; case KING,QUEEN,JACK,TEN -> 10; default -> (rank.ordinal()+2);
    };}
    public int pip(){ return switch(rank){ case ACE->1; case TEN,JACK,QUEEN,KING->0; default->(rank.ordinal()+2); }; }
    public String rankStr(){ return switch(rank){
        case ACE->"ACE"; case KING->"KING"; case QUEEN->"QUEEN"; case JACK->"JACK"; case TEN->"10";
        case NINE->"9"; case EIGHT->"8"; case SEVEN->"7"; case SIX->"6"; case FIVE->"5"; case FOUR->"4"; case THREE->"3"; case TWO->"2";
    };}
    public String suitStr(){ return switch(suit){ case C->"CLUBS"; case D->"DIAMONDS"; case H->"HEARTS"; case S->"SPADES"; }; }
}
