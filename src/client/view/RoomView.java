package client.view;

import client.controller.AppController;
import client.controller.RoomController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import java.util.UUID;

public class RoomView {

    private VBox root = new VBox(30);

    private TextField roomIdField = new TextField();
    private ComboBox<Integer> playerCountBox = new ComboBox<>();
    private Button createBtn = new Button("🚀 Tạo phòng & Chơi ngay");
    private Button joinBtn = new Button("➡ Vào phòng & Bắt đầu");

     private final AppController app;

    public RoomView(RoomController controller, AppController app) { // <-- nhận AppController từ bên ngoài
        this.app = app; // gán biến

        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #eef2ff, #ffffff);");

        Label title = new Label("🎮 Phòng Chơi");
        title.setFont(Font.font("Arial", 28));

        // ===== CARD TẠO PHÒNG =====
        VBox createCard = new VBox(15);
        createCard.setPadding(new Insets(25));
        createCard.setAlignment(Pos.CENTER_LEFT);
        createCard.setPrefWidth(400);
        createCard.setStyle("-fx-background-color: white; -fx-background-radius: 16;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 15, 0, 0, 4);");

        Label createLabel = new Label("🔧 Tạo phòng mới");
        createLabel.setFont(Font.font(18));

        roomIdField.setPromptText("Nhập Room ID (để trống = tự tạo)");
        roomIdField.setPrefHeight(35);

        playerCountBox.getItems().addAll(2, 3, 4);
        playerCountBox.setValue(4);
        playerCountBox.setPrefWidth(100);

        createBtn.setPrefWidth(Double.MAX_VALUE);
        createBtn.setStyle(btnStyle());

        createCard.getChildren().addAll(
                createLabel,
                new Label("Room ID:"), roomIdField,
                new Label("Số người chơi:"), playerCountBox,
                createBtn
        );

        // ===== CARD VÀO PHÒNG =====
        VBox joinCard = new VBox(15);
        joinCard.setPadding(new Insets(25));
        joinCard.setAlignment(Pos.CENTER_LEFT);
        joinCard.setPrefWidth(400);
        joinCard.setStyle("-fx-background-color: white; -fx-background-radius: 16;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 15, 0, 0, 4);");

        Label joinLabel = new Label("🚪 Vào phòng có sẵn");
        joinLabel.setFont(Font.font(18));

        TextField joinRoomField = new TextField();
        joinRoomField.setPromptText("Nhập Room ID muốn vào");
        joinRoomField.setPrefHeight(35);

        joinBtn.setPrefWidth(Double.MAX_VALUE);
        joinBtn.setStyle(btnStyle());

        joinCard.getChildren().addAll(
                joinLabel,
                joinRoomField,
                joinBtn
        );

        // Thêm 2 card vào layout chính
        root.getChildren().addAll(title, createCard, joinCard);

        // ===== EVENT =====

        // Tạo phòng → gửi CREATE_ROOM → server trả về ROOM_CREATED → server tự gửi GAME_START → vào game
       createBtn.setOnAction(e -> {
            String userInput = roomIdField.getText().trim();
            String finalRoomId;

            // Nếu người chơi KHÔNG nhập ID → tự tạo
            if (userInput.isEmpty()) {
                finalRoomId = "room-" + UUID.randomUUID().toString().substring(0, 6);
            } else {
                finalRoomId = userInput;
            }

            // Gửi request tạo phòng
            controller.createRoom(finalRoomId);

            // Thông báo trên giao diện (có thể thay bằng Alert)
            System.out.println("Đã tạo phòng với ID: " + finalRoomId);

            // Chuyển sang giao diện game (tùy project bạn)
            app.showGame(); 
        });
    }


    private String btnStyle() {
        return """
            -fx-background-color: #4A90E2;
            -fx-text-fill: white;
            -fx-font-size: 16;
            -fx-padding: 10;
            -fx-background-radius: 10;
            -fx-cursor: hand;
            """;
    }

    public VBox getRoot() {
        return root;
    }

    public void setRoomId(String id) {
        roomIdField.setText(id);
    }
}
