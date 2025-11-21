package client.controller;

import client.network.NetworkClient;
import client.view.RoomView;
import com.google.gson.JsonObject;
import javafx.application.Platform;

public class RoomController {
    private NetworkClient client;
    private AppController app;
    private RoomView view;

    public RoomController(NetworkClient client, AppController app) {
        this.client = client;
        this.app = app;

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
        // JsonObject payload = msg.get("payload").getAsJsonObject();
        // Platform.runLater(() -> view.updatePlayers(payload));
    }


    private void handleRoomCreated(JsonObject msg) {
        String roomId = msg.getAsJsonObject("payload").get("roomId").getAsString();
        System.out.println("Tạo phòng thành công, ID = " + roomId);

        // Hiển thị lên giao diện RoomView (ví dụ điền sẵn vào ô roomIdField)
        Platform.runLater(() -> view.setRoomId(roomId));
    }

}
