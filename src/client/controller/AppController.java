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
    private AppController app;

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
        Platform.runLater(() -> stage.setScene(new Scene(rv.getRoot(), 600, 400)));
    }

    public void showGame() {
    Platform.runLater(() -> {
        // 1. Tạo Controller với payload mặc định null (hoặc payload thực tế nếu có)
        GameController gameController = new GameController(client, app, null);

        // 2. Tạo GameView và truyền Controller
        GameView gameView = new GameView(gameController);

        // 3. Gán view vào Controller
        gameController.setView(gameView);

        // 4. Tạo Scene và thêm stylesheet
        Scene scene = new Scene(gameView.getRoot(), 1100, 700);
        scene.getStylesheets().add(getClass().getResource("/main/resources/styles/game.css").toExternalForm());

        // 5. Set Scene cho Stage và hiển thị
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
