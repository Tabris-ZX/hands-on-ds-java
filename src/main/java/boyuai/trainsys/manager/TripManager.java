package boyuai.trainsys.manager;

import boyuai.trainsys.info.TripInfo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TripManager {
    private final String dbPath = "data/hands-on-ds.db";
    private final Connection conn;

    public TripManager() throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS trip_info (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
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

    // 添加行程记录
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

    // 查询用户的所有行程
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

    // 删除用户的某个行程记录
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
