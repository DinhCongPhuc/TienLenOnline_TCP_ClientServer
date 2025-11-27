package client.view;

import client.controller.GameController;
import client.util.CardImageLoader;
import client.view.component.*;
import javafx.scene.layout.*;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;   // thêm dòng này
import javafx.scene.image.Image;       // thêm dòng này (nếu chưa có)
import java.util.ArrayList;
import java.util.List;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.control.Alert;
import com.google.gson.JsonObject;
import java.util.List;

import com.google.gson.JsonObject;

    public class GameView {

        private final BorderPane root = new BorderPane();

        private final PlayerBox bottom = new PlayerBox();
        private final PlayerBox left   = new PlayerBox();
        private final PlayerBox top    = new PlayerBox();
        private final PlayerBox right  = new PlayerBox();

        private final HandPane hand = new HandPane();
        private final CenterPlay center = new CenterPlay();

        private final Button btPlay = new Button("Đánh");
        private final Button btPass = new Button("Bỏ lượt");
        private final Button btSort = new Button("Sắp");

        private final GameController controller;
        private List<String> selectedCards = new ArrayList<>();
        private Pane gameArea; // Giả sử bạn có biến này
        

        public GameView(GameController controller) {
            this.controller = controller;
            build();
            setupActions();
        }

        private void build() {
            // TOP
            VBox topBox = new VBox(top);
            topBox.setAlignment(Pos.CENTER);
            topBox.setPadding(new Insets(10));

            // BOTTOM
            VBox bottomArea = new VBox(6, bottom, hand, new HBox(10, btSort, btPlay, btPass));
            bottomArea.setAlignment(Pos.CENTER);

            root.setTop(topBox);
            root.setBottom(bottomArea);
            root.setLeft(left);
            root.setRight(right);
            root.setCenter(center);

        }

        private void setupActions() {
            btSort.setOnAction(e -> hand.setHand(hand.getChildren().stream()
                    .map(n -> ((CardNode)n).getCode()).sorted().toList()));

            btPlay.setOnAction(e -> controller.playCards(hand.getSelected()));
            btPass.setOnAction(e -> controller.pass());
        }

        /* ====== Public API cho controller ====== */
        public BorderPane getRoot() { return root; }

        public void setHand(List<String> cards) {
            hand.setHand(cards);
        }

        public void showLastPlay(List<String> codes) {
            center.showCards(codes);
        }

        
        public void highlightPlayer(int idx) {
            bottom.highlight(idx == 0);
            left.highlight(idx == 1);
            top.highlight(idx == 2);
            right.highlight(idx == 3);
        }

        public void setPlayerNames(List<String> names, String myPos) {
            bottom.setPlayer(names.get(0), "/assets/avatar.jpg");
            left.setPlayer(names.get(1), "/assets/avatar.jpg");
            top.setPlayer(names.get(2), "/assets/avatar.jpg");
            right.setPlayer(names.get(3), "/assets/avatar.jpg");
        }

        public void removeCardsFromHand(List<String> codes) {
            hand.removeCards(codes);
        }

        public List<String> getSelectedHand() {
            return hand.getSelected();
        }

        // ⭐ THÊM CÁC METHOD NÀY VÀO GameView.java

        /** ⭐ XÓA CHỌN BÀI */
        public void clearSelection() {
            selectedCards.clear();
            updateCardDisplay();
            System.out.println("🧹 Đã xóa chọn tất cả");
        }

        /** ⭐ CẬP NHẬT TRẠNG THÁI GAME */
        public void updateGameState(String currentPlayerId, List<String> lastPlay, JsonObject handSizes) {
            System.out.println("📊 Cập nhật: Lượt " + currentPlayerId + " | Bài vừa đánh: " + lastPlay);
            
            // Hiển thị bài vừa đánh
            if (lastPlay != null && !lastPlay.isEmpty()) {
                displayLastPlay(lastPlay);
            }
            
            // Cập nhật số bài từng người
            updateHandSizes(handSizes);
            
            // Highlight người chơi hiện tại
            highlightPlayer(currentPlayerId);
        }

        /** ⭐ HIỂN THỊ KẾT QUẢ ĐÁNH BÀI */
        public void showPlayResult(boolean success, String message) {
            Label resultLabel = new Label(message);
            resultLabel.setStyle(success ? 
                "-fx-text-fill: #4CAF50; -fx-font-weight: bold; -fx-font-size: 16;" :
                "-fx-text-fill: #f44336; -fx-font-weight: bold; -fx-font-size: 16;");
            
            // Thêm vào góc trên trái
            resultLabel.setLayoutX(20);
            resultLabel.setLayoutY(20);
            gameArea.getChildren().add(resultLabel);
            
            // Tự xóa sau 2 giây
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(e -> gameArea.getChildren().remove(resultLabel));
            delay.play();
        }

        /** ⭐ HIỂN THỊ KẾT THÚC GAME */
        public void showGameEnd(String winner, String message) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("🎉 Kết thúc ván chơi!");
            alert.setHeaderText(winner + " đã thắng!");
            alert.setContentText(message);
            alert.showAndWait();
            
            // Thêm hiệu ứng trên màn hình
            Label endLabel = new Label("🏆 " + message);
            endLabel.setStyle("-fx-font-size: 32; -fx-text-fill: gold; -fx-font-weight: bold;");
            endLabel.setLayoutX(400);
            endLabel.setLayoutY(300);
            gameArea.getChildren().add(endLabel);
        }

        /** ⭐ HIỂN THỊ BÀN MỚI */
        public void showNewRound(String message) {
            Label roundLabel = new Label("🔄 " + message);
            roundLabel.setStyle("-fx-font-size: 24; -fx-text-fill: #FF9800; -fx-font-weight: bold;");
            roundLabel.setLayoutX(400);
            roundLabel.setLayoutY(250);
            gameArea.getChildren().add(roundLabel);
            
            // Tự xóa sau 3 giây
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(e -> gameArea.getChildren().remove(roundLabel));
            delay.play();
        }

        /** ⭐ CẬP NHẬT BÀI ĐÃ CHỌN */
        public void updateSelection(List<String> selectedCards) {
            this.selectedCards = new ArrayList<>(selectedCards);
            updateCardDisplay();
        }

        /** ⭐ METHOD GIẢ - SỬA SAU */
        private void displayLastPlay(List<String> lastPlay) {
            System.out.println("🎯 Bài vừa đánh: " + lastPlay);
            // TODO: Hiển thị giữa bàn
        }

        private void updateHandSizes(JsonObject handSizes) {
            System.out.println("📊 Số bài: " + handSizes);
            // TODO: Cập nhật label số bài
        }

        private void highlightPlayer(String playerId) {
            System.out.println("👑 Đến lượt: " + playerId);
            // TODO: Viền vàng
        }

        // ⭐ THÊM METHOD NÀY VÀO GameView.java
        public void updateCardDisplay() {
            System.out.println("🔄 Cập nhật hiển thị bài: " + selectedCards.size() + " lá đã chọn");
            // TODO: Update UI cards (highlight selected)
        }

        // ⭐ BIẾN CẦN THIẾT (thêm vào đầu class)
      
    }
