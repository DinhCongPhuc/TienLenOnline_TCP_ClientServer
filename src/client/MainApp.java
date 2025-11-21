package client;

import client.controller.AppController;
import client.network.NetworkClient;
import javafx.application.Application;
import javafx.stage.Stage;
import client.network.*;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        NetworkClient client = new NetworkClient("localhost", 12345);
        AppController appController = new AppController(stage, client);
        appController.showLogin();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
