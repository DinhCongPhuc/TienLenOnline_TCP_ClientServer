package client.view.component;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import java.io.InputStream;

public class PlayerBox extends VBox {

    private final ImageView avatar = new ImageView();
    private final Label name = new Label("-");
    private final Label count = new Label("0 lá");

    public PlayerBox() {
        setSpacing(6);
        setAlignment(Pos.CENTER);
        getStyleClass().add("player-box");

        avatar.setFitWidth(56);
        avatar.setFitHeight(56);
        avatar.setPreserveRatio(true);

        name.getStyleClass().add("player-name");
        count.getStyleClass().add("player-count");

        getChildren().addAll(avatar, name, count);
    }

    public void setPlayer(String playerName, String avatarPath) {
        name.setText(playerName);

        InputStream is = getClass().getResourceAsStream(avatarPath);
        if (is == null) {
            System.out.println("Không tìm thấy avatar: " + avatarPath);
        } else {
            avatar.setImage(new Image(is));
        }
    }

    public void setCardCount(int c) {
        count.setText(c + " lá");
    }

    public void highlight(boolean on) {
        setStyle(on ? "-fx-border-color: gold;" : "");
    }
}
