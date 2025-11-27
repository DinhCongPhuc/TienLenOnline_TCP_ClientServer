package client.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import client.network.NetworkClient;
import client.view.GameView;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

public class GameController {
    private final NetworkClient client;
    private final AppController app;
    private GameView view;
    private final Gson gson = new Gson();

    // ⭐ GAME STATE
    private String myPosition = "p1";
    private final List<String> playerNames = new ArrayList<>();
    private final List<String> initialCards = new ArrayList<>();
    private List<String> selectedCards = new ArrayList<>();
    private String myPlayerId;

    public GameController(NetworkClient client, AppController app, JsonObject payload) {
        this.client = client;
        this.app = app;

        // Load initial payload (nếu có)
        if (payload != null) {
            initializeFromPayload(payload);
        }
    }

    /** Khởi tạo từ payload ban đầu */
    private void initializeFromPayload(JsonObject payload) {
        if (payload.has("position")) myPosition = payload.get("position").getAsString();
        if (payload.has("playerNames")) {
            JsonArray arr = payload.getAsJsonArray("playerNames");
            playerNames.clear();
            for (int i = 0; i < arr.size(); i++) playerNames.add(arr.get(i).getAsString());
        }
    }

    /** ⭐ METHOD QUAN TRỌNG NHẤT - NHẬN MESSAGE TỪ SERVER */
    public void handleMessage(JsonObject msg) {
        String type = msg.get("type").getAsString();
        System.out.println("🎮 [GAME] Nhận: " + type);
        
        switch (type) {
            case "GAME_START": onGameStart(msg.getAsJsonObject("payload")); break;
            case "GAME_STATE": onGameState(msg.getAsJsonObject("payload")); break;
            case "PLAY_RESULT": onPlayResult(msg.getAsJsonObject("payload")); break;
            case "GAME_END": onGameEnd(msg.getAsJsonObject("payload")); break;
            case "NEW_ROUND": onNewRound(msg.getAsJsonObject("payload")); break;
            default: System.out.println("⚠️ Unknown: " + type);
        }
    }

    /** ⭐ GAME START - NHẬN 13 LÁ BÀI THẬT */
    private void onGameStart(JsonObject payload) {
        System.out.println("🚀 [GAME_START] Bắt đầu ván mới!");
        
        // Clear old data
        initialCards.clear();
        playerNames.clear();
        selectedCards.clear();
        
        // Parse yourCards
        JsonArray cardsArray = payload.getAsJsonArray("yourCards");
        for (int i = 0; i < cardsArray.size(); i++) {
            initialCards.add(cardsArray.get(i).getAsString());
        }
        
        // Parse playerNames
        JsonArray namesArray = payload.getAsJsonArray("playerNames");
        for (int i = 0; i < namesArray.size(); i++) {
            playerNames.add(namesArray.get(i).getAsString());
        }
        
        myPosition = payload.get("position").getAsString();
        myPlayerId = payload.get("firstPlayerId").getAsString();
        
        System.out.println("✅ [GAME_START] Nhận " + initialCards.size() + " lá | Vị trí: " + myPosition);
        
        // Update UI
        Platform.runLater(() -> {
            view.setPlayerNames(playerNames, myPosition);
            view.setHand(initialCards);
            view.clearSelection();
        });
    }

    /** ⭐ GAME STATE - CẬP NHẬT LƯỢT + SỐ BÀI */
    private void onGameState(JsonObject payload) {
        String currentPlayerId = payload.get("currentPlayerId").getAsString();
        JsonArray lastPlayArray = payload.getAsJsonArray("lastPlay");
        
        List<String> lastPlay = new ArrayList<>();
        if (lastPlayArray != null) {
            for (int i = 0; i < lastPlayArray.size(); i++) {
                lastPlay.add(lastPlayArray.get(i).getAsString());
            }
        }
        
        // Parse hand sizes
        JsonObject handSizes = payload.getAsJsonObject("handSizes");
        
        Platform.runLater(() -> {
            view.updateGameState(currentPlayerId, lastPlay, handSizes);
        });
    }

    /** ⭐ PLAY RESULT - KẾT QUẢ ĐÁNH/BỎ LƯỢT */
    private void onPlayResult(JsonObject payload) {
        boolean success = payload.get("success").getAsBoolean();
        String message = payload.get("message").getAsString();
        
        System.out.println("🎯 [PLAY_RESULT] " + (success ? "✅" : "❌") + " " + message);
        
        Platform.runLater(() -> {
            view.showPlayResult(success, message);
            if (success) {
                view.clearSelection();
            }
        });
    }

    /** ⭐ GAME END - KẾT THÚC VÁN */
    private void onGameEnd(JsonObject payload) {
        String winner = payload.get("winner").getAsString();
        String message = payload.get("message").getAsString();
        
        System.out.println("🏆 [GAME_END] " + message);
        
        Platform.runLater(() -> view.showGameEnd(winner, message));
    }

    /** ⭐ NEW ROUND - BÀN MỚI */
    private void onNewRound(JsonObject payload) {
        String message = payload.get("message").getAsString();
        System.out.println("🔄 [NEW_ROUND] " + message);
        Platform.runLater(() -> view.showNewRound(message));
    }

    /* ==========================
       GỬI HÀNH ĐỘNG LÊN SERVER
       ========================== */

    /** Đánh bài */
    public void playCards(List<String> cards) {
        selectedCards.clear();
        selectedCards.addAll(cards);
        
        JsonObject payload = new JsonObject();
        payload.add("cards", gson.toJsonTree(cards));
        
        System.out.println("🃏 Đánh " + cards.size() + " lá: " + cards);
        client.send("PLAY", payload);
    }

    /** Bỏ lượt */
    public void pass() {
        System.out.println("⏭️ Bỏ lượt");
        client.send("PASS", new JsonObject());
    }

    /** Chọn/bỏ chọn 1 lá */
    public void toggleCard(String cardCode) {
        if (selectedCards.contains(cardCode)) {
            selectedCards.remove(cardCode);
        } else {
            selectedCards.add(cardCode);
        }
        Platform.runLater(() -> view.updateSelection(selectedCards));
    }

    /** Chọn tất cả */
    public void selectAll() {
        selectedCards.clear();
        selectedCards.addAll(initialCards);
        Platform.runLater(() -> view.updateSelection(selectedCards));
    }

    /** Bỏ chọn tất cả */
    public void clearSelection() {
        selectedCards.clear();
        Platform.runLater(() -> view.updateSelection(selectedCards));
    }

    /* ==========================
       SET VIEW
       ========================== */
    public void setView(GameView view) {
        this.view = view;
        
        // Hiển thị ngay nếu đã có data
        if (!playerNames.isEmpty()) {
            Platform.runLater(() -> view.setPlayerNames(playerNames, myPosition));
        }
        if (!initialCards.isEmpty()) {
            Platform.runLater(() -> {
                System.out.println("✅ [SET VIEW] Hiển thị " + initialCards.size() + " lá bài!");
                view.setHand(initialCards);
            });
        }
    }

    /* ==========================
       GETTERS
       ========================== */
    public String getMyPosition() { return myPosition; }
    public List<String> getPlayerNames() { return playerNames; }
    public List<String> getInitialCards() { return initialCards; }
    public List<String> getSelectedCards() { return selectedCards; }
    public boolean isMyTurn(String currentPlayerId) { return currentPlayerId != null && myPlayerId != null && currentPlayerId.equals(myPlayerId); }

    /* ==========================
       PAYLOAD CLASSES
       ========================== */
    static class PlayPayload {
        List<String> cards;
        PlayPayload(List<String> cards) { this.cards = cards; }
    }

    static class ChatPayload {
        String text;
        ChatPayload(String text) { this.text = text; }
    }
}