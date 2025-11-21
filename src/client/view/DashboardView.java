package client.view;

import client.controller.AppController;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;



public class DashboardView extends BorderPane {

    private AppController app;
    private String username;

    private boolean darkMode = false;

    public DashboardView(AppController app, String username) {
        this.app = app;
        this.username = username;

        // ===== HEADER =====
        Label title = new Label("🎮 Tiến Lên Online");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));

        Button toggleTheme = new Button("🌙");
        toggleTheme.setStyle("-fx-background-color: transparent; -fx-font-size: 18;");
        toggleTheme.setCursor(Cursor.HAND);

        HBox header = new HBox(20, title, toggleTheme);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: linear-gradient(to right, #6a11cb, #2575fc);");

        // ===== SIDEBAR (sliding) =====
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(30));
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: #ffffffaa; -fx-backdrop-filter: blur(10px);");

        // Avatar
        ImageView avatar = new ImageView(new Image(getClass().getResource("/client/assets/avatar.jpg").toString()));
        avatar.setFitWidth(90);
        avatar.setFitHeight(90);

        Label nameLabel = new Label(username);
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        VBox userBox = new VBox(10, avatar, nameLabel);
        userBox.setAlignment(Pos.CENTER);

        Button btnPlay = createNavButton("▶ Bắt đầu chơi");
        Button btnRoom = createNavButton("👤 Tạo phòng");
        btnRoom.setOnAction(e -> app.showRoom());
        Button btnProfile = createNavButton("👤 Hồ sơ cá nhân");
        Button btnRank = createNavButton("🏆 Rank");
        Button btnLogout = createNavButton("🚪 Đăng xuất");

        sidebar.getChildren().addAll(userBox, btnPlay,btnRoom, btnProfile, btnRank, btnLogout);

        // Sidebar sliding animation
        sidebar.setTranslateX(-250);
        TranslateTransition slideIn = new TranslateTransition(Duration.seconds(0.4), sidebar);
        slideIn.setToX(0);
        slideIn.play();

        // ===== MAIN CONTENT =====
        Label welcome = new Label("Chào mừng, " + username + "!");
        welcome.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // PLAY button pulse animation
        btnPlay.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 18; -fx-padding: 10 20; -fx-background-radius: 10;");
        ScaleTransition pulse = new ScaleTransition(Duration.seconds(1.2), btnPlay);
        pulse.setFromX(1.0);
        pulse.setToX(1.1);
        pulse.setFromY(1.0);
        pulse.setToY(1.1);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();

        // Rank cards
        HBox rankCards = new HBox(20,
                createRankCard("🥇 Top 1", "Người chơi A", "1500 điểm"),
                createRankCard("🥈 Top 2", "Người chơi B", "1200 điểm"),
                createRankCard("🥉 Top 3", "Người chơi C", "1000 điểm")
        );
        rankCards.setAlignment(Pos.CENTER);
        rankCards.setPadding(new Insets(20));

        VBox mainArea = new VBox(25, welcome, rankCards);
        mainArea.setAlignment(Pos.TOP_CENTER);
        mainArea.setPadding(new Insets(30));

        // Dark/Light mode toggle
        toggleTheme.setOnAction(e -> toggleTheme(mainArea, sidebar));

        // Final layout
        this.setTop(header);
        this.setLeft(sidebar);
        this.setCenter(mainArea);
        this.setStyle("-fx-background-color: linear-gradient(to bottom, #eef2ff, #ffffff);");
    }

    // ===== Helper: Navigation Button =====
    private Button createNavButton(String text) {
        Button b = new Button(text);
        b.setPrefWidth(200);
        b.setFont(Font.font(16));
        b.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 8; -fx-padding: 10;");

        b.setCursor(Cursor.HAND);
        b.setOnMouseEntered(ev -> b.setStyle("-fx-background-color: #dcdcdc; -fx-background-radius: 8;"));
        b.setOnMouseExited(ev -> b.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 8;"));
        return b;
    }

    // ===== Helper: Rank Card =====
    private VBox createRankCard(String title, String player, String score) {
        Label t = new Label(title);
        t.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Label p = new Label(player);
        Label s = new Label(score);

        VBox box = new VBox(10, t, p, s);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));
        box.setPrefWidth(150);
        box.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 12; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10,0,0,3);");

        return box;
    }

    // ===== Toggle Dark / Light Mode =====
    private void toggleTheme(VBox main, VBox sidebar) {
        darkMode = !darkMode;

        if (darkMode) {
            this.setStyle("-fx-background-color: #1e1e1e;");
            main.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
            sidebar.setStyle("-fx-background-color: #222222aa; -fx-text-fill: white;");
        } else {
            this.setStyle("-fx-background-color: linear-gradient(to bottom, #eef2ff, #ffffff);");
            main.setStyle("-fx-background-color: transparent; -fx-text-fill: black;");
            sidebar.setStyle("-fx-background-color: #ffffffaa;");
        }
    }
}
