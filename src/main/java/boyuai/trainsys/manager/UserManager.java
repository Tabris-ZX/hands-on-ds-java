package boyuai.trainsys.manager;

import boyuai.trainsys.config.Config;
import boyuai.trainsys.info.UserInfo;
import boyuai.trainsys.util.Types.UserID;

import java.sql.*;

/**
 * 用户管理器（基于SQLite实现）
 * <p>
 * 负责用户信息的数据库持久化操作，包括：
 * <ul>
 *   <li>用户的增删查改</li>
 *   <li>用户权限管理</li>
 *   <li>密码管理</li>
 * </ul>
 * 
 * @author hands-on-ds
 * @version 1.0
 * @since 1.0
 */

public class UserManager {
    /** 数据库文件路径 */
    private final String dbPath = Config.DATABASE_PATH;
    
    /** 数据库连接对象 */
    private final Connection conn;

    /**
     * 构造函数，建立数据库连接并初始化表结构
     * <p>
     * 创建 user_info 表（如果不存在），包含字段：
     * <ul>
     *   <li>user_id: 用户ID（主键）</li>
     *   <li>username: 用户名</li>
     *   <li>password: 密码</li>
     *   <li>privilege: 权限等级</li>
     * </ul>
     * 
     * @throws SQLException 如果数据库连接失败或表创建失败
     */
    public UserManager() throws SQLException {
        conn = DriverManager.getConnection(Config.CONNECT_URL);
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS user_info(" +
                        "user_id INTEGER PRIMARY KEY," +
                        "username TEXT NOT NULL," +
                        "password TEXT NOT NULL," +
                        "privilege INTEGER NOT NULL)"
        );
    }

    /**
     * 插入或替换用户信息
     * <p>
     * 如果用户ID已存在，则更新该用户信息；否则插入新用户
     * 
     * @param userID 用户ID
     * @param username 用户名
     * @param password 密码
     * @param privilege 权限等级
     * @throws SQLException 如果插入或更新操作失败
     */
    public void insertUser(UserID userID, String username, String password, int privilege) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "INSERT  INTO user_info(user_id, username, password, privilege) VALUES (?, ?, ?, ?)");
        ps.setLong(1, userID.value());
        ps.setString(2, username);
        ps.setString(3, password);
        ps.setInt(4, privilege);
        ps.executeUpdate();
        ps.close();
    }

    /**
     * 检查用户是否存在
     * 
     * @param userID 用户ID
     * @return 如果用户存在返回 true，否则返回 false
     * @throws SQLException 如果查询操作失败
     */
    public boolean existUser(UserID userID) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM user_info WHERE user_id = ?");
        ps.setLong(1, userID.value());
        ResultSet rs = ps.executeQuery();
        boolean exists = rs.next();
        rs.close();
        ps.close();
        return exists;
    }

    /**
     * 根据用户ID查找用户信息
     * 
     * @param userID 用户ID
     * @return 用户信息对象，如果用户不存在返回 null
     * @throws SQLException 如果查询操作失败
     */
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

    /**
     * 删除用户
     * 
     * @param userID 用户ID
     * @throws SQLException 如果删除操作失败
     */
    public void removeUser(UserID userID) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM user_info WHERE user_id = ?");
        ps.setLong(1, userID.value());
        ps.executeUpdate();
        ps.close();
    }

    /**
     * 修改用户权限
     * 
     * @param userID 用户ID
     * @param newPrivilege 新的权限等级
     * @throws SQLException 如果更新操作失败
     */
    public void modifyUserPrivilege(UserID userID, int newPrivilege) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("UPDATE user_info SET privilege = ? WHERE user_id = ?");
        ps.setInt(1, newPrivilege);
        ps.setLong(2, userID.value());
        ps.executeUpdate();
        ps.close();
    }

    /**
     * 修改用户密码
     * 
     * @param userID 用户ID
     * @param newPassword 新密码
     * @throws SQLException 如果更新操作失败
     */
    public void modifyUserPassword(UserID userID, String newPassword) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("UPDATE user_info SET password = ? WHERE user_id = ?");
        ps.setString(1, newPassword);
        ps.setLong(2, userID.value());
        ps.executeUpdate();
        ps.close();
    }

    /**
     * 关闭数据库连接
     * <p>
     * 释放数据库连接资源，建议在系统关闭或重载时调用
     * 
     * @throws SQLException 如果关闭连接失败
     */
    public void close() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
}
