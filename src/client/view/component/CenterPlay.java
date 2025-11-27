package client.view.component;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class CenterPlay extends VBox {

    private final HBox cardBox = new HBox(12);
    private final Label status = new Label("Bắt đầu ván mới...");

    public CenterPlay() {
        setAlignment(Pos.CENTER);
        setSpacing(20);

        cardBox.setAlignment(Pos.CENTER);
        status.getStyleClass().add("center-status");

        getChildren().addAll(cardBox, status);
    }

    public void showCards(List<String> codes) {
        cardBox.getChildren().clear();
        if (codes == null || codes.isEmpty()) {
            status.setText("Chưa có ai đánh");
            return;
        }

        status.setText("");
        for (String code : codes) {
            CardNode card = new CardNode(code, 60, 88);
            card.setMouseTransparent(true); // không cho click
            cardBox.getChildren().add(card);
        }
    }

    public void clear() {
        cardBox.getChildren().clear();
        status.setText("Đã xóa lượt cũ");
    }
}