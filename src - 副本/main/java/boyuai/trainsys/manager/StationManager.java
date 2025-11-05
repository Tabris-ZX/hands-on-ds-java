package boyuai.trainsys.manager;

import boyuai.trainsys.config.Config;
import boyuai.trainsys.util.Types.*;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 站点管理器（基于SQLite实现）
 * <p>
 * 负责站点信息的管理，包括：
 * <ul>
 *   <li>从数据库加载站点数据到内存缓存</li>
 *   <li>提供站点ID与名称的双向查询</li>
 * </ul>
 * <p>
 * 为提高查询效率，使用 HashMap 缓存站点ID和名称的映射关系
 * 
 * @author hands-on-ds
 * @version 1.0
 * @since 1.0
 */
@Slf4j
public class StationManager {
    /** 站点ID到名称的映射（内存缓存） */
    private final Map<Integer, String> idToName;
    
    /** 站点名称到ID的映射（内存缓存） */
    private final Map<String, Integer> nameToID;
    
    /** 数据库文件路径 */
    private final String dbPath = Config.DATABASE_PATH;
    
    /** 数据库连接对象 */
    private Connection conn;

    /**
     * 构造函数，从数据库加载站点数据到内存
     * <p>
     * 创建 station 表（如果不存在），并将所有站点数据加载到内存缓存中
     */
    public StationManager() {
        idToName = new HashMap<>();
        nameToID = new HashMap<>();
        loadStationsFromDB();
    }

    /**
     * 从数据库加载站点数据到内存缓存
     * <p>
     * 查询 station 表中的所有记录，并构建 ID 与名称的双向映射
     */
    private void loadStationsFromDB() {
        try {
            conn = DriverManager.getConnection(Config.CONNECT_URL);
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
            log.error("无法加载数据库中的站点数据", e);
        }
    }

    /**
     * 根据站点ID获取站点名称
     * <p>
     * 从内存缓存中查询站点名称，时间复杂度 O(1)
     * 
     * @param stationID 站点ID
     * @return 站点名称，如果站点不存在返回空字符串
     */
    public StationName getStationName(StationID stationID) {
        String name = idToName.get(stationID.value());
        return new StationName(name != null ? name : "");
    }

    /**
     * 根据站点名称获取站点ID
     * <p>
     * 从内存缓存中查询站点ID，时间复杂度 O(1)
     * 
     * @param stationName 站点名称
     * @return 站点ID，如果站点不存在返回 -1
     */
    public StationID getStationID(String stationName) {
        Integer id = nameToID.get(stationName);
        return new StationID(id != null ? id : -1);
    }

}
