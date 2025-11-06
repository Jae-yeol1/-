package com.cardgame.blackjack;
import java.util.List;

public class BjDetail {
    public List<String> player;
    public int playerValue;
    public List<String> dealer;
    public int dealerValue;
    public String outcome;  // "WIN" | "LOSE" | "PUSH"
    public List<String> log;

    public BjDetail(List<String> player, int pv, List<String> dealer, int dv, String outcome, List<String> log){
        this.player=player; this.playerValue=pv; this.dealer=dealer; this.dealerValue=dv; this.outcome=outcome; this.log=log;
    }
}
