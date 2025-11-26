package client.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import client.controller.AppController;

public class LoginView {

    private VBox root = new VBox(15);
    private TextField usernameField = new TextField();
    private PasswordField passwordField = new PasswordField();
    private Button loginBtn = new Button("Đăng nhập");
    private Button registerBtn = new Button("Đăng ký");

    // Payload gửi qua mạng
    static class AuthPayload {
        String username;
        String password;
        AuthPayload(String u, String p) {
            this.username = u;
            this.password = p;
        }
    }

    public LoginView(AppController app) {

        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setPrefWidth(300);

        Label title = new Label("🎮 Tiến Lên Online");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        usernameField.setPromptText("Tên đăng nhập");
        passwordField.setPromptText("Mật khẩu");

        loginBtn.setMaxWidth(Double.MAX_VALUE);
        registerBtn.setMaxWidth(Double.MAX_VALUE);

        // 🔥 THÊM CÁC THÀNH PHẦN VÀO ROOT — QUAN TRỌNG
        root.getChildren().addAll(
                title,
                usernameField,
                passwordField,
                loginBtn,
                registerBtn
        );

        // ❗Xử lý nút đăng nhập
        loginBtn.setOnAction(e -> {
            String user = usernameField.getText().trim();
            String pass = passwordField.getText().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                showAlert("Vui lòng nhập đầy đủ tên và mật khẩu!");
                return;
            }

            app.getClient().send("LOGIN", new AuthPayload(user, pass));
        });

        // ❗Xử lý nút đăng ký
        registerBtn.setOnAction(e -> app.showRegisterView());
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public VBox getRoot() {
        return root;
    }
}
