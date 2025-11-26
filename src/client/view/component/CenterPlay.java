package client.view.component;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

import java.util.List;

public class CenterPlay extends VBox {

    private final HBox cardBox = new HBox(6);
    private final Label status = new Label();

    public CenterPlay() {
        setAlignment(Pos.CENTER);
        getChildren().addAll(cardBox, status);
    }

    public void showCards(List<String> codes) {
        cardBox.getChildren().clear();
        if (codes == null || codes.isEmpty()) {
            status.setText("(Chưa có lượt nào)");
            return;
        }
        status.setText("");
        for (String c : codes)
            cardBox.getChildren().add(new CardNode(c, 52, 76));
    }
}
