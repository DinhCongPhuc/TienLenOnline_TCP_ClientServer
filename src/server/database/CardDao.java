    package server.database;

    import java.sql.*;
    import java.io.InputStream;
    import javafx.scene.image.Image;

    public class CardDao {

        private Connection conn;

        public CardDao() throws SQLException {
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/tienlen_db", "root", ""  // sửa theo DB của bạn
            );
        }

        public Image loadCardImage(String code) throws SQLException {
            String sql = "SELECT image_path FROM cards WHERE code=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String path = rs.getString("image_path");
                // Nếu đường dẫn là đường dẫn tuyệt đối trên máy
                return new Image("file:///" + path.replace("\\", "/"));
            }
            return null;
        }
    }
