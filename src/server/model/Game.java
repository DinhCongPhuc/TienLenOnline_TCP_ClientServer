package server.model;

import com.google.gson.*;
import java.util.*;

public class Game {
    private Room room;
    private List<String> deck = new ArrayList<>();
    private int currentIdx = 0;
    private List<String> lastPlay = null;
    private Gson gson;

    public Game(Room room, Gson gson) {
        this.room = room;
        this.gson = gson;
    }

    // ===== Khởi động ván mới =====
    public void start() {
        initDeck();
        Collections.shuffle(deck);

        // Danh sách tên người chơi trong phòng
        List<String> playerNames = new ArrayList<>();
        for (Player pl : room.getPlayers()) {
            playerNames.add(pl.getName());
        }

        // Gửi bài + thông tin cho từng người
        for (int i = 0; i < room.getPlayers().size(); i++) {
            Player p = room.getPlayers().get(i);
            p.getHand().clear();

            // Chia 13 lá
            for (int j = 0; j < 13; j++) {
                if (!deck.isEmpty()) {
                    p.getHand().add(deck.remove(0));
                }
            }

            // Tạo payload
            JsonObject payload = new JsonObject();
            payload.add("yourCards", gson.toJsonTree(p.getHand()));  // Bài của người chơi
            payload.addProperty("firstPlayer", room.getPlayers().get(0).getId());
            payload.addProperty("position", "p" + (i + 1));          // Vị trí p1/p2/p3/p4
            payload.add("playerNames", gson.toJsonTree(playerNames)); // Danh sách tên thật

            // Tạo message GAME_START
            JsonObject msg = new JsonObject();
            msg.addProperty("type", "GAME_START");
            msg.add("payload", payload);

            // Gửi tới từng client
            p.getOut().println(gson.toJson(msg));
        }

        // Đặt lượt đầu tiên
        currentIdx = 0;
        broadcastState();
    }


    private void initDeck() {
        String[] ranks = {"3","4","5","6","7","8","9","10","J","Q","K","A","2"};
        String[] suits = {"C","D","H","S"};
        deck.clear();
        for (String r : ranks) {
            for (String s : suits) {
                deck.add(r+s);
            }
        }
    }

    // ===== Người chơi đánh bài =====
    public void processPlay(Player p, List<String> cards) {
        if (room.getPlayers().get(currentIdx) != p) {
            sendPlayResult(p, false, "Không phải lượt của bạn");
            return;
        }
        if (!p.getHand().containsAll(cards)) {
            sendPlayResult(p, false, "Bạn không có những lá này");
            return;
        }

        // (Chưa kiểm tra luật – cho phép đánh bất kỳ bộ nào)
        p.getHand().removeAll(cards);
        lastPlay = new ArrayList<>(cards);

        sendPlayResult(p, true, "Đánh thành công");
        broadcastState();

        // Kiểm tra thắng
        if (p.getHand().isEmpty()) {
            room.broadcast("GAME_END", gson.toJsonTree(Map.of("winner", p.getName())));
            room.setState(RoomState.WAITING);
            return;
        }

        currentIdx = (currentIdx + 1) % room.getPlayers().size();
    }

    private void sendPlayResult(Player p, boolean success, String message) {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "PLAY_RESULT");
        JsonObject payload = new JsonObject();
        payload.addProperty("success", success);
        payload.addProperty("message", message);
        msg.add("payload", payload);
        p.getOut().println(gson.toJson(msg));
    }

    // ===== Người chơi bỏ lượt =====
    public void processPass(Player p) {
        if (room.getPlayers().get(currentIdx) != p) {
            p.getOut().println(gson.toJson(Map.of(
                "type","PLAY_RESULT",
                "payload", Map.of("success", false, "message", "Không phải lượt của bạn")
            )));
            return;
        }
        currentIdx = (currentIdx + 1) % room.getPlayers().size();
        broadcastState();
    }

    // ===== Gửi trạng thái cho tất cả player =====
    public void broadcastState() {
        Map<String,Object> payload = new HashMap<>();
        payload.put("currentPlayer", room.getPlayers().get(currentIdx).getId());
        payload.put("lastPlay", lastPlay);

        Map<String,Integer> sizes = new HashMap<>();
        for (Player pl : room.getPlayers()) {
            sizes.put(pl.getId(), pl.getHand().size());
        }
        payload.put("handsSizes", sizes);

        room.broadcast("GAME_STATE", gson.toJsonTree(payload));
    }
}

