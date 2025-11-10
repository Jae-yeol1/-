package com.cardgame.cardserver.api;

import com.cardgame.cardserver.core.Card;
import com.cardgame.cardserver.core.SevenPokerGame;
import com.cardgame.cardserver.util.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/seven")
public class SevenPokerController {

    private final Map<String, SevenPokerGame.State> rooms = new HashMap<>();
    private final SevenPokerGame game = new SevenPokerGame();

    private SevenPokerGame.State stateFor(String roomId){
        return rooms.computeIfAbsent(roomId, k -> new SevenPokerGame.State());
    }

    private static Map<String,Object> dto(Card c){
        return Map.of("rank", c.rankStr(), "suit", c.suitStr());
    }

    /** 상대의 프라이빗 카드(1, 2, 7번째)는 가린다 */
    private static List<Map<String,Object>> maskedCards(String viewer, String uid, List<Card> cards){
        List<Map<String,Object>> out = new ArrayList<>(cards.size());
        for(int i=0;i<cards.size();i++){
            boolean isPrivateSlot = (i < 2) || (i == 6); // 1번, 2번, 7번(0-index 기준 0,1,6)
            if(!viewer.equals(uid) && isPrivateSlot){
                out.add(Map.of("rank","BACK","suit",""));
            }else{
                out.add(dto(cards.get(i)));
            }
        }
        return out;
    }

    private static Map<String,Object> maskedPlayers(String viewer, Map<String,SevenPokerGame.Side> players){
        Map<String,Object> m = new LinkedHashMap<>();
        for (var e : players.entrySet()){
            String uid = e.getKey();
            SevenPokerGame.Side side = e.getValue();
            Map<String,Object> row = new LinkedHashMap<>();
            row.put("user", uid);
            row.put("cards", maskedCards(viewer, uid, side.cards));
            row.put("lastAction", side.lastAction);
            row.put("lastAmount", side.lastAmount);
            m.put(uid, row);
        }
        return m;
    }

    @PostMapping("/start")
    public Object start(@RequestParam String roomId,
                        @RequestParam String users, // 쉼표로 구분
                        @RequestParam(defaultValue = "10") int ante){
        SevenPokerGame.State s = stateFor(roomId);
        List<String> list = Arrays.stream(users.split(","))
                .map(String::trim).filter(v->!v.isEmpty()).toList();
        game.start(s, list, ante);
        return ApiResponse.of("ok", true).detail(Map.of(
                "inProgress", s.inProgress,
                "stage", s.stageName,
                "round", s.stage,
                "turn", s.turn
        ));
    }

    @GetMapping("/state")
    public Object state(@RequestParam String roomId, @RequestParam String viewer){
        var s = stateFor(roomId);
        Map<String,Object> d = new LinkedHashMap<>();
        d.put("inProgress", s.inProgress);
        d.put("stage", s.stageName);
        d.put("round", s.stage);
        d.put("turn", s.turn);
        d.put("ante", s.ante);
        d.put("pot", s.pot);
        d.put("players", maskedPlayers(viewer, s.players));
        return ApiResponse.of("ok", true).detail(d);
    }

    @PostMapping("/bet")
    public Object bet(@RequestParam String roomId, @RequestParam String user, @RequestParam int amount){
        SevenPokerGame.State s = stateFor(roomId);
        game.bet(s, user, amount);
        return ApiResponse.of("ok", true).detail(Map.of(
                "pot", s.pot,
                "turn", s.turn
        ));
    }

    @PostMapping("/check")
    public Object check(@RequestParam String roomId, @RequestParam String user){
        SevenPokerGame.State s = stateFor(roomId);
        game.check(s, user);
        return ApiResponse.of("ok", true).detail(Map.of(
                "turn", s.turn
        ));
    }

    @PostMapping("/fold")
    public Object fold(@RequestParam String roomId, @RequestParam String user){
        SevenPokerGame.State s = stateFor(roomId);
        game.fold(s, user);
        return ApiResponse.of("ok", true).detail(Map.of(
                "turn", s.turn
        ));
    }

    @PostMapping("/next")
    public Object next(@RequestParam String roomId){
        SevenPokerGame.State s = stateFor(roomId);
        game.next(s);
        return ApiResponse.of("ok", true).detail(Map.of(
                "stage", s.stage,
                "stageName", s.stageName,
                "turn", s.turn,
                "inProgress", s.inProgress
        ));
    }
}
