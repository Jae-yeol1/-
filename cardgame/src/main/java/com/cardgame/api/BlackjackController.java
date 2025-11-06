// com/cardgame/api/BlackjackController.java
package com.cardgame.api;

import com.cardgame.card.Deck;
import com.cardgame.blackjack.BlackJack;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController @RequestMapping("/api/blackjack") @CrossOrigin(origins="*")
public class BlackjackController {
    private final Deck shoe;
    public BlackjackController(Deck shoe){ this.shoe = shoe; }

    @PostMapping("/round")
    public Map<String,Object> round(@RequestParam String userId, @RequestParam int bet){
        int bal = SessionController.get(userId);
        if (bal <= 0) return Map.of("ok", false, "msg", "잔액 없음");
        if (bet < 1)  return Map.of("ok", false, "msg", "베팅 최소 1");
        if (bet > bal) return Map.of("ok", false, "msg", "잔액 부족", "balance", bal);

        int delta = new BlackJack().playOneRoundForBet(shoe, bet);
        int newBal = bal + delta; SessionController.set(userId, newBal);
        return Map.of("ok", true, "delta", delta, "balance", newBal);
    }
}
