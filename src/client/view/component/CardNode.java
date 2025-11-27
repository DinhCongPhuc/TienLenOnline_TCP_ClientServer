package client.view.component;

import client.util.CardImageLoader;
import javafx.scene.image.ImageView;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;

public class CardNode extends ImageView {

    private boolean selected = false;
    private final String code;

    public CardNode(String code, double w, double h) {
        this.code = code.toUpperCase();

        // Load ảnh từ resources
        setImage(CardImageLoader.load(code));

        setFitWidth(w);
        setFitHeight(h);
        setPreserveRatio(true);
        setSmooth(true);

        // Hiệu ứng bóng
        setEffect(new DropShadow(6, Color.rgb(0, 0, 0, 0.6)));

        // Hover: nâng lên nhẹ
        setOnMouseEntered(e -> { if (!selected) setTranslateY(-10); });
        setOnMouseExited(e -> { if (!selected) setTranslateY(0); });

        // Click để chọn/bỏ chọn
        setOnMouseClicked(this::toggle);
    }
private void toggle(MouseEvent e) {
    selected = !selected;
    setTranslateY(selected ? -30 : 0);

    if (selected) {
        DropShadow glow = new DropShadow();
        glow.setColor(Color.GOLD);
        glow.setRadius(15);
        glow.setSpread(0.35);
        glow.setOffsetX(0);
        glow.setOffsetY(0);
        setEffect(glow);
    } else {
        setEffect(new DropShadow(6, Color.rgb(0, 0, 0, 0.6)));
    }
}

    public boolean isSelected() {
        return selected;
    }

    public String getCode() {
        return code;
    }

    // Dùng khi muốn reset trạng thái chọn (ví dụ sau khi đánh)
    public void unselect() {
        if (selected) {
            selected = false;
            setTranslateY(0);
            setEffect(new DropShadow(6, Color.rgb(0, 0, 0, 0.6)));
        }
    }
}