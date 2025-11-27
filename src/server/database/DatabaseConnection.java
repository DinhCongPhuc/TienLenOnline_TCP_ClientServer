package server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/tienlen_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        try {
            // ⭐ TẠO CONNECTION MỚI MỖI LẦN GỌI!
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[DB] ✅ Tạo connection MỚI thành công!");
            return conn;
        } catch (SQLException e) {
            System.err.println("[DB] 🔴 LỖI kết nối MySQL: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}