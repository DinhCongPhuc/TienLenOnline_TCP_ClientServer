package client.view.component;

import javafx.scene.layout.FlowPane;
import java.util.List;
import java.util.stream.Collectors;

public class HandPane extends FlowPane {

    public HandPane() {
        setHgap(10);
        setVgap(12);
        setPrefWrapLength(800);
        getStyleClass().add("hand-pane");
    }

    public void setHand(List<String> codes) {
        getChildren().clear();
        // if (codes == null || codes.isEmpty()) {
        // // TEST: hiện thử 13 lá cố định
        //     codes = List.of("3C","4C","5C","6C","7C","8C","9C","TC","JC","QC","KC","AC","2C");
        // }
        if (codes == null) return;
        for (String code : codes) {
            CardNode card = new CardNode(code, 80, 116); // kích thước đẹp cho tay bài
            getChildren().add(card);
        }
        requestLayout();
    }

    public List<String> getSelected() {
        return getChildren().stream()
                .filter(n -> n instanceof CardNode && ((CardNode) n).isSelected())
                .map(n -> ((CardNode) n).getCode())
                .collect(Collectors.toList());
    }

    public void removeCards(List<String> codes) {
        if (codes == null || codes.isEmpty()) return;
        getChildren().removeIf(node -> {
            if (node instanceof CardNode) {
                String code = ((CardNode) node).getCode();
                return codes.contains(code);
            }
            return false;
        });
    }

    // Sau khi đánh xong, bỏ chọn tất cả
    public void clearSelection() {
        getChildren().forEach(node -> {
            if (node instanceof CardNode) {
                ((CardNode) node).unselect();
            }
        });
    }
}