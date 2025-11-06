// com/cardgame/api/SevenController.java
package com.cardgame.api;

import com.cardgame.card.Deck;
import com.cardgame.sevenpoker.SevenPoker;
import com.cardgame.sevenpoker.SevenStudFixedLimit;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/api/seven") @CrossOrigin(origins="*")
public class SevenController {
    private final Deck shoe;
    public SevenController(Deck shoe){ this.shoe = shoe; }

    // 간단 자동판(앤티만)
    @PostMapping("/simple")
    public Map<String,Object> simple(@RequestParam String userId, @RequestParam int ante){
        int bal = SessionController.get(userId);
        if (bal <= 0) return Map.of("ok", false, "msg", "잔액 없음");
        if (ante < 1)  return Map.of("ok", false, "msg", "베팅 최소 1");
        if (ante > bal) return Map.of("ok", false, "msg", "잔액 부족", "balance", bal);

        int delta = new SevenPoker().playOneRoundForBet(shoe, ante);
        int newBal = bal + delta; SessionController.set(userId, newBal);
        return Map.of("ok", true, "delta", delta, "balance", newBal);
    }

    // 고급판(레이즈 캡/올인/AI) — 스택=현재 잔액 사용
    @PostMapping("/stud")
    public Map<String,Object> stud(@RequestParam String userId, @RequestParam int ante){
        int bal = SessionController.get(userId);
        if (bal <= 0) return Map.of("ok", false, "msg", "잔액 없음");
        if (ante < 1)  return Map.of("ok", false, "msg", "베팅 최소 1");

        int delta = new SevenStudFixedLimit().playOneRoundForBet(shoe, ante, bal);
        int newBal = bal + delta; SessionController.set(userId, newBal);
        return Map.of("ok", true, "delta", delta, "balance", newBal);
    }
}
