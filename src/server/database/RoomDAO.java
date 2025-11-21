package server.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public int createRoom(String roomName, String createdBy) {
        String sql = "INSERT INTO rooms (room_name, status, created_by, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, roomName);
            stmt.setString(2, "waiting");
            stmt.setString(3, createdBy);      // dùng String id
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // trả về room_id
            }

        } catch (SQLException e) {
            System.err.println("[DB] Lỗi khi tạo phòng: " + e.getMessage());
        }
        return -1;
    }


    public List<String> getAllRooms() {
        List<String> rooms = new ArrayList<>();
        String sql = "SELECT room_name, status FROM rooms";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String name = rs.getString("room_name");
                String status = rs.getString("status");
                rooms.add(name + " (" + status + ")");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Lỗi khi lấy danh sách phòng: " + e.getMessage());
        }
        return rooms;
    }
}
