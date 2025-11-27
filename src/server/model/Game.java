package server.model;

import com.google.gson.*;
import java.util.*;

public class Game {
    private final Room room;
    private final List<String> deck = new ArrayList<>();
    private int currentIdx = 0;
    private List<String> lastPlay = null;
    private final Gson gson;
    private int consecutivePass = 0;

    public Game(Room room, Gson gson) {
        this.room = room;
        this.gson = gson;
    }

    public void start() {
        initDeck();
        Collections.shuffle(deck);

        List<String> playerNames = new ArrayList<>();
        for (Player pl : room.getPlayers()) {
            playerNames.add(pl.getName() == null ? "Player" : pl.getName());
        }

        // TÌM NGƯỜI ĐÁNH TRƯỚC (MẶC ĐỊNH P1)
        currentIdx = 0;

        for (int i = 0; i < room.getPlayers().size(); i++) {
            Player p = room.getPlayers().get(i);
            p.getHand().clear();

            // Chia 13 lá
            for (int j = 0; j < 13; j++) {
                if (!deck.isEmpty()) {
                    String cardCode = deck.remove(0);
                    Card card = new Card(cardCode);
                    p.getHand().add(card);
                }
            }

            // TÌM NGƯỜI CÓ 3 BÍCH
            if (p.getHand().stream().anyMatch(c -> c.getCode().equals("3C"))) {
                currentIdx = i;
            }

            // GỬI GAME_START
            JsonObject payload = new JsonObject();
            payload.add("yourCards", gson.toJsonTree(p.getHandAsStringList()));
            payload.add("playerNames", gson.toJsonTree(playerNames));
            payload.addProperty("position", "p" + (i + 1));
            payload.addProperty("firstPlayerId", room.getPlayers().get(currentIdx).getId());

            JsonObject msg = new JsonObject();
            msg.addProperty("type", "GAME_START");
            msg.add("payload", payload);
            p.getOut().println(gson.toJson(msg));
        }

        lastPlay = null;
        consecutivePass = 0;
        broadcastState();
    }

    private void initDeck() {
        deck.clear();
        String[] ranks = {"3","4","5","6","7","8","9","10","J","Q","K","A","2"};
        String[] suits = {"C","D","H","S"};
        for (String r : ranks) for (String s : suits) deck.add(r + s);
    }

    public void processPlay(Player p, List<String> cards) {
        if (room.getPlayers().get(currentIdx) != p) {
            sendPlayResult(p, false, "Không phải lượt của bạn!");
            return;
        }

        // KIỂM TRA CÓ BÀI KHÔNG
        List<String> playerHandCodes = p.getHandAsStringList();
        if (!playerHandCodes.containsAll(cards)) {
            sendPlayResult(p, false, "Bạn không có những lá bài này!");
            return;
        }

        // XÓA BÀI
        removeCardsFromPlayer(p, cards);
        lastPlay = new ArrayList<>(cards);
        consecutivePass = 0;

        sendPlayResult(p, true, "Đánh thành công!");

        // KIỂM TRA THẮNG
        if (p.getHand().isEmpty()) {
            room.broadcast("GAME_END", gson.toJsonTree(Map.of(
                "winner", p.getName(),
                "message", p.getName() + " đã về nhất!"
            )));
            room.setState(RoomState.WAITING);
            return;
        }

        currentIdx = (currentIdx + 1) % room.getPlayers().size();
        broadcastState();
    }

    public void processPass(Player p) {
        if (room.getPlayers().get(currentIdx) != p) {
            sendPlayResult(p, false, "Không phải lượt của bạn!");
            return;
        }

        consecutivePass++;
        if (consecutivePass >= 3) {
            lastPlay = null;
            consecutivePass = 0;
            room.broadcast("NEW_ROUND", gson.toJsonTree(Map.of("message", "Bàn mới - Đánh tự do!")));
        }

        currentIdx = (currentIdx + 1) % room.getPlayers().size();
        broadcastState();
    }

    private void removeCardsFromPlayer(Player p, List<String> cards) {
        List<Card> toRemove = new ArrayList<>();
        for (String cardCode : cards) {
            for (Card c : p.getHand()) {
                if (c.getCode().equals(cardCode)) {
                    toRemove.add(c);
                    break;
                }
            }
        }
        p.getHand().removeAll(toRemove);
    }

    private void sendPlayResult(Player p, boolean success, String message) {
        JsonObject payload = new JsonObject();
        payload.addProperty("success", success);
        payload.addProperty("message", message);

        JsonObject msg = new JsonObject();
        msg.addProperty("type", "PLAY_RESULT");
        msg.add("payload", payload);
        p.getOut().println(gson.toJson(msg));
    }

    public void broadcastState() {
        JsonObject payload = new JsonObject();
        payload.addProperty("currentPlayerId", room.getPlayers().get(currentIdx).getId());
        payload.add("lastPlay", gson.toJsonTree(lastPlay));

        JsonObject handSizes = new JsonObject();
        for (Player pl : room.getPlayers()) {
            handSizes.addProperty(pl.getId(), pl.getHand().size());
        }
        payload.add("handSizes", handSizes);

        room.broadcast("GAME_STATE", payload);
    }
}