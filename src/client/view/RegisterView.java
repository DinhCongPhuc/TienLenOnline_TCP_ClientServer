package client.view;

import com.google.gson.JsonObject;

import client.controller.AppController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class RegisterView {

    private VBox root = new VBox(15);
    private TextField usernameField = new TextField();
    private PasswordField passwordField = new PasswordField();
    private PasswordField confirmField = new PasswordField();
    private Button registerBtn = new Button("Đăng ký");
    private Button backBtn = new Button("← Quay lại đăng nhập");

    // Payload gửi qua mạng
    static class RegisterPayload {
        String username;
        String password;
        RegisterPayload(String u, String p) {
            this.username = u;
            this.password = p;
        }
    }

    public RegisterView(AppController app) {
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setPrefWidth(350);

        Label title = new Label("🆕 Đăng ký tài khoản");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        usernameField.setPromptText("Tên đăng nhập");
        passwordField.setPromptText("Mật khẩu");
        confirmField.setPromptText("Xác nhận mật khẩu");

        registerBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setMaxWidth(Double.MAX_VALUE);

        // Xử lý nút đăng ký
        registerBtn.setOnAction(e -> {
            String user = usernameField.getText().trim();
            String pass = passwordField.getText().trim();
            String confirm = confirmField.getText().trim();

            if (user.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                showAlert("Vui lòng nhập đầy đủ thông tin!");
                return;
            }
            if (!pass.equals(confirm)) {
                showAlert("Mật khẩu xác nhận không khớp!");
                return;
            }

            JsonObject payload = new JsonObject();
            payload.addProperty("username", user);
            payload.addProperty("password", pass);

            app.getClient().send("REGISTER", payload);
        });


        // Nút quay lại login
        backBtn.setOnAction(e -> {
            app.showLoginView();
        });

        root.getChildren().addAll(
            title,
            usernameField,
            passwordField,
            confirmField,
            registerBtn,
            backBtn
        );
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public VBox getRoot() {
        return root;
    }
}
