package server.controller;

import com.google.gson.*;

import server.database.UserDAO;
import server.model.*;
import server.view.ServerView;

import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import server.database.RoomDAO;;


public class ServerController {
    private Map<String, Player> players = new ConcurrentHashMap<>();
    private Map<String, Room> rooms = new ConcurrentHashMap<>();
    private Gson gson = new Gson();
    private UserDAO userDAO = new UserDAO();  // ⭐ Dùng UserDAO
    

    public void handleMessage(Player me, String line) {
        JsonObject msg = gson.fromJson(line, JsonObject.class);
        String type = msg.get("type").getAsString();
        JsonObject payload = msg.has("payload") ? msg.get("payload").getAsJsonObject() : new JsonObject();

        switch (type) {

            /* ============================
             *        REGISTER
             * ============================ */
            case "REGISTER": {
                RegisterPayload reg = gson.fromJson(payload, RegisterPayload.class);

                boolean success = userDAO.registerUser(reg.username, reg.password);

                if (success) {
                    send(me.getOut(), "REGISTER_OK",
                            gson.toJsonTree(Map.of("message", "Đăng ký thành công!")));
                } else {
                    send(me.getOut(), "REGISTER_FAIL",
                            gson.toJsonTree(Map.of("message", "Tên tài khoản đã tồn tại!")));
                }
                break;
            }

            /* ============================
             *          LOGIN
             * ============================ */
            case "LOGIN": {
                String user = payload.get("username").getAsString();
                String pass = payload.get("password").getAsString();

                boolean ok = userDAO.loginUser(user, pass);

                if (ok) {
                    me.setName(user);
                    send(me.getOut(), "LOGIN_OK",
                            gson.toJsonTree(Map.of("message", "Đăng nhập thành công!", "user", user)));
                } else {
                    send(me.getOut(), "LOGIN_FAIL",
                            gson.toJsonTree(Map.of("message", "Sai tài khoản hoặc mật khẩu!")));
                }
                break;
            }
           case "CREATE_ROOM":
                String roomName = payload.get("roomName").getAsString();

                // khởi tạo RoomDAO
                RoomDAO roomDAO = new RoomDAO();

                // lưu vào DB
                int dbRoomId = roomDAO.createRoom(roomName, me.getId());
                if (dbRoomId == -1) {
                    send(me.getOut(), "ERROR", gson.toJsonTree(Map.of("message","Tạo phòng thất bại!")));
                    break;
                }

                // tạo room trong memory
                String roomId = "room-" + UUID.randomUUID().toString().substring(0,6);
                Room r = new Room(roomId, roomName, gson);
                r.addPlayer(me);
                rooms.put(roomId, r);

                // gửi cho client
                send(me.getOut(), "ROOM_CREATED", gson.toJsonTree(Map.of("roomId", roomId)));

                // broadcast cập nhật phòng
                broadcastRoomUpdate(r);
                break;



            case "JOIN_ROOM":
                String rid = payload.get("roomId").getAsString();
                Room room = rooms.get(rid);
                if (room == null) {
                    send(me.getOut(), "ERROR", gson.toJsonTree(Map.of("message","Room not found")));
                    break;
                }
                room.addPlayer(me);
                broadcastRoomUpdate(room);
                break;
            case "READY":
                Room rr = findPlayerRoom(me);
                if (rr != null) {
                    me.setReady(true);
                    broadcastRoomUpdate(rr);
                    if (rr.allReadyAndFull()) {
                        rr.startGame();
                    }
                }
                break;
            case "PLAY_CARDS":
                Room rplay = findPlayerRoom(me);
                if (rplay != null && rplay.getGame() != null) {
                    List<String> cards = new ArrayList<>();
                    payload.get("cards").getAsJsonArray().forEach(e -> cards.add(e.getAsString()));
                    rplay.getGame().processPlay(me, cards);
                }
                break;
            case "PASS":
                Room rpass = findPlayerRoom(me);
                if (rpass != null && rpass.getGame() != null) {
                    rpass.getGame().processPass(me);
                }
                break;
            case "CHAT":
                Room rchat = findPlayerRoom(me);
                if (rchat != null) {
                    rchat.broadcast("CHAT_BROADCAST",
                        gson.toJsonTree(Map.of("from", me.getName(), "text", payload.get("text").getAsString())));
                }
                break;
           default:
                send(me.getOut(), "ERROR",
                        gson.toJsonTree(Map.of("message", "Unknown type " + type)));
        }

        ServerView.log("Message from " + me.getName() + ": " + type);
    }

    private void send(PrintWriter out, String type, Object payload) {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", type);
        msg.add("payload", gson.toJsonTree(payload));
        out.println(gson.toJson(msg));
    }

    private void broadcastRoomUpdate(Room r) {
        r.broadcast("ROOM_UPDATE", gson.toJsonTree(Map.of(
                "players", r.playerNames(),
                "status", r.getState().name()
        )));
    }

    private Room findPlayerRoom(Player p) {
        return rooms.values().stream().filter(r -> r.hasPlayer(p)).findFirst().orElse(null);
    }

    public void addPlayer(Player p) {
        players.put(p.getId(), p);
    }
}

