package client.controller;

import com.google.gson.JsonObject;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import client.network.NetworkClient;
import client.view.RoomView;

public class RoomController {
    private NetworkClient client;
    private AppController app;
    private RoomView view;

    public RoomController(NetworkClient client, AppController app) {
        this.client = client;
        this.app = app;
        client.addHandler("ERROR", this::handleError);

        client.addHandler("ROOM_UPDATE", this::handleRoomUpdate);
        client.addHandler("ROOM_CREATED", this::handleRoomCreated);
    }

    static class CreateRoomPayload {
    String roomName;
    CreateRoomPayload(String n) { this.roomName = n; }
    }

    static class JoinRoomPayload {
    String roomId;
    JoinRoomPayload(String id) { this.roomId = id; }
    }


    public void setView(RoomView view) {
        this.view = view;
    }

    public void createRoom(String name) {
    client.send("CREATE_ROOM", new CreateRoomPayload(name));
    }

    public void joinRoom(String roomId) {
    client.send("JOIN_ROOM", new JoinRoomPayload(roomId));
    }

    public void ready() {
        client.send("READY", new Object(){});
    }

    private void handleRoomUpdate(JsonObject msg) {
    JsonObject payload = msg.getAsJsonObject("payload");
    System.out.println("📊 ROOM_UPDATE: " + payload);
    
    Platform.runLater(() -> {
        // Cập nhật danh sách người chơi trong phòng
        // view.updatePlayers(payload);  // Nếu có method này
        
        // Kiểm tra đủ 4 người → tự động READY
        int playerCount = payload.get("players").getAsJsonArray().size();
        if (playerCount >= 1) {  // Test 1 người trước
            System.out.println("✅ Phòng đủ người → READY!");
            ready();  // Tự động READY
        }
    });
}

    public void handleMessage(JsonObject msg) {
        String type = msg.get("type").getAsString();
        switch (type) {
            case "ROOM_UPDATE":
                handleRoomUpdate(msg);
                break;
            case "ROOM_CREATED":
                handleRoomCreated(msg);
                break;
            case "ERROR":
                handleError(msg);
                break;
        }
    }

    private void handleError(JsonObject msg) {
        String error = msg.getAsJsonObject("payload").get("message").getAsString();
        System.out.println("❌ LỖI: " + error);
        
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể vào phòng!");
            alert.setContentText(error);
            alert.showAndWait();
        });
    }


    private void handleRoomCreated(JsonObject msg) {
        String roomId = msg.getAsJsonObject("payload").get("roomId").getAsString();
        System.out.println("Tạo phòng thành công, ID = " + roomId);

        // Hiển thị lên giao diện RoomView (ví dụ điền sẵn vào ô roomIdField)
        Platform.runLater(() -> view.setRoomId(roomId));
    }

}
