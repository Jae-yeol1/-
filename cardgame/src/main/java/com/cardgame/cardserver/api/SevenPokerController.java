package com.cardgame.cardserver.api;
import com.cardgame.cardserver.core.*; import com.cardgame.cardserver.util.ApiResponse;
import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/seven")
public class SevenPokerController {
    private final Map<String,SevenPokerGame.State> rooms = new HashMap<>();
    private final SevenPokerGame game = new SevenPokerGame();
    private SevenPokerGame.State st(String roomId){ return rooms.computeIfAbsent(roomId, k-> new SevenPokerGame.State()); }
    private static Map<String,Object> dto(Card c){ return Map.of("rank", c.rankStr(), "suit", c.suitStr()); }
    private static List<Map<String,Object>> maskFor(String viewer, Map<String,SevenPokerGame.Side> players){
        List<Map<String,Object>> out=new ArrayList<>();
        for(var e: players.entrySet()){
            String uid=e.getKey(); var cards=e.getValue().cards;
            List<Map<String,Object>> show=new ArrayList<>();
            for(int i=0;i<cards.size();i++){
                if(!viewer.equals(uid) && (i<2 || i==6)) show.add(Map.of("rank","BACK","suit",""));
                else show.add(dto(cards.get(i)));
            }
            out.add(Map.of("user", uid, "cards", show));
        }
        return out;
    }
    @PostMapping("/start")
    public Object start(@RequestParam String roomId, @RequestParam String users, @RequestParam int ante){
        var list = Arrays.asList(users.split(","));
        var s = st(roomId);
        game.start(s, list, ante);
        return ApiResponse.of("ok", true).detail(Map.of("inProgress", s.inProgress, "stage", s.stage));
    }
    @GetMapping("/state")
    public Object state(@RequestParam String roomId, @RequestParam String viewer){
        var s = st(roomId);
        Map<String,Object> d=new LinkedHashMap<>();
        d.put("inProgress", s.inProgress); d.put("stage", s.stage); d.put("pot", s.pot);
        d.put("players", maskFor(viewer, s.players));
        return ApiResponse.of("ok", true).detail(d);
    }
    @PostMapping("/bet") public Object bet(@RequestParam String roomId, @RequestParam String user, @RequestParam int amount){
        var s=st(roomId); game.bet(s,user,amount); return ApiResponse.of("ok", true).detail(Map.of("pot", s.pot, "bets", s.bets));
    }
    @PostMapping("/check") public Object check(@RequestParam String roomId, @RequestParam String user){
        var s=st(roomId); game.check(s,user); return ApiResponse.of("ok", true);
    }
    @PostMapping("/fold") public Object fold(@RequestParam String roomId, @RequestParam String user){
        var s=st(roomId); game.fold(s,user); return ApiResponse.of("ok", true).detail(Map.of("remain", s.players.keySet()));
    }
    @PostMapping("/next") public Object next(@RequestParam String roomId){
        var s=st(roomId); game.next(s); return ApiResponse.of("ok", true).detail(Map.of("stage", s.stage, "inProgress", s.inProgress));
    }
}
