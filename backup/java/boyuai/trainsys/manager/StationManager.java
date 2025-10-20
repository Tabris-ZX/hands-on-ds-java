package boyuai.trainsys.manager;

import boyuai.trainsys.util.Types.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 站点管理器
 */
public class StationManager {
    private final Map<Integer, String> idToName;
    private final Map<String, Integer> nameToID;

    /**
     * 构造函数
     * @param filename 站点数据文件名
     */
    public StationManager(String filename) {
        idToName = new HashMap<>();
        nameToID = new HashMap<>();
        loadStations(filename);
    }

    /**
     * 从文件加载站点信息
     * @param filename 文件名
     */
    private void loadStations(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length >= 2) {
                    try {
                        int id = Integer.parseInt(parts[0]);
                        String name = parts[1];
                        idToName.put(id, name);
                        nameToID.put(name, id);
                    } catch (NumberFormatException e) {
                        // 忽略格式错误的行
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("无法加载站点文件: " + filename);
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

    /**
     * 获取所有站点数量
     * @return 站点数量
     */
    public int getStationCount() {
        return idToName.size();
    }
}
