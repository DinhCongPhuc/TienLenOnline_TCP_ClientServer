package client.model;

import java.util.*;

public class GameState {
    private String currentPlayerId;
    private List<String> lastPlay;          // danh sách lá vừa đánh
    private Map<String, Integer> handSizes; // số lá còn lại của từng người

    public GameState() {
        this.lastPlay = new ArrayList<>();
        this.handSizes = new HashMap<>();
    }

    public String getCurrentPlayerId() { return currentPlayerId; }
    public void setCurrentPlayerId(String id) { this.currentPlayerId = id; }

    public List<String> getLastPlay() { return lastPlay; }
    public void setLastPlay(List<String> lastPlay) { this.lastPlay = lastPlay; }

    public Map<String, Integer> getHandSizes() { return handSizes; }
    public void setHandSizes(Map<String, Integer> handSizes) { this.handSizes = handSizes; }
}
