    package client.view;

    import client.controller.GameController;
    import client.view.component.*;
    import javafx.scene.layout.*;
    import javafx.geometry.Insets;
    import javafx.geometry.Pos;
    import javafx.scene.control.Button;

    import java.util.List;

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
            bottom.setPlayer(names.get(0), "/main/resources/assets/avatar.jpg");
            left.setPlayer(names.get(1), "/main/resources/assets/avatar.jpg");
            top.setPlayer(names.get(2), "/main/resources/assets/avatar.jpg");
            right.setPlayer(names.get(3), "/main/resources/assets/avatar.jpg");
        }

        public void removeCardsFromHand(List<String> codes) {
            hand.removeCards(codes);
        }

        public List<String> getSelectedHand() {
            return hand.getSelected();
        }
    }
