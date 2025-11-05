package boyuai.trainsys.manager;

import boyuai.trainsys.config.Config;
import boyuai.trainsys.core.TrainScheduler;
import boyuai.trainsys.util.FixedString;
import boyuai.trainsys.util.Types.*;
import java.sql.*;

/**
 * 列车调度计划管理器（基于SQLite实现）
 * <p>
 * 负责列车调度计划的数据库持久化操作，包括：
 * <ul>
 *   <li>添加、查询、删除调度计划</li>
 *   <li>数组数据的序列化与反序列化</li>
 * </ul>
 * <p>
 * 注意：由于数据库不支持数组类型，站点列表、时长和票价数组以逗号分隔的字符串形式存储
 * 
 * @author hands-on-ds
 * @version 1.0
 * @since 1.0
 */
public class SchedulerManager {
    /** 数据库文件路径 */
    private final String dbPath = Config.DATABASE_PATH;
    
    /** 数据库连接对象 */
    private final Connection conn;

    /**
     * 构造函数，建立数据库连接并初始化表结构
     * <p>
     * 创建 train_scheduler 表（如果不存在），包含字段：
     * <ul>
     *   <li>train_id: 车次ID（主键）</li>
     *   <li>seat_num: 座位数</li>
     *   <li>passing_num: 经过站点数</li>
     *   <li>stations: 站点列表（逗号分隔）</li>
     *   <li>duration: 时长列表（逗号分隔）</li>
     *   <li>price: 票价列表（逗号分隔）</li>
     * </ul>
     * 
     * @throws SQLException 如果数据库连接失败或表创建失败
     */
    public SchedulerManager() throws SQLException {
        conn = DriverManager.getConnection(Config.CONNECT_URL);
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS train_scheduler (" +
                "train_id TEXT PRIMARY KEY, " +
                "seat_num INTEGER, " +
                "passing_num INTEGER, " +
                "stations TEXT, " +
                "duration TEXT, " +
                "price TEXT)"
        );
    }

    /**
     * 添加或更新列车调度计划
     * <p>
     * 如果车次ID已存在，则更新该计划；否则添加新计划
     * 
     * @param trainID 车次ID
     * @param seatNum 座位数
     * @param passingStationNumber 经过站点数
     * @param stations 站点ID数组
     * @param duration 各区段运行时长数组（长度为 passingStationNumber - 1）
     * @param price 各区段票价数组（长度为 passingStationNumber - 1）
     * @throws SQLException 如果插入或更新操作失败
     */
    public void addScheduler(FixedString trainID, int seatNum, int passingStationNumber, int[] stations, int[] duration, int[] price) throws SQLException {
        // 把int数组stations/price/duration拼接为字符串存TEXT
        String stationStr = joinIntArray(stations, passingStationNumber);
        String durationStr = joinIntArray(duration, passingStationNumber-1);
        String priceStr = joinIntArray(price, passingStationNumber-1);

        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO train_scheduler(train_id, seat_num, passing_num, stations, duration, price) VALUES (?, ?, ?, ?, ?, ?)"
        );
        ps.setString(1, trainID.toString());
        ps.setInt(2, seatNum);
        ps.setInt(3, passingStationNumber);
        ps.setString(4, stationStr); // 用逗号串存TEXT
        ps.setString(5, durationStr);
        ps.setString(6, priceStr);
        ps.executeUpdate();
        ps.close();
    }

    /**
     * 检查指定车次的调度计划是否存在
     * 
     * @param trainID 车次ID
     * @return 如果调度计划存在返回 true，否则返回 false
     * @throws SQLException 如果查询操作失败
     */
    public boolean existScheduler(FixedString trainID) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM train_scheduler WHERE train_id=?");
        ps.setString(1, trainID.toString());
        ResultSet rs = ps.executeQuery();
        boolean exists = rs.next();
        rs.close();
        ps.close();
        return exists;
    }

    /**
     * 查询指定车次的调度计划
     * 
     * @param trainID 车次ID
     * @return 调度计划对象，如果不存在返回 null
     * @throws SQLException 如果查询操作失败
     */
    public TrainScheduler getScheduler(FixedString trainID) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM train_scheduler WHERE train_id=?");
        ps.setString(1, trainID.toString());
        ResultSet rs = ps.executeQuery();
        TrainScheduler scheduler = null;
        if (rs.next()) {
            scheduler = new TrainScheduler();
            scheduler.setTrainID(new TrainID(rs.getString("train_id")));
            scheduler.setSeatNum(rs.getInt("seat_num"));
            scheduler.setDuration(parseIntArray(rs.getString("duration")));
            scheduler.setPrice(parseIntArray(rs.getString("price")));
            // 还原站点
            int[] stationIDs = parseIntArray(rs.getString("stations"));
            for (int id : stationIDs) scheduler.addStation(new StationID(id));
        }
        rs.close();
        ps.close();
        return scheduler;
    }

    /**
     * 删除指定车次的调度计划
     * 
     * @param trainID 车次ID
     * @throws SQLException 如果删除操作失败
     */
    public void removeScheduler(FixedString trainID) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM train_scheduler WHERE train_id=?");
        ps.setString(1, trainID.toString());
        ps.executeUpdate();
        ps.close();
    }

    /**
     * 将整数数组序列化为逗号分隔的字符串
     * <p>
     * 用于将数组数据存储到数据库的 TEXT 字段中
     * 
     * @param arr 整数数组
     * @param len 需要序列化的元素个数
     * @return 逗号分隔的字符串，例如 "1,2,3"
     */
    private String joinIntArray(int[] arr, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            if (i > 0) sb.append(",");
            sb.append(arr[i]);
        }
        return sb.toString();
    }

    /**
     * 将逗号分隔的字符串反序列化为整数数组
     * <p>
     * 用于从数据库的 TEXT 字段中恢复数组数据
     * 
     * @param s 逗号分隔的字符串，例如 "1,2,3"
     * @return 整数数组，如果字符串为空返回空数组
     */
    private int[] parseIntArray(String s) {
        if (s == null || s.isEmpty()) return new int[0];
        String[] parts = s.split(",");
        int[] arr = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            arr[i] = Integer.parseInt(parts[i]);
        }
        return arr;
    }
}
