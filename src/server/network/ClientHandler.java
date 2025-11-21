package server.network;

import server.controller.ServerController;
import server.model.Player;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ServerController controller;

    public ClientHandler(Socket socket, ServerController controller) {
        this.socket = socket;
        this.controller = controller;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            Player me = new Player(socket, in, out);
            controller.addPlayer(me);

            String line;
            while ((line = in.readLine()) != null) {
                controller.handleMessage(me, line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
