package client.view;

import client.controller.GameController;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Modernized GameView: chat drawer, last-play images, avatar bubbles, highlight current player,
 * responsive hand, selection + sort, and basic styling.
 *
 * NOTE: ensure card images are in classpath at /client/assets/card/{CODE}.png
 * and avatars at /client/assets/avatar/*.png
 */
public class GameView {

    private final StackPane root = new StackPane();

    // Player boxes
    private VBox topBox, leftBox, rightBox, bottomBox;
    private Label topCount, leftCount, rightCount, bottomCount;
    private ImageView topAvatar, leftAvatar, rightAvatar, bottomAvatar;
    private Label topBubble, leftBubble, rightBubble, bottomBubble;

    // center
    private final Label titleLabel = new Label("TIẾN LÊN MIỀN NAM");
    private final Label betLabel = new Label("Mức cược: 5.000");
    private final HBox lastPlayImages = new HBox(6); // will show images of last played cards
    private final Label lastPlayText = new Label("(Chưa có lượt nào)");

    // hand and actions
    private final TilePane handBox = new TilePane(6, 0); // responsive grid-like layout
    private final Button playBtn = new Button("Đánh");
    private final Button passBtn = new Button("Bỏ lượt");
    private final Button sortBtn = new Button("Xếp");

    // chat
    private final VBox chatPane = new VBox(6);
    private final Button chatIcon = new Button();
    private final Label chatBadge = new Label();
    private final ListView<String> chatArea = new ListView<>();
    private final TextField chatInput = new TextField();
    private final Button sendChatBtn = new Button("Gửi");
    private boolean chatVisible = false;
    private int unreadCount = 0;

    private final GameController controller;

    // player ordering used by updateState (arranged bottom,left,top,right)
    private List<String> arrangedNames = List.of("p1","p2","p3","p4");

    public GameView(GameController controller, JsonObject payload) {
        this.controller = controller;
        buildLayout();
        setupActions();

        // initial hand if provided
        if (payload != null && payload.has("yourCards")) {
            JsonArray arr = payload.getAsJsonArray("yourCards");
            List<String> list = new ArrayList<>();
            for (int i = 0; i < arr.size(); i++) list.add(arr.get(i).getAsString());
            setHand(list);
        }
    }

    public StackPane getRoot() { return root; }

    /* ---------------- layout ---------------- */
    private void buildLayout() {
        BorderPane table = new BorderPane();
        table.setPadding(new Insets(12));
        table.setStyle("-fx-background-color: linear-gradient(to bottom, #0E7A45, #0A6A3A);");

        // TOP / LEFT / RIGHT / BOTTOM player boxes
        topBox = createPlayerBox("PlayerTop", "/client/assets/avatar.jpg");
        leftBox = createPlayerBox("PlayerLeft", "/client/assets/avatar.jpg");
        rightBox = createPlayerBox("PlayerRight", "/client/assets/avatar.jpg");
        bottomBox = createPlayerBox("Bạn", "/client/assets/avatar.jpg");

        BorderPane.setAlignment(topBox, Pos.TOP_CENTER);
        BorderPane.setAlignment(leftBox, Pos.CENTER_LEFT);
        BorderPane.setAlignment(rightBox, Pos.CENTER_RIGHT);

        table.setTop(topBox);
        table.setLeft(leftBox);
        table.setRight(rightBox);

        // Bottom area contains bottomBox, hand, and buttons
        handBox.setHgap(6);
        handBox.setVgap(0);
        handBox.setPrefColumns(13);
        handBox.setAlignment(Pos.CENTER);
        handBox.setPadding(new Insets(8));
        handBox.setStyle("-fx-padding: 8 0 12 0;");

        HBox actions = new HBox(10, sortBtn, playBtn, passBtn);
        actions.setAlignment(Pos.CENTER);

        VBox bottomArea = new VBox(6, bottomBox, handBox, actions);
        bottomArea.setAlignment(Pos.CENTER);
        bottomArea.setPadding(new Insets(8));
        table.setBottom(bottomArea);

        // Center area (title, bet, lastPlay)
        titleLabel.setFont(Font.font(28));
        titleLabel.setTextFill(Color.web("#f7f7f7"));

        betLabel.setFont(Font.font(14));
        betLabel.setTextFill(Color.web("#f3f3f3"));

        lastPlayText.setFont(Font.font(13));
        lastPlayText.setTextFill(Color.web("#fffb"));

        lastPlayImages.setAlignment(Pos.CENTER);
        lastPlayImages.setPadding(new Insets(8));

        VBox centerBox = new VBox(8, titleLabel, betLabel, lastPlayImages, lastPlayText);
        centerBox.setAlignment(Pos.CENTER);
        table.setCenter(centerBox);

        // Chat overlay and icon stacked on top-left
        setupChatOverlay();

        // root
        root.getChildren().addAll(table, chatOverlayContainer());
    }

    private VBox createPlayerBox(String name, String avatarPath) {
        ImageView avatar = new ImageView(loadImage(avatarPath));
        avatar.setFitWidth(60);
        avatar.setFitHeight(60);
        avatar.setPreserveRatio(true);
        avatar.setSmooth(true);
        avatar.setCache(true);
        avatar.setCacheHint(CacheHint.SPEED);

        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font(13));
        nameLabel.setTextFill(Color.WHITE);

        Label countLabel = new Label("13 lá");
        countLabel.setFont(Font.font(12));
        countLabel.setTextFill(Color.web("#e9f7ee"));

        Label bubble = new Label();
        bubble.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-padding: 4 8 4 8; -fx-background-radius: 10;");
        bubble.setVisible(false);

        VBox box = new VBox(6, avatar, nameLabel, countLabel, bubble);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(6));
        box.setStyle("-fx-background-color: rgba(0,0,0,0.08); -fx-background-radius: 8;");

        // store references
        if (topAvatar == null) { topAvatar = avatar; topCount = countLabel; topBubble = bubble; }
        else if (leftAvatar == null) { leftAvatar = avatar; leftCount = countLabel; leftBubble = bubble; }
        else if (rightAvatar == null) { rightAvatar = avatar; rightCount = countLabel; rightBubble = bubble; }
        else { bottomAvatar = avatar; bottomCount = countLabel; bottomBubble = bubble; }

        return box;
    }

    /* ---------------- chat overlay ---------------- */
    private void setupChatOverlay() {
        chatArea.setPrefHeight(200);
        HBox inputRow = new HBox(6, chatInput, sendChatBtn);
        inputRow.setAlignment(Pos.CENTER_LEFT);
        chatInput.setPromptText("Gõ tin nhắn...");
        chatPane.getChildren().addAll(new Label("💬 Chat trong ván"), chatArea, inputRow);
        chatPane.setPadding(new Insets(8));
        chatPane.setStyle("-fx-background-color: rgba(255,255,255,0.95); -fx-border-color: rgba(0,0,0,0.08); -fx-border-radius: 8;");
        chatPane.setPrefWidth(280);
        chatPane.setVisible(false);

        // icon + badge
        chatIcon.setText("💬");
        chatIcon.setStyle("-fx-background-radius: 50%; -fx-pref-width:40; -fx-pref-height:40; -fx-font-size: 14;");
        chatBadge.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size:11; -fx-padding: 2 6 2 6; -fx-background-radius: 10;");
        chatBadge.setVisible(false);

        sendChatBtn.setOnAction(e -> {
            String t = chatInput.getText().trim();
            if (!t.isEmpty()) {
                controller.sendChat(t);
                chatInput.clear();
                if (!chatVisible) toggleChatPane();
            }
        });

        chatIcon.setOnAction(e -> toggleChatPane());
    }

    private StackPane chatOverlayContainer() {
        StackPane container = new StackPane();
        container.setPickOnBounds(false);
        StackPane.setAlignment(chatIcon, Pos.BOTTOM_LEFT);
        StackPane.setMargin(chatIcon, new Insets(12));
        StackPane.setAlignment(chatPane, Pos.BOTTOM_LEFT);
        StackPane.setMargin(chatPane, new Insets(60,0,12,12));
        // wrap icon and badge
        StackPane iconStack = new StackPane(chatIcon, chatBadge);
        StackPane.setAlignment(chatBadge, Pos.TOP_RIGHT);
        StackPane.setMargin(chatBadge, new Insets(2,2,0,0));
        container.getChildren().addAll(iconStack, chatPane);
        return container;
    }

    private void toggleChatPane() {
        chatVisible = !chatVisible;
        chatPane.setVisible(chatVisible);
        if (chatVisible) {
            unreadCount = 0;
            chatBadge.setVisible(false);
        }
    }

    public void appendChat(String from, String text) {
        Platform.runLater(() -> {
            chatArea.getItems().add(from + ": " + text);
            chatArea.scrollTo(chatArea.getItems().size() - 1);
        });

        if (!chatVisible) {
            unreadCount++;
            Platform.runLater(() -> {
                chatBadge.setText(String.valueOf(unreadCount));
                chatBadge.setVisible(true);
            });
        }

        // show bubble over matching name (match bottomBox name, left, top, right)
        String bottomName = ((Label)bottomBox.getChildren().get(1)).getText();
        String leftName = ((Label)leftBox.getChildren().get(1)).getText();
        String topName = ((Label)topBox.getChildren().get(1)).getText();
        String rightName = ((Label)rightBox.getChildren().get(1)).getText();

        Label target = null;
        if (from.equals(bottomName)) target = bottomBubble;
        else if (from.equals(leftName)) target = leftBubble;
        else if (from.equals(topName)) target = topBubble;
        else if (from.equals(rightName)) target = rightBubble;

        if (target != null) {
            final Label t = target;
            Platform.runLater(() -> {
                t.setText(text);
                t.setVisible(true);
            });
            // auto-hide after 3s
            new Thread(() -> {
                try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
                Platform.runLater(() -> t.setVisible(false));
            }).start();
        }
    }

    /* ---------------- actions (play/pass/sort) ---------------- */
    private void setupActions() {
        playBtn.setOnAction(e -> {
            List<String> list = handBox.getChildren().stream()
                    .filter(n -> n.getTranslateY() < 0)
                    .map(n -> ((ImageView)n).getId())
                    .collect(Collectors.toList());
            if (!list.isEmpty()) controller.playCards(list);
        });

        passBtn.setOnAction(e -> controller.pass());

        sortBtn.setOnAction(e -> sortHand());

        // hover effect for hand tiles
        handBox.addEventFilter(MouseEvent.MOUSE_MOVED, ev -> {
            // noop (could implement hover highlight)
        });
    }

    public void setHand(List<String> cards) {
        Platform.runLater(() -> {
            handBox.getChildren().clear();
            for (String code : cards) {
                ImageView iv = createCardImageView(code);
                handBox.getChildren().add(iv);
            }
        });
    }

    private ImageView createCardImageView(String code) {
        Image img = loadImage("/client/assets/card/" + code + ".png");
        ImageView iv = new ImageView(img);
        iv.setFitWidth(72);
        iv.setFitHeight(104);
        iv.setPreserveRatio(true);
        iv.setSmooth(true);
        iv.setId(code);

        // selection: click toggles translateY and a slight glow using style
        iv.setOnMouseClicked(e -> {
            boolean sel = iv.getTranslateY() < 0;
            if (!sel) {
                iv.setTranslateY(-26);
                iv.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.45), 10, 0, 0, 4);");
            } else {
                iv.setTranslateY(0);
                iv.setStyle("");
            }
        });

        // simple hover: raise slightly
        iv.setOnMouseEntered(e -> {
            if (iv.getTranslateY() == 0) iv.setTranslateY(-6);
        });
        iv.setOnMouseExited(e -> {
            if (iv.getStyle().isEmpty()) iv.setTranslateY(0);
            else if (iv.getTranslateY() == -6) iv.setTranslateY(0);
        });

        return iv;
    }

    private Image loadImage(String path) {
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
            // fallback placeholder (external) so app doesn't crash
            System.out.println("Không tìm thấy ảnh: " + path);
            return new Image("https://via.placeholder.com/72x104.png?text=card");
        }
        return new Image(is);
    }

    private void sortHand() {
        List<ImageView> ordered = handBox.getChildren().stream()
                .map(n -> (ImageView)n)
                .sorted(Comparator.comparingInt(iv -> rankValue(iv.getId())))
                .collect(Collectors.toList());
        Platform.runLater(() -> handBox.getChildren().setAll(ordered));
    }

    private int rankValue(String card) {
        String r = card.replaceAll("[CDHS]", "");
        String[] order = {"3","4","5","6","7","8","9","10","J","Q","K","A","2"};
        for (int i=0;i<order.length;i++) if (order[i].equals(r)) return i;
        return 0;
    }

    /* ---------------- update state from server ---------------- */
    public void setPlayerNames(List<String> names, String myPosition) {
        if (names == null || names.size() < 4) return;
        int myIndex = switch (myPosition) {
            case "p1" -> 0;
            case "p2" -> 1;
            case "p3" -> 2;
            case "p4" -> 3;
            default -> 0;
        };
        List<String> arr = new ArrayList<>();
        for (int i=0;i<4;i++) arr.add(names.get((myIndex + i) % 4));
        arrangedNames = arr;

        Platform.runLater(() -> {
            ((Label)bottomBox.getChildren().get(1)).setText(arr.get(0) + " (Bạn)");
            ((Label)leftBox.getChildren().get(1)).setText(arr.get(1));
            ((Label)topBox.getChildren().get(1)).setText(arr.get(2));
            ((Label)rightBox.getChildren().get(1)).setText(arr.get(3));
        });
    }

    /**
     * payload structure expected:
     * {
     *   "currentPlayer": "<playerId>",
     *   "handsSizes": { "<playerId>": int, ... }  // same order as arrangedNames earlier
     *   "lastPlay": ["3C","5D",...]
     * }
     */
    public void updateState(JsonObject payload) {
        if (payload == null) return;

        // update lastPlay (images)
        if (payload.has("lastPlay") && payload.get("lastPlay").isJsonArray()) {
            JsonArray arr = payload.getAsJsonArray("lastPlay");
            Platform.runLater(() -> {
                lastPlayImages.getChildren().clear();
                if (arr.size() > 0) {
                    for (int i=0;i<arr.size();i++) {
                        String code = arr.get(i).getAsString();
                        ImageView iv = new ImageView(loadImage("/client/assets/card/" + code + ".png"));
                        iv.setFitWidth(52); iv.setFitHeight(76); iv.setPreserveRatio(true);
                        lastPlayImages.getChildren().add(iv);
                    }
                    lastPlayText.setText("");
                } else {
                    lastPlayText.setText("(Chưa có lượt nào)");
                }
            });
        }

        // highlight current player and update hand counts
        String curId = payload.has("currentPlayer") ? payload.get("currentPlayer").getAsString() : null;
        if (payload.has("handsSizes") && payload.get("handsSizes").isJsonObject()) {
            JsonObject hs = payload.getAsJsonObject("handsSizes");
            // hs keys order may vary; we assume arrangedNames correspond to mapping order client uses
            List<String> ids = new ArrayList<>();
            hs.entrySet().forEach(e -> ids.add(e.getKey()));

            // map id->count
            Map<String,Integer> map = new LinkedHashMap<>();
            for (var entry : hs.entrySet()) map.put(entry.getKey(), entry.getValue().getAsInt());

            // update text counts by arrangedNames order
            // arrangedNames currently holds names in [bottom,left,top,right] (strings assigned earlier)
            // but server likely uses playerId keys; this method assumes arrangedNames stored IDs earlier
            // We'll simply iterate map in insertion order and assign counts to boxes in [bottom,left,top,right]
            List<Integer> counts = new ArrayList<>(map.values());
            Platform.runLater(() -> {
                if (counts.size() >= 1) bottomCount.setText(counts.get(0) + " lá");
                if (counts.size() >= 2) leftCount.setText(counts.get(1) + " lá");
                if (counts.size() >= 3) topCount.setText(counts.get(2) + " lá");
                if (counts.size() >= 4) rightCount.setText(counts.get(3) + " lá");

                // reset styles
                bottomBox.setStyle("-fx-background-color: rgba(0,0,0,0.06);");
                leftBox.setStyle("-fx-background-color: rgba(0,0,0,0.06);");
                topBox.setStyle("-fx-background-color: rgba(0,0,0,0.06);");
                rightBox.setStyle("-fx-background-color: rgba(0,0,0,0.06);");

                // highlight by index of curId
                int idx = ids.indexOf(curId);
                if (idx >= 0) {
                    VBox target = switch (idx) {
                        case 0 -> bottomBox;
                        case 1 -> leftBox;
                        case 2 -> topBox;
                        default -> rightBox;
                    };
                    target.setStyle("-fx-border-color: gold; -fx-border-width: 2; -fx-background-color: rgba(255,255,0,0.06);");
                }
            });
        }
    }

    /* -------------- helper UI utilities -------------- */
    public void showPlayResult(boolean success, String message) {
        Platform.runLater(() -> {
            Alert a = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.WARNING, message, ButtonType.OK);
            a.showAndWait();
        });
    }
}
