package boyuai.trainsys.manager;

import boyuai.trainsys.util.Types.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 站点管理器
 */
public class StationManager {
    private final Map<Integer, String> idToName;
    private final Map<String, Integer> nameToID;
    private final String dbPath = "data/hands-on-ds.db";
    private Connection conn;

    /**
     * 构造函数
     * @param filename 站点数据文件名
     */
    public StationManager() {
        idToName = new HashMap<>();
        nameToID = new HashMap<>();
        loadStationsFromDB();
    }

    private void loadStationsFromDB() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS station (id INTEGER PRIMARY KEY, name TEXT)");
            ResultSet rs = stmt.executeQuery("SELECT id, name FROM station");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                idToName.put(id, name);
                nameToID.put(name, id);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("无法加载数据库中的站点数据: " + e.getMessage());
        }
    }

    /**
     * 根据站点ID获取站点名称
     * @param stationID 站点ID
     * @return 站点名称
     */
    public StationName getStationName(StationID stationID) {
        String name = idToName.get(stationID.value());
        return new StationName(name != null ? name : "");
    }

    /**
     * 根据站点名称获取站点ID
     * @param stationName 站点名称
     * @return 站点ID
     */
    public StationID getStationID(String stationName) {
        Integer id = nameToID.get(stationName);
        return new StationID(id != null ? id : -1);
    }

}
