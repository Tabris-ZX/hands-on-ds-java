package boyuai.trainsys.manager;

import boyuai.trainsys.core.TrainScheduler;
import boyuai.trainsys.util.FixedString;
import boyuai.trainsys.util.Types.*;
import java.sql.*;

/** 列车调度管理器（基于SQLite重写） */
public class SchedulerManager {
    private final String dbPath = "data/hands-on-ds.db";
    private final Connection conn;

    public SchedulerManager() throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
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

    /** 添加一个运行计划 */
    public void addScheduler(FixedString trainID, int seatNum, int passingStationNumber, int[] stations, int[] duration, int[] price) throws SQLException {
        // 把int数组stations/price/duration拼接为字符串存TEXT
        String stationStr = joinIntArray(stations, passingStationNumber);
        String durationStr = joinIntArray(duration, passingStationNumber-1);
        String priceStr = joinIntArray(price, passingStationNumber-1);

        PreparedStatement ps = conn.prepareStatement(
            "INSERT OR REPLACE INTO train_scheduler(train_id, seat_num, passing_num, stations, duration, price) VALUES (?, ?, ?, ?, ?, ?)"
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

    /** 查询某个ID的运行计划是否存在 */
    public boolean existScheduler(FixedString trainID) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM train_scheduler WHERE train_id=?");
        ps.setString(1, trainID.toString());
        ResultSet rs = ps.executeQuery();
        boolean exists = rs.next();
        rs.close();
        ps.close();
        return exists;
    }

    /** 查询某个ID的运行计划 */
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

    /** 删除某个ID的运行计划 */
    public void removeScheduler(FixedString trainID) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM train_scheduler WHERE train_id=?");
        ps.setString(1, trainID.toString());
        ps.executeUpdate();
        ps.close();
    }

    private String joinIntArray(int[] arr, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            if (i > 0) sb.append(",");
            sb.append(arr[i]);
        }
        return sb.toString();
    }

    private int[] parseIntArray(String s) {
        if (s == null || s.length() == 0) return new int[0];
        String[] parts = s.split(",");
        int[] arr = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            arr[i] = Integer.parseInt(parts[i]);
        }
        return arr;
    }
}
