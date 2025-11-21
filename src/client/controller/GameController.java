package client.controller;

import client.network.NetworkClient;
import client.view.GameView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

public class GameController {
    private NetworkClient client;
    private AppController app;
    private GameView view;
    private String myPosition = "p1"; // mặc định nếu payload null
    private List<String> playerNames = new ArrayList<>();

    // Constructor cho phép payload null
    public GameController(NetworkClient client, AppController app, JsonObject payload) {
        this.client = client;
        this.app = app;

        if (payload != null) {
            if (payload.has("position"))
                myPosition = payload.get("position").getAsString();

            if (payload.has("playerNames")) {
                JsonArray arr = payload.get("playerNames").getAsJsonArray();
                for (int i = 0; i < arr.size(); i++) playerNames.add(arr.get(i).getAsString());
            }
        }

        client.addHandler("GAME_STATE", this::handleGameState);
        client.addHandler("PLAY_RESULT", this::handlePlayResult);
        client.addHandler("CHAT_BROADCAST", this::handleChatBroadcast);
    }

    public void setView(GameView view) {
        this.view = view;
        if (!playerNames.isEmpty()) {
            Platform.runLater(() -> view.setPlayerNames(playerNames, myPosition));
        }
    }

    // Gửi hành động đánh bài
    public void playCards(List<String> cards) {
        client.send("PLAY_CARDS", new PlayPayload(cards));
    }

    // Gửi hành động bỏ lượt
    public void pass() {
        client.send("PASS", new Object(){});
    }

    // Gửi chat
    public void sendChat(String text) {
        client.send("CHAT", new ChatPayload(text));
    }

    // Xử lý update game state từ server
    private void handleGameState(JsonObject msg) {
        if (view == null) return;
        JsonObject payload = msg.getAsJsonObject("payload");
        Platform.runLater(() -> view.updateState(payload));
    }

    private void handlePlayResult(JsonObject msg) {
        if (view == null) return;
        JsonObject payload = msg.getAsJsonObject("payload");
        boolean success = payload.get("success").getAsBoolean();
        String message = payload.get("message").getAsString();
        Platform.runLater(() -> view.showPlayResult(success, message));
    }

    private void handleChatBroadcast(JsonObject msg) {
        if (view == null) return;
        JsonObject payload = msg.getAsJsonObject("payload");
        String from = payload.get("from").getAsString();
        String text = payload.get("text").getAsString();
        Platform.runLater(() -> view.appendChat(from, text));
    }

    // getters
    public String getMyPosition() { return myPosition; }
    public List<String> getPlayerNames() { return playerNames; }

    // Payload class
    static class PlayPayload { 
        List<String> cards; 
        PlayPayload(List<String> c){this.cards=c;} 
    }

    static class ChatPayload { 
        String text; 
        ChatPayload(String t){this.text=t;} 
    }
}
