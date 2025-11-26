package client.view.component;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import server.database.CardDao; // nhớ import đúng package CardDao

public class CardNode extends ImageView {

    private boolean selected = false;
    private final String code;

    /**
     * @param code: mã bài (ví dụ "3C")
     * @param w: chiều rộng hiển thị
     * @param h: chiều cao hiển thị
     * @param loader: đối tượng CardDao để load ảnh từ DB
     */
    public CardNode(String code, double w, double h, CardDao loader) {
        this.code = code;

        // Load ảnh từ DB
        Image img = null;
        try {
            img = loader.loadCardImage(code);
            if (img == null) {
                System.out.println("[CardNode] Không tìm thấy bài: " + code);
            }
        } catch (Exception e) {
            System.err.println("[CardNode] Lỗi khi load bài: " + code);
            e.printStackTrace();
        }

        if (img != null) {
            setImage(img);
        } else {
            // Nếu không tìm thấy ảnh, có thể hiển thị placeholder
            setStyle("-fx-background-color: gray; -fx-border-color: red;");
        }

        // Cấu hình hiển thị
        setFitWidth(w);
        setFitHeight(h);
        setPreserveRatio(true);
        setEffect(new DropShadow(6, Color.rgb(0, 0, 0, 0.4)));

        // Sự kiện click chọn/bỏ chọn
        setOnMouseClicked(this::toggle);

        // Hover nâng lên
        setOnMouseEntered(e -> { if (!selected) setTranslateY(-8); });
        setOnMouseExited(e -> { if (!selected) setTranslateY(0); });
    }

    private void toggle(MouseEvent e) {
        selected = !selected;
        setTranslateY(selected ? -28 : 0);
    }

    public boolean isSelected() {
        return selected;
    }

    public String getCode() {
        return code;
    }
}
