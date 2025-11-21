package server.model;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class Player {
    private String id;
    private String name;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private List<String> hand = new ArrayList<>();
    private boolean ready = false;

    public Player(Socket socket, BufferedReader in, PrintWriter out) {
        this.id = UUID.randomUUID().toString();
        this.socket = socket;
        this.in = in;
        this.out = out;
    }

    // getters / setters
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String n) { this.name = n; }
    public PrintWriter getOut() { return out; }
    public List<String> getHand() { return hand; }
    public boolean isReady() { return ready; }
    public void setReady(boolean ready) { this.ready = ready; }
}

