package com.cardgame.cardserver.api;

import com.cardgame.cardserver.core.Card;
import com.cardgame.cardserver.core.SevenPokerGame;
import com.cardgame.cardserver.util.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/seven")
public class SevenPokerController {

    private final Map<String, SevenPokerGame.State> rooms = new ConcurrentHashMap<>();
    private final SevenPokerGame game = new SevenPokerGame();

    private SevenPokerGame.State ensureState(String roomId) {
        return rooms.computeIfAbsent(roomId, key -> new SevenPokerGame.State());
    }

    private static Map<String, Object> dto(Card card) {
        return Map.of(
                "rank", card.rankStr(),
                "suit", card.suitStr()
        );
    }

    private List<Map<String, Object>> maskFor(String viewer, SevenPokerGame.State state) {
        List<Map<String, Object>> out = new ArrayList<>();
        Set<String> handled = new HashSet<>();
        for (String uid : state.order) {
            SevenPokerGame.Side side = state.players.get(uid);
            if (side == null) {
                continue;
            }
            handled.add(uid);
            out.add(playerRow(viewer, state, uid, side));
        }
        for (Map.Entry<String, SevenPokerGame.Side> entry : state.players.entrySet()) {
            String uid = entry.getKey();
            if (handled.contains(uid)) {
                continue;
            }
            SevenPokerGame.Side side = entry.getValue();
            if (side == null) {
                continue;
            }
            out.add(playerRow(viewer, state, uid, side));
        }
        return out;
    }

    private Map<String, Object> playerRow(String viewer, SevenPokerGame.State state,
                                          String uid, SevenPokerGame.Side side) {
        List<Map<String, Object>> visible = new ArrayList<>();
        for (int i = 0; i < side.cards.size(); i++) {
            boolean hide = !Objects.equals(viewer, uid) && (i < 2 || i == 6);
            visible.add(hide ? Map.of("rank", "BACK", "suit", "") : dto(side.cards.get(i)));
        }
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("user", uid);
        row.put("cards", visible);
        row.put("bet", side.contributed);
        row.put("action", side.lastAction);
        row.put("actionAmount", side.lastAmount);
        row.put("folded", side.folded);
        row.put("ai", side.ai);
        row.put("toCall", game.toCall(state, uid));
        row.put("streetBet", state.streetContribution.getOrDefault(uid, 0));
        row.put("winner", side.winner);
        if (side.payout != null) {
            row.put("payout", side.payout);
        }
        if (side.showdownScore != null) {
            row.put("handRank", side.showdownScore.rank.name());
            row.put("handScore", side.showdownScore.toString());
        }
        if (side.profileName() != null) {
            row.put("profile", side.profileName());
        }
        return row;
    }

    @PostMapping("/start")
    public Object start(@RequestParam String roomId,
                        @RequestParam String users,
                        @RequestParam int ante) {
        List<String> list = Arrays.stream(users.split(","))
                .map(String::trim)
                .filter(u -> !u.isEmpty())
                .collect(Collectors.toList());
        SevenPokerGame.State state = ensureState(roomId);
        game.start(state, list, ante);
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("inProgress", state.inProgress);
        payload.put("stage", state.stageName);
        payload.put("round", state.stage);
        if (state.turn != null) {
            payload.put("turn", state.turn);
        }
        return ApiResponse.of("ok", true).detail(payload);
    }

    @GetMapping("/state")
    public Object state(@RequestParam String roomId, @RequestParam String viewer) {
        SevenPokerGame.State state = ensureState(roomId);
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("inProgress", state.inProgress);
        detail.put("stage", state.stageName);
        detail.put("round", state.stage);
        detail.put("turn", state.turn);
        detail.put("ante", state.ante);
        detail.put("pot", state.pot);
        detail.put("currentBet", state.currentBet);
        detail.put("minRaise", game.minRaise(state));
        detail.put("pending", game.pendingOrder(state));
        detail.put("winners", new ArrayList<>(state.winners));
        detail.put("payouts", new LinkedHashMap<>(state.payouts));
        detail.put("settledPot", state.settledPot);
        detail.put("players", maskFor(viewer, state));
        return ApiResponse.of("ok", true).detail(detail);
    }

    @PostMapping("/bet")
    public Object bet(@RequestParam String roomId,
                      @RequestParam String user,
                      @RequestParam int amount) {
        SevenPokerGame.State state = ensureState(roomId);
        game.bet(state, user, amount);
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("pot", state.pot);
        payload.put("currentBet", state.currentBet);
        if (state.turn != null) {
            payload.put("turn", state.turn);
        }
        return ApiResponse.of("ok", true).detail(payload);
    }

    @PostMapping("/call")
    public Object call(@RequestParam String roomId, @RequestParam String user) {
        SevenPokerGame.State state = ensureState(roomId);
        game.call(state, user);
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("pot", state.pot);
        payload.put("currentBet", state.currentBet);
        if (state.turn != null) {
            payload.put("turn", state.turn);
        }
        return ApiResponse.of("ok", true).detail(payload);
    }

    @PostMapping("/check")
    public Object check(@RequestParam String roomId, @RequestParam String user) {
        SevenPokerGame.State state = ensureState(roomId);
        game.check(state, user);
        Map<String, Object> payload = new LinkedHashMap<>();
        if (state.turn != null) {
            payload.put("turn", state.turn);
        }
        return ApiResponse.of("ok", true).detail(payload);
    }

    @PostMapping("/fold")
    public Object fold(@RequestParam String roomId, @RequestParam String user) {
        SevenPokerGame.State state = ensureState(roomId);
        game.fold(state, user);
        Map<String, Object> payload = new LinkedHashMap<>();
        if (state.turn != null) {
            payload.put("turn", state.turn);
        }
        payload.put("inProgress", state.inProgress);
        payload.put("winners", new ArrayList<>(state.winners));
        return ApiResponse.of("ok", true).detail(payload);
    }

    @PostMapping("/next")
    public Object next(@RequestParam String roomId) {
        SevenPokerGame.State state = ensureState(roomId);
        game.next(state);
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("stage", state.stage);
        payload.put("stageName", state.stageName);
        if (state.turn != null) {
            payload.put("turn", state.turn);
        }
        payload.put("inProgress", state.inProgress);
        return ApiResponse.of("ok", true).detail(payload);
    }
}
