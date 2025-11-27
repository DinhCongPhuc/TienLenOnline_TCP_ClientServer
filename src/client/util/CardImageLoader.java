package client.util;

import javafx.scene.image.Image;

/**
 * Load ảnh bài từ resources - cách chuẩn nhất cho JavaFX
 */
public class CardImageLoader {

    private static final String CARD_PATH = "/main/resources/card/%s.png";
    private static final String BACK_PATH = "/main/resources/card/back.png";

    private static Image backImage;

    public static Image load(String code) {
        if (code == null || code.isEmpty()) return null;
        String path = String.format(CARD_PATH, code.toUpperCase());

        // Dòng debug cực kỳ quan trọng
        System.out.println("Đang load ảnh: " + path);
        var inputStream = CardImageLoader.class.getResourceAsStream(path);
        if (inputStream == null) {
            System.err.println("KHÔNG TÌM THẤY ẢNH: " + path);
            return null;
        }
        return new Image(inputStream);
    }

    public static Image loadBack() {
        if (backImage == null) {
            backImage = new Image(CardImageLoader.class.getResourceAsStream(BACK_PATH));
        }
        return backImage;
    }
}