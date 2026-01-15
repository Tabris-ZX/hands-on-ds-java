package boyuai.trainsys.manager;

import boyuai.trainsys.config.StaticConfig;
import boyuai.trainsys.util.TrainScheduler;
import boyuai.trainsys.util.Time;
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
    private final String dbPath = StaticConfig.DATABASE_PATH;
    
    /** 数据库连接对象 */
    private final Connection conn;

    /**
     * 构造函数，建立数据库连接并初始化表结构
     * <p>
     * 创建 ticket_info 表（如果不存在），包含字段：
     * <ul>
     *   <li>id: 自增主键</li>
     *   <li>train_id: 车次ID</li>
     *   <li>departure_time: 出发时间（格式 HH:MM MM-DD）</li>
     *   <li>departure_station: 出发站ID</li>
     *   <li>arrival_station: 到达站ID</li>
     *   <li>seat_num: 余票数量</li>
     *   <li>price: 票价</li>
     *   <li>duration: 运行时长（分钟）</li>
     * </ul>
     * 
     * @throws SQLException 如果数据库连接失败或表创建失败
     */
    public TicketManager() throws SQLException {
        conn = DriverManager.getConnection(StaticConfig.CONNECT_URL);
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS ticket_info (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "train_id TEXT, " +
                    "departure_time TEXT, " +
                    "departure_station INTEGER, " +
                    "arrival_station INTEGER, " +
                    "seat_num INTEGER, " +
                    "price INTEGER, " +
                    "duration INTEGER, " +
                    "UNIQUE(train_id, departure_time, departure_station))"
        );
    }

    /**
     * 查询余票数量
     * 
     * @param trainID 车次ID
     * @param departureTime 出发时间
     * @param stationID 出发站ID
     * @return 余票数量，如果查询不到返回 -1
     * @throws SQLException 如果查询操作失败
     */
    public int querySeat(FixedString trainID, Time departureTime, int stationID) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "SELECT seat_num FROM ticket_info WHERE train_id=? AND departure_time=? AND departure_station=?");
        ps.setString(1, trainID.toString());
        ps.setString(2, departureTime.toString());
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
     * @param departureTime 出发时间
     * @param stationID 出发站ID
     * @param delta 变化量（正数表示增加，负数表示减少）
     * @return 该区段的票价，如果查询不到返回 -1
     * @throws SQLException 如果更新操作失败
     */
    public int updateSeat(FixedString trainID, Time departureTime, int stationID, int delta) throws SQLException {
        PreparedStatement select = conn.prepareStatement(
                "SELECT id, seat_num, price FROM ticket_info WHERE train_id=? AND departure_time=? AND departure_station=?");
        select.setString(1, trainID.toString());
        select.setString(2, departureTime.toString());
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
     * 根据列车调度计划，批量插入该车次在指定时间的所有区段车票信息
     * 
     * @param scheduler 列车调度计划
     * @param baseTime 基准时间（用于确定具体哪一天的车次）
     * @throws SQLException 如果插入操作失败
     */
    public void releaseTicket(TrainScheduler scheduler, Time baseTime) throws SQLException {
        int passingStationNum = scheduler.getPassingStationNum();
        for (int i = 0; i + 1 < passingStationNum; i++) {
            // 计算该站的出发时间
            Time departureTime = scheduler.getDepartureTimeAt(i, baseTime);
            
            // 使用 INSERT OR REPLACE 避免重复数据
            PreparedStatement ps = conn.prepareStatement(
                "INSERT OR REPLACE INTO ticket_info (train_id, departure_time, departure_station, arrival_station, seat_num, price, duration) VALUES (?, ?, ?, ?, ?, ?, ?)"
            );
            ps.setString(1, scheduler.getTrainID().toString());
            ps.setString(2, departureTime.toString());
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
     * 删除指定车次在指定时间的所有车票信息
     * 
     * @param trainID 车次ID
     * @param departureTime 出发时间
     * @throws SQLException 如果删除操作失败
     */
    public void expireTicket(FixedString trainID, Time departureTime) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM ticket_info WHERE train_id=? AND departure_time=?");
        ps.setString(1, trainID.toString());
        ps.setString(2, departureTime.toString());
        ps.executeUpdate();
        ps.close();
    }

    /**
     * 查询所有已发售的车票
     * <p>
     * 返回所有已发售的车票信息（seat_num >= 0 表示已发售）
     * 
     * @return 车票信息列表，每个元素包含 [train_id, departure_time, departure_station, arrival_station, seat_num, price, duration]
     * @throws SQLException 如果查询操作失败
     */
    public java.util.List<Object[]> getAllReleasedTickets() throws SQLException {
        java.util.List<Object[]> tickets = new java.util.ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
                "SELECT train_id, departure_time, departure_station, arrival_station, seat_num, price, duration " +
                "FROM ticket_info WHERE seat_num >= 0 ORDER BY train_id, departure_time, departure_station");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Object[] ticket = new Object[7];
            ticket[0] = rs.getString("train_id");
            ticket[1] = rs.getString("departure_time");
            ticket[2] = rs.getInt("departure_station");
            ticket[3] = rs.getInt("arrival_station");
            ticket[4] = rs.getInt("seat_num");
            ticket[5] = rs.getInt("price");
            ticket[6] = rs.getInt("duration");
            tickets.add(ticket);
        }
        rs.close();
        ps.close();
        return tickets;
    }

    /**
     * 查询所有车票（包括已发售和未发售的，管理员使用）
     * <p>
     * 返回所有车票信息
     * 
     * @return 车票信息列表，每个元素包含 [train_id, departure_time, departure_station, arrival_station, seat_num, price, duration]
     * @throws SQLException 如果查询操作失败
     */
    public java.util.List<Object[]> getAllTickets() throws SQLException {
        java.util.List<Object[]> tickets = new java.util.ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
                "SELECT train_id, departure_time, departure_station, arrival_station, seat_num, price, duration " +
                "FROM ticket_info ORDER BY train_id, departure_time, departure_station");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Object[] ticket = new Object[7];
            ticket[0] = rs.getString("train_id");
            ticket[1] = rs.getString("departure_time");
            ticket[2] = rs.getInt("departure_station");
            ticket[3] = rs.getInt("arrival_station");
            ticket[4] = rs.getInt("seat_num");
            ticket[5] = rs.getInt("price");
            ticket[6] = rs.getInt("duration");
            tickets.add(ticket);
        }
        rs.close();
        ps.close();
        return tickets;
    }
}
