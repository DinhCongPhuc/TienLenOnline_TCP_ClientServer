package client.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import client.network.NetworkClient;
import client.view.*;

public class AppController {
    private Stage stage;
    private NetworkClient client;
    private Gson gson = new Gson();
    private RoomController roomController;  // ← THÊM DÒNG NÀY
    private GameController gameController;

    public AppController(Stage stage, NetworkClient client) {
        this.stage = stage;
        this.client = client;

        // Lắng nghe message chung
        client.setOnMessage(this::handleMessage);
    }

    public void showLogin() {
        LoginView loginView = new LoginView(this);
        stage.setScene(new Scene(loginView.getRoot(), 400, 300));
        stage.show();
    }

    public void showRoom() {
        RoomController rc = new RoomController(client, this);
        RoomView rv = new RoomView(rc, this);
        rc.setView(rv);
        this.roomController = rc;
        Platform.runLater(() -> stage.setScene(new Scene(rv.getRoot(), 600, 400)));
    }

  public void showGame() {
    Platform.runLater(() -> {
        GameController gameController = new GameController(client, this, null);
        GameView gameView = new GameView(gameController);
        gameController.setView(gameView);
        this.gameController = gameController;  // ← THÊM DÒNG NÀY
        
        Scene scene = new Scene(gameView.getRoot(), 1100, 700);
        scene.getStylesheets().add(getClass().getResource("/main/resources/styles/game.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Game - Phòng mới");
        stage.show();
    });
}




    // Chuyển tiếp các message từ server đến controller phù hợp
    private void handleMessage(JsonObject msg) {
        String type = msg.get("type").getAsString();
        JsonObject payload = msg.getAsJsonObject("payload");

        switch (type) {
            case "REGISTER_OK":
                Platform.runLater(() -> {
                    showAlert("Đăng ký thành công!");
                    showLoginView();
                });
                break;

            case "REGISTER_FAIL":
                Platform.runLater(() ->
                    showAlert(payload.get("message").getAsString())
                );
                break;

            case "LOGIN_OK":
                Platform.runLater(() -> {
                    showAlert("Đăng nhập thành công!");

                    String username = payload.get("user").getAsString();

                    // 👉 Điều hướng sang Dashboard
                    showDashboard(username);
                });
                break;



            case "LOGIN_FAIL":
                Platform.runLater(() ->
                    showAlert("Sai tài khoản hoặc mật khẩu!")
                );
                break;
                case "ROOM_CREATED":
                case "ROOM_UPDATE":
                case "ERROR":
                    if (roomController != null) {
                        roomController.handleMessage(msg);  // ← CHUYỂN CHO RoomController
                    }
                    break;

                case "GAME_START":
                case "GAME_STATE":
                case "PLAY_RESULT":
                case "GAME_END":
                case "NEW_ROUND":
                    if (gameController != null) {
                        gameController.handleMessage(msg);  // ← CHUYỂN CHO GameController
                    }
                    break;

            default:
                Platform.runLater(() ->
                    showAlert("Unknown message type: " + type)
                );
                break;
        }
    }


    private void showAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }


    public NetworkClient getClient() {
        return client;
    }

    // Trong AppController
    public void showLoginView() {
        LoginView view = new LoginView(this);

        // Tạo Scene mới cho lần đầu
        javafx.scene.Scene scene = new javafx.scene.Scene(view.getRoot(), 400, 350);
        stage.setScene(scene);
        stage.setTitle("Đăng nhập");
        stage.show();
    }


    public void showRegisterView() {
        RegisterView view = new RegisterView(this);

        // Nếu chưa có scene (phòng hờ), thì tạo luôn
        if (stage.getScene() == null) {
            javafx.scene.Scene scene = new javafx.scene.Scene(view.getRoot(), 400, 350);
            stage.setScene(scene);
        } else {
            stage.getScene().setRoot(view.getRoot());
        }
    }

    public void showDashboard(String username) {
        DashboardView dashboard = new DashboardView(this, username);

        Platform.runLater(() -> {
            Scene scene = new Scene(dashboard, 1100, 700);
            stage.setScene(scene);
            stage.setTitle("Trang Chủ — " + username);
            stage.show();
        });
    }


}
