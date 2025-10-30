package boyuai.trainsys.manager;

import boyuai.trainsys.info.UserInfo;
import boyuai.trainsys.util.Types.UserID;

import java.sql.*;

/**
 * 用户管理器（基于SQLite实现）
 */
public class UserManager {
    private final String dbPath = "data/hands-on-ds.db";
    private Connection conn;

    /**
     * 构造函数，建立数据库连接并确保表存在
     */
    public UserManager() throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS user_info(" +
                        "user_id INTEGER PRIMARY KEY," +
                        "username TEXT NOT NULL," +
                        "password TEXT NOT NULL," +
                        "privilege INTEGER NOT NULL)"
        );
    }

    /** 插入用户 */
    public void insertUser(UserID userID, String username, String password, int privilege) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "INSERT OR REPLACE INTO user_info(user_id, username, password, privilege) VALUES (?, ?, ?, ?)");
        ps.setLong(1, userID.value());
        ps.setString(2, username);
        ps.setString(3, password);
        ps.setInt(4, privilege);
        ps.executeUpdate();
        ps.close();
    }

    /** 检查用户是否存在 */
    public boolean existUser(UserID userID) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM user_info WHERE user_id = ?");
        ps.setLong(1, userID.value());
        ResultSet rs = ps.executeQuery();
        boolean exists = rs.next();
        rs.close();
        ps.close();
        return exists;
    }

    /** 查找用户 */
    public UserInfo findUser(UserID userID) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT username, password, privilege FROM user_info WHERE user_id = ?");
        ps.setLong(1, userID.value());
        ResultSet rs = ps.executeQuery();
        UserInfo user = null;
        if (rs.next()) {
            String username = rs.getString(1);
            String password = rs.getString(2);
            int privilege = rs.getInt(3);
            user = new UserInfo(userID, username, password, privilege);
        }
        rs.close();
        ps.close();
        return user;
    }

    /** 删除用户 */
    public void removeUser(UserID userID) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM user_info WHERE user_id = ?");
        ps.setLong(1, userID.value());
        ps.executeUpdate();
        ps.close();
    }

    /** 修改用户权限 */
    public void modifyUserPrivilege(UserID userID, int newPrivilege) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("UPDATE user_info SET privilege = ? WHERE user_id = ?");
        ps.setInt(1, newPrivilege);
        ps.setLong(2, userID.value());
        ps.executeUpdate();
        ps.close();
    }

    /** 修改用户密码 */
    public void modifyUserPassword(UserID userID, String newPassword) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("UPDATE user_info SET password = ? WHERE user_id = ?");
        ps.setString(1, newPassword);
        ps.setLong(2, userID.value());
        ps.executeUpdate();
        ps.close();
    }

    /** 关闭数据库连接，建议在系统关闭/重载时调用 */
    public void close() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
}
