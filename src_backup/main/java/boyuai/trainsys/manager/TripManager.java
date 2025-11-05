package boyuai.trainsys.manager;

import boyuai.trainsys.config.Config;
import boyuai.trainsys.info.TripInfo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 行程管理器（基于SQLite实现）
 * <p>
 * 负责用户行程（订单）信息的数据库持久化操作，包括：
 * <ul>
 *   <li>添加行程记录</li>
 *   <li>查询用户行程</li>
 *   <li>删除行程记录</li>
 * </ul>
 * 
 */
public class TripManager {
    /** 数据库文件路径 */
    private final String dbPath = Config.DATABASE_PATH;
    
    /** 数据库连接对象 */
    private final Connection conn;

    /**
     * 构造函数，建立数据库连接并初始化表结构
     * <p>
     * 创建 trip_info 表（如果不存在），包含字段：
     * <ul>
     *   <li>id: 自增主键</li>
     *   <li>user_id: 用户ID</li>
     *   <li>train_id: 车次ID</li>
     *   <li>departure_station: 出发站ID</li>
     *   <li>arrival_station: 到达站ID</li>
     *   <li>type: 行程类型（正数表示购票，负数表示退票）</li>
     *   <li>duration: 运行时长</li>
     *   <li>price: 票价</li>
     *   <li>date: 日期</li>
     * </ul>
     * 
     * @throws SQLException 如果数据库连接失败或表创建失败
     */
    public TripManager() throws SQLException {
        conn = DriverManager.getConnection(Config.CONNECT_URL);
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS trip_info (" +
                        "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                        "user_id INTEGER, " +
                        "train_id TEXT, " +
                        "departure_station INTEGER, " +
                        "arrival_station INTEGER, " +
                        "type INTEGER, " +
                        "duration INTEGER, " +
                        "price INTEGER, " +
                        "date TEXT)"
        );
    }

    /**
     * 添加行程记录
     * <p>
     * 将用户的行程（订单）信息插入到数据库中
     * 
     * @param userID 用户ID
     * @param trip 行程信息
     * @throws SQLException 如果插入操作失败
     */
    public void addTrip(long userID, TripInfo trip) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO trip_info (user_id, train_id, departure_station, arrival_station, type, duration, price, date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
        );
        ps.setLong(1, userID);
        ps.setString(2, trip.getTrainID().toString());
        ps.setInt(3, trip.getDepartureStation().value());
        ps.setInt(4, trip.getArrivalStation().value());
        ps.setInt(5, trip.getType());
        ps.setInt(6, trip.getDuration());
        ps.setInt(7, trip.getPrice());
        ps.setString(8, trip.getDate().toString());
        ps.executeUpdate();
        ps.close();
    }

    /**
     * 查询用户的所有行程
     * <p>
     * 从数据库中查询指定用户的所有行程记录
     * 
     * @param userID 用户ID
     * @return 行程信息列表，如果用户没有行程则返回空列表
     * @throws SQLException 如果查询操作失败
     */
    public List<TripInfo> queryTrip(long userID) throws SQLException {
        List<TripInfo> trips = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM trip_info WHERE user_id = ?");
        ps.setLong(1, userID);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            TripInfo t = new TripInfo();
            t.setTrainID(new boyuai.trainsys.util.Types.TrainID(rs.getString("train_id")));
            t.setDepartureStation(new boyuai.trainsys.util.Types.StationID(rs.getInt("departure_station")));
            t.setArrivalStation(new boyuai.trainsys.util.Types.StationID(rs.getInt("arrival_station")));
            t.setType(rs.getInt("type"));
            t.setDuration(rs.getInt("duration"));
            t.setPrice(rs.getInt("price"));
            t.setDate(new boyuai.trainsys.util.Date(rs.getString("date")));
            trips.add(t);
        }
        rs.close();
        ps.close();
        return trips;
    }

    /**
     * 删除用户的某个行程记录
     * <p>
     * 根据用户ID和行程信息删除对应的行程记录
     * 
     * @param userID 用户ID
     * @param trip 行程信息
     * @throws SQLException 如果删除操作失败
     */
    public void removeTrip(long userID, TripInfo trip) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM trip_info WHERE user_id=? AND train_id=? AND departure_station=? AND arrival_station=? AND type=? AND date=?"
        );
        ps.setLong(1, userID);
        ps.setString(2, trip.getTrainID().toString());
        ps.setInt(3, trip.getDepartureStation().value());
        ps.setInt(4, trip.getArrivalStation().value());
        ps.setInt(5, trip.getType());
        ps.setString(6, trip.getDate().toString());
        ps.executeUpdate();
        ps.close();
    }
}
