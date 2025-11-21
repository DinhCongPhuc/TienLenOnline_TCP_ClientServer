package client.network;

import com.google.gson.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class NetworkClient {
    private String host;
    private int port;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Consumer<String> messageHandler;
    private Gson gson = new Gson();

    // Map type -> list handler
    private Map<String, List<java.util.function.Consumer<JsonObject>>> handlers = new ConcurrentHashMap<>();
    private java.util.function.Consumer<JsonObject> globalHandler;

    public NetworkClient(String host, int port) {
        this.host = host;
        this.port = port;
        connect();
    }

    private void connect() {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Thread lắng nghe server
            new Thread(this::listen).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listen() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                JsonObject msg = gson.fromJson(line, JsonObject.class);
                String type = msg.get("type").getAsString();

                // Gọi handler theo type
                if (handlers.containsKey(type)) {
                    for (var h : handlers.get(type)) {
                        h.accept(msg);
                    }
                }

                // Gọi global handler (AppController)
                if (globalHandler != null) {
                    globalHandler.accept(msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Gửi message lên server
    public void send(String type, Object payload) {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", type);
        msg.add("payload", gson.toJsonTree(payload));
        out.println(gson.toJson(msg));
    }

    // Đăng ký handler cho một type cụ thể
    public void addHandler(String type, java.util.function.Consumer<JsonObject> handler) {
        handlers.computeIfAbsent(type, k -> new ArrayList<>()).add(handler);
    }

    // Đăng ký handler tổng quát (AppController dùng)
    public void setOnMessage(java.util.function.Consumer<JsonObject> handler) {
        this.globalHandler = handler;
    }

    
}