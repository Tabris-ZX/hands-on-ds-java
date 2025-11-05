package boyuai.trainsys.manager;

import boyuai.trainsys.config.Config;
import boyuai.trainsys.util.Types.TrainID;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 路线区段管理器（基于SQLite实现）
 * <p>
 * 负责路线区段和站点连通分量的数据库持久化操作，包括：
 * <ul>
 *   <li>路线区段的增删查改</li>
 *   <li>站点连通分量的保存和清除</li>
 *   <li>数据库表的初始化</li>
 * </ul>
 * 
 * @author hands-on-ds
 * @version 1.0
 * @since 1.0
 */
public class RouteSectionManager {
    /** 数据库文件路径 */
    private final String dbPath = Config.DATABASE_PATH;
    
    /** 数据库连接对象 */
    private final Connection conn;

    /**
     * 路线区段数据类（用于从数据库加载）
     * <p>
     * 用于封装从数据库查询出的路线区段数据，避免直接使用 ResultSet
     */
    public static class RouteSectionData {
        /** 车次ID */
        public final TrainID trainID;
        /** 出发站ID */
        public final int departureID;
        /** 到达站ID */
        public final int arrivalID;
        /** 票价 */
        public final int price;
        /** 运行时长（分钟） */
        public final int duration;

        /**
         * 构造路线区段数据对象
         * 
         * @param trainID 车次ID
         * @param departureID 出发站ID
         * @param arrivalID 到达站ID
         * @param price 票价
         * @param duration 运行时长（分钟）
         */
        public RouteSectionData(TrainID trainID, int departureID, int arrivalID, int price, int duration) {
            this.trainID = trainID;
            this.departureID = departureID;
            this.arrivalID = arrivalID;
            this.price = price;
            this.duration = duration;
        }
    }

    /**
     * 构造函数，建立数据库连接并初始化表结构
     * 
     * @throws SQLException 如果数据库连接失败或表创建失败
     */
    public RouteSectionManager() throws SQLException {
        conn = DriverManager.getConnection(Config.CONNECT_URL);
        initDB();
    }

    /**
     * 初始化数据库表结构
     * <p>
     * 创建以下表（如果不存在）：
     * <ul>
     *   <li>route_section: 存储路线区段信息</li>
     *   <li>station_component: 存储站点连通分量（并查集根节点）</li>
     * </ul>
     * 
     * @throws SQLException 如果表创建失败
     */
    private void initDB() throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS route_section (" +
                "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                "train_id TEXT, " +
                "departure_id INTEGER, " +
                "arrival_id INTEGER, " +
                "price INTEGER, " +
                "duration INTEGER)");
        // 记录每个站点所属的连通分量（并查集根）
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS station_component (" +
                "station_id INTEGER PRIMARY KEY, " +
                "component_id INTEGER)");
        stmt.close();
    }

    /**
     * 保存路线区段到数据库
     * <p>
     * 将一条路线区段信息插入到 route_section 表中
     * 
     * @param trainID 车次ID
     * @param departureID 出发站ID
     * @param arrivalID 到达站ID
     * @param duration 运行时长（分钟）
     * @param price 票价
     * @throws SQLException 如果插入操作失败
     */
    public void saveSection(TrainID trainID, int departureID, int arrivalID, int duration, int price) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO route_section (train_id, departure_id, arrival_id, price, duration) VALUES (?, ?, ?, ?, ?)"
        );
        ps.setString(1, trainID.toString());
        ps.setInt(2, departureID);
        ps.setInt(3, arrivalID);
        ps.setInt(4, price);
        ps.setInt(5, duration);
        ps.executeUpdate();
        ps.close();
    }

    /**
     * 从数据库加载所有路线区段
     * <p>
     * 查询 route_section 表中的所有记录，并转换为 RouteSectionData 对象列表
     * 
     * @return 路线区段数据列表，如果表为空则返回空列表
     * @throws SQLException 如果查询操作失败
     */
    public List<RouteSectionData> loadAllSections() throws SQLException {
        List<RouteSectionData> sections = new ArrayList<>();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM route_section");
        while (rs.next()) {
            RouteSectionData section = new RouteSectionData(
                new TrainID(rs.getString("train_id")),
                rs.getInt("departure_id"),
                rs.getInt("arrival_id"),
                rs.getInt("price"),
                rs.getInt("duration")
            );
            sections.add(section);
        }
        rs.close();
        stmt.close();
        return sections;
    }

    /**
     * 保存站点连通分量到数据库
     * <p>
     * 将站点所属的连通分量（并查集根节点）保存到 station_component 表中。
     * 如果该站点已存在记录，则更新其连通分量ID
     * 
     * @param stationId 站点ID
     * @param componentId 连通分量ID（并查集根节点）
     * @throws SQLException 如果插入或更新操作失败
     */
    public void saveStationComponent(int stationId, int componentId) throws SQLException {
        //"INSERT INTO station_component (station_id, component_id) VALUES (?, ?) " +
        //            "ON CONFLICT(station_id) DO UPDATE SET component_id = excluded.component_id"
        PreparedStatement ps = conn.prepareStatement("INSERT INTO station_component (station_id, component_id)"
                + "VALUES (?, ?);\n"

        );
        ps.setInt(1, stationId);
        ps.setInt(2, componentId);
        ps.executeUpdate();
        ps.close();
    }

    /**
     * 清空站点连通分量表
     * <p>
     * 删除 station_component 表中的所有记录，通常在重新构建图结构时使用
     * 
     * @throws SQLException 如果删除操作失败
     */
    public void clearStationComponents() throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("DELETE FROM station_component");
        stmt.close();
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

