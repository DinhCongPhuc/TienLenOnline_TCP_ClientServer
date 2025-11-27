package server.database;

import java.sql.*;

public class UserDAO {

    public boolean registerUser(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("[DB] Không thể kết nối database!");
                return false;
            }
            
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            int rows = stmt.executeUpdate();
            System.out.println("🔵 [DB] Đăng ký '" + username + "' → INSERT " + rows + " rows");
            
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("🔴 [DB] LỖI ĐĂNG KÝ '" + username + "': " + e.getMessage());
            return false;
        } finally {
            // ⭐ ĐÓNG CONNECTION ĐÚNG CÁCH
            closeResources(stmt, conn);
        }
    }

    public boolean loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("[DB] Không thể kết nối database!");
                return false;
            }
            
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            rs = stmt.executeQuery();
            boolean found = rs.next();
            
            System.out.println("🔵 [DB] Login '" + username + "' → TÌM THẤY: " + found);
            return found;
        } catch (SQLException e) {
            System.err.println("🔴 [DB] LỖI LOGIN '" + username + "': " + e.getMessage());
            return false;
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
    
    // ⭐ METHOD ĐÓNG RESOURCES AN TOÀN
    private void closeResources(PreparedStatement stmt, Connection conn) {
        try { if (stmt != null) stmt.close(); } catch (Exception e) {}
        try { if (conn != null) conn.close(); } catch (Exception e) {}
    }
    
    private void closeResources(ResultSet rs, PreparedStatement stmt, Connection conn) {
        try { if (rs != null) rs.close(); } catch (Exception e) {}
        closeResources(stmt, conn);
    }
}