// com/cardgame/api/BaccaratController.java
package com.cardgame.api;

import com.cardgame.card.Deck;
import com.cardgame.baccarat.Baccarat;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController @RequestMapping("/api/baccarat") @CrossOrigin(origins="*")
public class BaccaratController {
    private final Deck shoe;
    public BaccaratController(Deck shoe){ this.shoe = shoe; }

    @PostMapping("/round")
    public Map<String,Object> round(@RequestParam String userId,
                                    @RequestParam int bet,
                                    @RequestParam String target) {
        int bal = SessionController.get(userId);
        if (bal <= 0) return Map.of("ok", false, "msg", "잔액 없음");
        if (bet < 1)  return Map.of("ok", false, "msg", "베팅 최소 1");
        if (bet > bal) return Map.of("ok", false, "msg", "잔액 부족", "balance", bal);

        var game = new Baccarat();
        Baccarat.Bet on = switch (target.toUpperCase()) {
            case "PLAYER" -> Baccarat.Bet.PLAYER;
            case "BANKER" -> Baccarat.Bet.BANKER;
            case "TIE"    -> Baccarat.Bet.TIE;
            default -> Baccarat.Bet.PLAYER;
        };

        int delta = game.playOneRoundForBet(shoe, bet, on);
        int newBal = bal + delta; SessionController.set(userId, newBal);
        return Map.of("ok", true, "delta", delta, "balance", newBal);
    }
}
