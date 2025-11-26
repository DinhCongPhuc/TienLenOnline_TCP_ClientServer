package client.view.component;

import javafx.scene.layout.FlowPane;
import java.util.List;
import java.util.stream.Collectors;

public class HandPane extends FlowPane {

    public HandPane() {
        setHgap(8);
        setVgap(8);
        getStyleClass().add("hand-pane");
    }

    public void setHand(List<String> codes) {
        getChildren().clear();
        for (String c : codes)
            getChildren().add(new CardNode(c, 72, 104));
    }

    public List<String> getSelected() {
        return getChildren().stream()
                .filter(n -> n instanceof CardNode)
                .map(n -> (CardNode) n)
                .filter(CardNode::isSelected)
                .map(CardNode::getCode)
                .collect(Collectors.toList());
    }

    public void removeCards(List<String> codes) {
    getChildren().removeIf(n -> n instanceof CardNode && codes.contains(((CardNode)n).getCode()));
}
}
