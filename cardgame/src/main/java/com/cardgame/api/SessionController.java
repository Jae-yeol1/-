// com/cardgame/api/SessionController.java
package com.cardgame.api;

import org.springframework.web.bind.annotation.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/session")
@CrossOrigin(origins="*")
public class SessionController {
    private static final Map<String, Integer> BAL = new ConcurrentHashMap<>();

    public static int get(String userId){
        return BAL.getOrDefault(userId, 0);
    }
    public static void set(String userId, int balance){
        if (balance <= 0) BAL.remove(userId);
        else BAL.put(userId, balance);
    }

    @PostMapping("/start")
    public Map<String, Object> start(@RequestParam String userId, @RequestParam(defaultValue="1000") int init){
        if (init < 0) init = 0;
        BAL.put(userId, init);
        Map<String, Object> res = new java.util.HashMap<>();
        res.put("ok", true);
        res.put("balance", init);
        return res;
    }

    @GetMapping("/balance")
    public Map<String, Object> balance(@RequestParam String userId){
        Map<String, Object> res = new java.util.HashMap<>();
        res.put("ok", true);
        res.put("balance", get(userId));
        return res;
    }

    @PostMapping("/reset")
    public Map<String, Object> reset(@RequestParam String userId){
        BAL.remove(userId);
        Map<String, Object> res = new java.util.HashMap<>();
        res.put("ok", true);
        res.put("balance", 0);
        return res;
    }
}