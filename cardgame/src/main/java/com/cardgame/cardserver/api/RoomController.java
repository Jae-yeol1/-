package com.cardgame.cardserver.api;
import com.cardgame.cardserver.core.*;
import com.cardgame.cardserver.util.ApiResponse;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/rooms")
public class RoomController {
    @GetMapping public Object list(){ return ApiResponse.of("ok", true).detail(RoomStore.list()); }
    @PostMapping("/create")
    public Object create(@RequestParam String user, @RequestParam String game, @RequestParam int decks){
        Room.Game g = Room.Game.valueOf(game.toUpperCase());
        Room r = RoomStore.create(g, decks, user);
        return ApiResponse.of("ok", true).detail(r);
    }
    @PostMapping("/join")
    public Object join(@RequestParam String user, @RequestParam String roomId){
        boolean ok = RoomStore.join(roomId, user);
        return ApiResponse.of("ok", ok).detail(RoomStore.get(roomId));
    }
}
