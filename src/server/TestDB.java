package server;
import server.database.DatabaseConnection;

public class TestDB {
    public static void main(String[] args) {
        DatabaseConnection.getConnection();
    }
}
