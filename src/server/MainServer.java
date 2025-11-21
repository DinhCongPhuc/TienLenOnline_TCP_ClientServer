package server;

import server.network.ClientHandler;
import server.controller.ServerController;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainServer {
    private static final int PORT = 12345;

    public static void main(String[] args) throws Exception {
        ServerController controller = new ServerController();
        ExecutorService exec = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server listening on " + PORT);
            while (true) {
                Socket sock = serverSocket.accept();
                exec.submit(new ClientHandler(sock, controller));
            }
        }
    }
}
