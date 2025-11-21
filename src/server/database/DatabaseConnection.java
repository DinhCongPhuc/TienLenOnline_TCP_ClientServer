package server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/tienlen_db";
    private static final String USER = "root"; // user mặc định của XAMPP
    private static final String PASSWORD = ""; // để trống nếu bạn chưa đặt mật khẩu

    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DB] Kết nối MySQL thành công!");
            } catch (ClassNotFoundException e) {
                System.err.println("[DB] Không tìm thấy driver JDBC!");
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("[DB] Lỗi kết nối MySQL!");
                e.printStackTrace();
            }
        }
        return connection;
    }
}
