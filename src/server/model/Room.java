package server.model;

import com.google.gson.*;
import java.util.*;

public class Room {
    private String id;
    private String name;
    private List<Player> players = Collections.synchronizedList(new ArrayList<>());
    private RoomState state = RoomState.WAITING;
    private Game game;
    private Gson gson;

    public Room(String id, String name, Gson gson) {
        this.id = id;
        this.name = name;
        this.gson = gson;
    }

    public void setState(RoomState state) {
    this.state = state;
    }   

    public void addPlayer(Player p) { if (players.size() < 4) players.add(p); }
    public boolean hasPlayer(Player p) { return players.contains(p); }
    public List<String> playerNames() {
        List<String> l = new ArrayList<>();
        for (Player p: players) l.add(p.getName());
        return l;
    }
    public boolean allReadyAndFull() { return players.size()==4 && players.stream().allMatch(Player::isReady); }
    
    public void broadcast(String type, JsonElement payload) {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", type);
        msg.add("payload", payload);

        String json = gson.toJson(msg);
        for (Player pl : players) {
            pl.getOut().println(json);
        }
    }

    public void startGame() {
        this.state = RoomState.PLAYING;
        this.game = new Game(this, gson);
        this.game.start();
    }

    // getters
    public RoomState getState() { return state; }
    public Game getGame() { return game; }
    public List<Player> getPlayers() { return players; }
}

