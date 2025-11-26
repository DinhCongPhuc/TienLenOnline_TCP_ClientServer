package client.controller;

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

    private String myPosition = "p1";  // vị trí server gán
    private final List<String> playerNames = new ArrayList<>();
    private final List<String> initialCards = new ArrayList<>();

    public GameController(NetworkClient client, AppController app, JsonObject payload) {
        this.client = client;
        this.app = app;

        // ----- Load payload đầu trận -----
        if (payload != null) {
            if (payload.has("position")) {
                myPosition = payload.get("position").getAsString();
            }

            if (payload.has("playerNames")) {
                JsonArray arr = payload.getAsJsonArray("playerNames");
                for (int i = 0; i < arr.size(); i++) {
                    playerNames.add(arr.get(i).getAsString());
                }
            }

            if (payload.has("yourCards")) {
                JsonArray arr = payload.getAsJsonArray("yourCards");
                for (int i = 0; i < arr.size(); i++) {
                    initialCards.add(arr.get(i).getAsString());
                }
            }
        }

        // ----- Đăng ký lắng nghe server -----
        // client.addHandler("GAME_STATE", this::handleGameState);
        // client.addHandler("PLAY_RESULT", this::handlePlayResult);
        // client.addHandler("CHAT_BROADCAST", this::handleChatBroadcast);
    }

    public void setView(GameView view) {
        this.view = view;

        // ----- Set tên người chơi -----
        if (!playerNames.isEmpty()) {
            Platform.runLater(() -> view.setPlayerNames(playerNames, myPosition));
        }

        // ----- Set bài trên tay -----
        if (!initialCards.isEmpty()) {
            Platform.runLater(() -> view.setHand(initialCards));
        }
    }

    /* ==========================
       GỬI HÀNH ĐỘNG LÊN SERVER
       ========================== */

    public void playCards(List<String> cards) {
        client.send("PLAY_CARDS", new PlayPayload(cards));
        // Xóa bài trên tay tạm thời (UI sẽ tự update khi server phản hồi)
        Platform.runLater(() -> view.removeCardsFromHand(cards));
    }

    public void pass() {
        client.send("PASS", new Object() {});
    }

    public void sendChat(String text) {
        client.send("CHAT", new ChatPayload(text));
    }

    /* ==========================
       XỬ LÝ TIN NHẮN SERVER
       ========================== */

    // private void handleGameState(JsonObject msg) {
    //     if (view == null) return;
    //     JsonObject p = msg.getAsJsonObject("payload");
    //     Platform.runLater(() -> view.updateState(p));
    // }

    // private void handlePlayResult(JsonObject msg) {
    //     if (view == null) return;
    //     JsonObject p = msg.getAsJsonObject("payload");
    //     boolean success = p.get("success").getAsBoolean();
    //     String message = p.get("message").getAsString();
    //     Platform.runLater(() -> view.showPlayResult(success, message));
    // }

    // private void handleChatBroadcast(JsonObject msg) {
    //     if (view == null) return;
    //     JsonObject p = msg.getAsJsonObject("payload");
    //     String from = p.get("from").getAsString();
    //     String text = p.get("text").getAsString();
    //     Platform.runLater(() -> view.appendChat(from, text));
    // }

    /* ==========================
       GETTER
       ========================== */
    public String getMyPosition() { return myPosition; }
    public List<String> getPlayerNames() { return playerNames; }

    /* ==========================
       Payload class
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
