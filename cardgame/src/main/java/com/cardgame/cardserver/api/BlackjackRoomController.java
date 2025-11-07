package com.cardgame.cardserver.api;

import com.cardgame.cardserver.core.*;
import com.cardgame.cardserver.util.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController @RequestMapping("/api/blackjack/room")
public class BlackjackRoomController {

    // roomId -> BlackjackRoom
    private final Map<String,BlackjackRoom> rooms = new HashMap<>();

    private BlackjackRoom getOrCreate(String roomId, int decks){
        return rooms.computeIfAbsent(roomId, k -> new BlackjackRoom(decks));
    }

    private static Map<String,Object> dto(Card c){ return Map.of("rank", c.rankStr(), "suit", c.suitStr()); }

    private static List<Map<String,Object>> maskHands(String me, Map<String, BlackjackRoom.Hand> all, boolean inProgress){
        List<Map<String,Object>> out = new ArrayList<>();
        for(var e: all.entrySet()){
            String uid = e.getKey();
            var h = e.getValue();
            List<Map<String,Object>> cards = new ArrayList<>();
            for(int i=0; i<h.cards.size(); i++){
                if(inProgress && !uid.equals(me) && i>=1){
                    cards.add(Map.of("rank","BACK","suit",""));
                }else{
                    cards.add(dto(h.cards.get(i)));
                }
            }
            out.add(Map.of("user", uid, "total", uid.equals(me)||!inProgress? h.total : null, "cards", cards));
        }
        return out;
    }

    @PostMapping("/start")
    public Object start(@RequestParam String roomId, @RequestParam String host, @RequestParam int bet, @RequestParam(defaultValue="4") int decks){
        Room r = RoomStore.get(roomId);
        if(r==null) return ApiResponse.of("ok", false).detail("no room");
        var game = getOrCreate(roomId, decks);
        var users = new ArrayList<>(r.players);
        var st = game.start(users, bet);
        return ApiResponse.of("ok", true).detail(Map.of(
                "inProgress", st.inProgress,
                "bet", st.bet,
                "dealer", Map.of(
                        "cards", List.of(dto(st.dealer.cards.get(0)), Map.of("rank","BACK","suit","")),
                        "total", null
                ),
                "hands", maskHands(host, st.hands, true)
        ));
    }

    @GetMapping("/state")
    public Object state(@RequestParam String roomId, @RequestParam String viewer){
        var g = rooms.get(roomId);
        if(g==null) return ApiResponse.of("ok", false).detail("no game");
        var st = g.s;
        var dealerCards = new ArrayList<Map<String,Object>>();
        if(st.inProgress){
            dealerCards.add(dto(st.dealer.cards.get(0)));
            dealerCards.add(Map.of("rank","BACK","suit",""));
        }else{
            for(Card c: st.dealer.cards) dealerCards.add(dto(c));
        }
        return ApiResponse.of("ok", true).detail(Map.of(
                "inProgress", st.inProgress,
                "bet", st.bet,
                "dealer", Map.of("cards", dealerCards, "total", st.inProgress?null:st.dealer.total),
                "hands", maskHands(viewer, st.hands, st.inProgress),
                "deltaTotal", st.deltaTotal
        ));
    }

    @PostMapping("/hit")
    public Object hit(@RequestParam String roomId, @RequestParam String user){
        var g = rooms.get(roomId);
        if(g==null) return ApiResponse.of("ok", false).detail("no game");
        var st = g.hit(user);
        return ApiResponse.of("ok", true).detail(Map.of("inProgress", st.inProgress));
    }

    @PostMapping("/stand")
    public Object stand(@RequestParam String roomId, @RequestParam String user){
        var g = rooms.get(roomId);
        if(g==null) return ApiResponse.of("ok", false).detail("no game");
        var st = g.stand(user);
        return ApiResponse.of("ok", true).detail(Map.of("inProgress", st.inProgress));
    }
}
