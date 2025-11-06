package boyuai.trainsys.manager;

import boyuai.trainsys.config.Config;
import boyuai.trainsys.core.TrainScheduler;
import boyuai.trainsys.util.Date;
import boyuai.trainsys.util.FixedString;
import java.sql.*;

/**
 * 车票管理器（基于SQLite实现）
 * <p>
 * 负责车票信息的数据库持久化操作，包括：
 * <ul>
 *   <li>查询余票数量</li>
 *   <li>更新余票数量</li>
 *   <li>发布车票</li>
 *   <li>停售车票</li>
 * </ul>
 * 
 * @author hands-on-ds
 * @version 1.0
 * @since 1.0
 */
public class TicketManager {
    /** 数据库文件路径 */
    private final String dbPath = Config.DATABASE_PATH;
    
    /** 数据库连接对象 */
    private final Connection conn;

    /**
     * 构造函数，建立数据库连接并初始化表结构
     * <p>
     * 创建 ticket_info 表（如果不存在），包含字段：
     * <ul>
     *   <li>id: 自增主键</li>
     *   <li>train_id: 车次ID</li>
     *   <li>date: 日期</li>
     *   <li>departure_station: 出发站ID</li>
     *   <li>arrival_station: 到达站ID</li>
     *   <li>seat_num: 余票数量</li>
     *   <li>price: 票价</li>
     *   <li>duration: 运行时长</li>
     * </ul>
     * 
     * @throws SQLException 如果数据库连接失败或表创建失败
     */
    public TicketManager() throws SQLException {
        conn = DriverManager.getConnection(Config.CONNECT_URL);
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS ticket_info (" +
                    "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    "train_id TEXT, " +
                    "date TEXT, " +
                    "departure_station INTEGER, " +
                    "arrival_station INTEGER, " +
                    "seat_num INTEGER, " +
                    "price INTEGER, " +
                    "duration INTEGER)"
        );
    }

    /**
     * 查询余票数量
     * 
     * @param trainID 车次ID
     * @param date 日期
     * @param stationID 出发站ID
     * @return 余票数量，如果查询不到返回 -1
     * @throws SQLException 如果查询操作失败
     */
    public int querySeat(FixedString trainID, Date date, int stationID) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "SELECT seat_num FROM ticket_info WHERE train_id=? AND date=? AND departure_station=?");
        ps.setString(1, trainID.toString());
        ps.setString(2, date.toString());
        ps.setInt(3, stationID);
        ResultSet rs = ps.executeQuery();
        int seatNum = -1;
        if (rs.next()) {
            seatNum = rs.getInt(1);
        }
        rs.close();
        ps.close();
        return seatNum;
    }

    /**
     * 更新余票数量
     * <p>
     * 根据 delta 增加或减少余票数量（delta 为负数表示减少）
     * 
     * @param trainID 车次ID
     * @param date 日期
     * @param stationID 出发站ID
     * @param delta 变化量（正数表示增加，负数表示减少）
     * @return 该区段的票价，如果查询不到返回 -1
     * @throws SQLException 如果更新操作失败
     */
    public int updateSeat(FixedString trainID, Date date, int stationID, int delta) throws SQLException {
        PreparedStatement select = conn.prepareStatement(
                "SELECT id, seat_num, price FROM ticket_info WHERE train_id=? AND date=? AND departure_station=?");
        select.setString(1, trainID.toString());
        select.setString(2, date.toString());
        select.setInt(3, stationID);
        ResultSet rs = select.executeQuery();
        int retPrice = -1;
        if (rs.next()) {
            int id = rs.getInt("id");
            int seatNum = rs.getInt("seat_num");
            retPrice = rs.getInt("price");
            PreparedStatement update = conn.prepareStatement(
                    "UPDATE ticket_info SET seat_num=? WHERE id=?");
            update.setInt(1, seatNum+delta);
            update.setInt(2, id);
            update.executeUpdate();
            update.close();
        }
        rs.close();
        select.close();
        return retPrice;
    }

    /**
     * 开售车票（批量插入）
     * <p>
     * 根据列车调度计划，批量插入该车次在指定日期的所有区段车票信息
     * 
     * @param scheduler 列车调度计划
     * @param date 日期
     * @throws SQLException 如果插入操作失败
     */
    public void releaseTicket(TrainScheduler scheduler, Date date) throws SQLException {
        int passingStationNum = scheduler.getPassingStationNum();
        for (int i = 0; i + 1 < passingStationNum; i++) {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO ticket_info (train_id, date, departure_station, arrival_station, seat_num, price, duration) VALUES (?, ?, ?, ?, ?, ?, ?)"
            );
            ps.setString(1, scheduler.getTrainID().toString());
            ps.setString(2, date.toString());
            ps.setInt(3, scheduler.getStation(i).value());
            ps.setInt(4, scheduler.getStation(i+1).value());
            ps.setInt(5, scheduler.getSeatNum());
            ps.setInt(6, scheduler.getPrice(i));
            ps.setInt(7, scheduler.getDuration(i));
            ps.executeUpdate();
            ps.close();
        }
    }

    /**
     * 停售车票
     * <p>
     * 删除指定车次在指定日期的所有车票信息
     * 
     * @param trainID 车次ID
     * @param date 日期
     * @throws SQLException 如果删除操作失败
     */
    public void expireTicket(FixedString trainID, Date date) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM ticket_info WHERE train_id=? AND date=?");
        ps.setString(1, trainID.toString());
        ps.setString(2, date.toString());
        ps.executeUpdate();
        ps.close();
    }
}
