package boyuai.trainsys.manager;

import boyuai.trainsys.core.TrainScheduler;
import boyuai.trainsys.util.Date;
import boyuai.trainsys.util.FixedString;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketManager {
    private final String dbPath = "data/hands-on-ds.db";
    private final Connection conn;

    public TicketManager() throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS ticket_info (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "train_id TEXT, " +
                    "date TEXT, " +
                    "departure_station INTEGER, " +
                    "arrival_station INTEGER, " +
                    "seat_num INTEGER, " +
                    "price INTEGER, " +
                    "duration INTEGER)"
        );
    }

    // 查询余票数量
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

    // 更新余票数量
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

    // 开售车票（批量插入）
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

    // 停售车票
    public void expireTicket(FixedString trainID, Date date) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM ticket_info WHERE train_id=? AND date=?");
        ps.setString(1, trainID.toString());
        ps.setString(2, date.toString());
        ps.executeUpdate();
        ps.close();
    }
}
