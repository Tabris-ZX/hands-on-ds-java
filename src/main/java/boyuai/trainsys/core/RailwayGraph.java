package boyuai.trainsys.core;

import boyuai.trainsys.info.RouteSectionInfo;
import boyuai.trainsys.datastructure.AdjListGraph;
import boyuai.trainsys.datastructure.DisjointSet;
import boyuai.trainsys.datastructure.SeqList;
import boyuai.trainsys.config.Config;
import boyuai.trainsys.util.Types.*;
import lombok.Data;

import java.util.*;
import java.sql.*;

@Data
public class RailwayGraph {

    // 使用邻接表图存储"存在性"，同时自维护可遍历的邻接结构
    private AdjListGraph<RouteSectionInfo> routeGraph;
    private List<List<Edge>> adjacency; // 自维护的可遍历邻接表

    // 并查集用于快速判断站点连通性
    private DisjointSet stationSet;

    // 路段信息内存池
    private SeqList<RouteSectionInfo> routeSectionPool;

    // 站点、区段持久化: 初始化和存盘到数据库
    private final String dbPath = "data/hands-on-ds.db";
    private Connection conn;
    public void initDB() throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS station (id INTEGER PRIMARY KEY, name TEXT)");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS route_section (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
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
     * 构造函数
     */
    public RailwayGraph() {
        this.routeGraph = new AdjListGraph<>(Config.MAX_STATIONID);
        this.adjacency = new ArrayList<>(Config.MAX_STATIONID);
        for (int i = 0; i < Config.MAX_STATIONID; i++) adjacency.add(new ArrayList<>());
        this.stationSet = new DisjointSet(Config.MAX_STATIONID);
        this.routeSectionPool = new SeqList<>();
    }

    private static class Edge {
        int end;
        RouteSectionInfo info;
        Edge(int end, RouteSectionInfo info) { this.end = end; this.info = info; }
    }

    // 向运行图中加入一条边
    public void addRoute(int departureStationID, int arrivalStationID,
                         int duration, int price, TrainID trainID) {
        // 新建一条 SectionInfo 并将其放入内存池
        RouteSectionInfo section = new RouteSectionInfo(trainID, new StationID(arrivalStationID), price, duration);
        routeSectionPool.insert(routeSectionPool.length(), section); // 内存池
        // 向图中插入一条边
        routeGraph.insert(departureStationID, arrivalStationID, section);
        adjacency.get(departureStationID).add(new Edge(arrivalStationID, section));
        // 用不相交集将节点标记为连通
        int x = stationSet.find(departureStationID);
        int y = stationSet.find(arrivalStationID);
        stationSet.join(x, y);
        // 记录两个端点当前的连通分量根到数据库
        try {
            saveStationComponentToDB(departureStationID, stationSet.find(departureStationID));
            saveStationComponentToDB(arrivalStationID, stationSet.find(arrivalStationID));
        } catch (java.sql.SQLException e) {
            System.out.println("写入站点连通分量失败");
            e.printStackTrace();
        }
        // 同步写入数据库
        try {
            saveSectionToDB(trainID, departureStationID, arrivalStationID, duration, price);
        } catch (java.sql.SQLException e) {
            System.out.println("写入数据库异常");
            e.printStackTrace();
        }
    }

    /**
     * 检查两个站点是否连通
     * @param departureStationID 出发站ID
     * @param arrivalStationID 到达站ID
     * @return 是否连通
     */
    public boolean checkStationAccessibility(int departureStationID, int arrivalStationID) {
        System.out.println(departureStationID + " " + arrivalStationID);
        System.out.println(stationSet.find(departureStationID) + " " + stationSet.find(arrivalStationID));
        // 利用并查集判断连通性（使用便捷方法 connected）
        return stationSet.connected(departureStationID, arrivalStationID);
    }

    /**
     * 深度优先搜索查找路径
     * @param curIdx 当前节点索引
     * @param arrivalIdx 目标节点索引
     * @param prevStations 路径上的站点列表
     * @param visited 访问标记数组
     */
    private void routeDfs(int curIdx, int arrivalIdx,
                          SeqList<Integer> prevStations, boolean[] visited) {
        prevStations.insert(prevStations.length(), curIdx);

        // 已找到一条路径，输出它
        if (curIdx == arrivalIdx) {
            System.out.print("route found: ");
            for (int i = 0; i < prevStations.length(); i++) {
                System.out.print(prevStations.visit(i) + " ");
            }
            System.out.println();
            prevStations.remove(prevStations.length() - 1);
            return;
        }

        visited[curIdx] = true;

        // 遍历所有邻接节点
        for (Edge e : adjacency.get(curIdx)) {
            if (!visited[e.end]) {
                routeDfs(e.end, arrivalIdx, prevStations, visited);
            }
        }

        // 回溯
        visited[curIdx] = false;
        prevStations.remove(prevStations.length() - 1);
    }

    /**
     * 显示从出发站到到达站的所有路线
     * @param departureStationID 出发站ID
     * @param arrivalStationID 到达站ID
     */
    public void displayRoute(int departureStationID, int arrivalStationID) {
        boolean[] visited = new boolean[routeGraph.NumOfVer()];
        SeqList<Integer> prev = new SeqList<>();
        routeDfs(departureStationID, arrivalStationID, prev, visited);
    }

    /**
     * 使用Dijkstra算法查找最短路径
     * @param departureStationID 出发站ID
     * @param arrivalStationID 到达站ID
     * @param type 0-按价格最优，1-按时间最优
     */
    public void shortestPath(int departureStationID, int arrivalStationID, int type) {
        int numOfVer = routeGraph.NumOfVer();

        // 使用朴素 Dijkstra 算法求解最短路
        int[] prev = new int[numOfVer];
        boolean[] known = new boolean[numOfVer];
        long[] distance = new long[numOfVer];
        long min;

        // 初始化
        for (int i = 0; i < numOfVer; i++) {
            prev[i] = i;
            known[i] = false;
            distance[i] = Long.MAX_VALUE / 2;
        }

        distance[departureStationID] = 0;
        prev[departureStationID] = departureStationID;

        // Dijkstra 算法主循环
        for (int i = 1; i < numOfVer; i++) {
            int u = -1;
            min = Long.MAX_VALUE / 2;

            // 找到未处理节点中距离最小的
            for (int j = 0; j < numOfVer; j++) {
                if (!known[j] && distance[j] < min) {
                    min = distance[j];
                    u = j;
                }
            }

            if (u == -1) break; // 没有可达节点

            known[u] = true;

            // 更新邻接节点的距离
            for (Edge e : adjacency.get(u)) {
                int weight = (type == 1) ? e.info.getDuration() : e.info.getPrice();
                if (!known[e.end] && distance[e.end] > min + weight) {
                    distance[e.end] = min + weight;
                    prev[e.end] = u;
                }
            }
        }

        // 检查是否可达
        if (distance[arrivalStationID] == Long.MAX_VALUE / 2) {
            System.out.println("No path found.");
            return;
        }

        // 反向寻路，找到一条最短路径
        SeqList<Integer> path = new SeqList<>();
        int u = arrivalStationID;

        // 回溯路径
        while (u != departureStationID) {
            path.insert(0, u);
            u = prev[u];
        }
        path.insert(0, departureStationID);

        // 输出最短路
        System.out.print("shortest path: ");
        for (int i = 0; i < path.length(); i++) {
            System.out.print(path.visit(i) + " ");
        }
        System.out.println();

        // 输出总代价
        if (type == 1) {
            System.out.println("Total time: " + distance[arrivalStationID]);
        } else {
            System.out.println("Total price: " + distance[arrivalStationID]);
        }
    }

    // 加载所有区段和车站到图结构
    public void loadFromDB() throws SQLException {
        if (conn == null) initDB();
        // 加载所有区段
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM route_section");
        while (rs.next()) {
            RouteSectionInfo section = new RouteSectionInfo(
                new boyuai.trainsys.util.Types.TrainID(rs.getString("train_id")),
                new boyuai.trainsys.util.Types.StationID(rs.getInt("arrival_id")),
                rs.getInt("price"),
                rs.getInt("duration")
            );
            int departureID = rs.getInt("departure_id");
            int arrivalID = rs.getInt("arrival_id");
            // 录入到运行图、邻接表、内存池
            this.routeSectionPool.insert(this.routeSectionPool.length(), section);
            this.routeGraph.insert(departureID, arrivalID, section);
            this.adjacency.get(departureID).add(new Edge(arrivalID, section));
            // 并查集连通性维护
            int x = stationSet.find(departureID);
            int y = stationSet.find(arrivalID);
            stationSet.join(x, y);
            // 同步记录当前两个端点的连通分量
            try {
                saveStationComponentToDB(departureID, stationSet.find(departureID));
                saveStationComponentToDB(arrivalID, stationSet.find(arrivalID));
            } catch (java.sql.SQLException e) {
                System.out.println("写入站点连通分量失败");
                e.printStackTrace();
            }
        }
        rs.close();
        stmt.close();
    }
    // 保存区段到数据库
    public void saveSectionToDB(TrainID trainID, int departureID, int arrivalID, int duration, int price) throws SQLException {
        if (conn == null) initDB();
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
     * 从数据库重建所有图结构和站点连通分量，并同步到station_component表
     */
    public void refreshConnectivityFromDB() throws SQLException {
        if (conn == null) initDB();
        // 清空邻接表、内存池和并查集
        this.routeGraph = new AdjListGraph<>(Config.MAX_STATIONID);
        for (int i = 0; i < adjacency.size(); i++) adjacency.get(i).clear();
        this.stationSet = new DisjointSet(Config.MAX_STATIONID);
        // 记录实际出现过的所有站点id
        java.util.HashSet<Integer> usedStations = new java.util.HashSet<>();
        // 加载所有区段并重新建图和联通分量
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM route_section");
        while (rs.next()) {
            int departureID = rs.getInt("departure_id");
            int arrivalID = rs.getInt("arrival_id");
            usedStations.add(departureID);
            usedStations.add(arrivalID);
            RouteSectionInfo section = new RouteSectionInfo(
                new boyuai.trainsys.util.Types.TrainID(rs.getString("train_id")),
                new boyuai.trainsys.util.Types.StationID(arrivalID),
                rs.getInt("price"),
                rs.getInt("duration")
            );
            this.routeSectionPool.insert(this.routeSectionPool.length(), section);
            this.routeGraph.insert(departureID, arrivalID, section);
            this.adjacency.get(departureID).add(new Edge(arrivalID, section));
            // 联通分量合并
            stationSet.union(departureID, arrivalID);
        }
        rs.close();
        stmt.close();
        // 清空原有staion_component表
        stmt = conn.createStatement();
        stmt.executeUpdate("DELETE FROM station_component");
        stmt.close();
        // 重新写入所有当前站点的component_id
        for (int sid : usedStations) {
            saveStationComponentToDB(sid, stationSet.find(sid));
        }
    }

    // 记录单个站点所属连通分量（根）。已存在则覆盖更新
    private void saveStationComponentToDB(int stationId, int componentId) throws SQLException {
        if (conn == null) initDB();
        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO station_component (station_id, component_id) VALUES (?, ?) " +
            "ON CONFLICT(station_id) DO UPDATE SET component_id = excluded.component_id"
        );
        ps.setInt(1, stationId);
        ps.setInt(2, componentId);
        ps.executeUpdate();
        ps.close();
    }
}
