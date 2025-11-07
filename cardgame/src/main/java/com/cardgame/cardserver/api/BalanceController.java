package com.cardgame.cardserver.api;
import com.cardgame.cardserver.core.SessionStore;
import com.cardgame.cardserver.util.ApiResponse;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/balance")
public class BalanceController {
    @GetMapping public Object get(@RequestParam String user){
        int bal = SessionStore.get(user);
        return ApiResponse.of("ok", true).detail(Map.of("balance", bal));
    }
    @PostMapping("/set") public Object set(@RequestParam String user, @RequestParam int amount){
        int v=SessionStore.set(user, amount);
        return ApiResponse.of("ok", true).detail(Map.of("balance", v));
    }
}
