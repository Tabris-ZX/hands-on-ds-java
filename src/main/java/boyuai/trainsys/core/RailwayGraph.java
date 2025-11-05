package boyuai.trainsys.core;

import boyuai.trainsys.info.RouteSectionInfo;
import boyuai.trainsys.datastructure.AdjListGraph;
import boyuai.trainsys.datastructure.DisjointSet;
import boyuai.trainsys.datastructure.SeqList;
import boyuai.trainsys.config.Config;
import boyuai.trainsys.manager.RouteSectionManager;
import boyuai.trainsys.util.Types.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 铁路线路图
 * <p>
 * 使用图数据结构表示铁路网络，支持：
 * <ul>
 *   <li>路线区段的添加和管理</li>
 *   <li>站点连通性检查（基于并查集）</li>
 *   <li>路线查询（DFS遍历）</li>
 *   <li>最短路径计算（Dijkstra算法）</li>
 *   <li>数据持久化（通过 RouteSectionManager）</li>
 * </ul>
 * <p>
 * 数据结构设计：
 * <ul>
 *   <li>邻接表图：存储路线区段的详细信息</li>
 *   <li>并查集：快速判断站点间的连通性</li>
 *   <li>内存池：缓存路线区段信息对象</li>
 * </ul>
 */
@Data
@Slf4j
public class RailwayGraph {

    /** 邻接表图，存储路线区段信息 */
    private AdjListGraph<RouteSectionInfo> routeGraph;
    
    /** 自维护的邻接表结构，便于遍历 */
    private List<List<Edge>> adjacency;

    /** 并查集，用于快速判断站点连通性 */
    private DisjointSet stationSet;

    /** 路段信息内存池 */
    private SeqList<RouteSectionInfo> routeSectionPool;

    /** 路线区段管理器（负责数据库操作） */
    private final RouteSectionManager routeSectionManager;

    /**
     * 构造铁路线路图
     * <p>
     * 初始化图结构、并查集和内存池
     * 
     * @param routeSectionManager 路线区段管理器，用于数据库持久化
     */
    public RailwayGraph(RouteSectionManager routeSectionManager) {
        this.routeSectionManager = routeSectionManager;
        this.routeGraph = new AdjListGraph<>(Config.MAX_STATIONID);
        this.adjacency = new ArrayList<>(Config.MAX_STATIONID);
        for (int i = 0; i < Config.MAX_STATIONID; i++) adjacency.add(new ArrayList<>());
        this.stationSet = new DisjointSet(Config.MAX_STATIONID);
        this.routeSectionPool = new SeqList<>();
    }

    /**
     * 边的内部表示
     * <p>
     * 用于在邻接表中存储路线区段信息
     */
    private static class Edge {
        /** 目标站点ID */
        int end;
        /** 路线区段详细信息 */
        RouteSectionInfo info;
        
        Edge(int end, RouteSectionInfo info) { 
            this.end = end; 
            this.info = info; 
        }
    }

    /**
     * 向铁路线路图中添加一条路线区段
     * <p>
     * 执行以下操作：
     * <ol>
     *   <li>创建路线区段信息对象并加入内存池</li>
     *   <li>更新图结构和邻接表</li>
     *   <li>使用并查集标记站点连通性</li>
     *   <li>将数据持久化到数据库</li>
     * </ol>
     * 
     * @param departureStationID 出发站ID
     * @param arrivalStationID 到达站ID
     * @param duration 运行时长（分钟）
     * @param price 票价
     * @param trainID 车次ID
     */
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
            routeSectionManager.saveStationComponent(departureStationID, stationSet.find(departureStationID));
            routeSectionManager.saveStationComponent(arrivalStationID, stationSet.find(arrivalStationID));
        } catch (java.sql.SQLException e) {
            log.error("写入站点连通分量失败", e);
        }
        // 同步写入数据库
        try {
            routeSectionManager.saveSection(trainID, departureStationID, arrivalStationID, duration, price);
        } catch (java.sql.SQLException e) {
            log.error("写入数据库异常", e);
        }
    }

    /**
     * 检查两个站点是否连通
     * <p>
     * 使用并查集快速判断连通性，时间复杂度接近 O(1)
     * 
     * @param departureStationID 出发站ID
     * @param arrivalStationID 到达站ID
     * @return 如果两站点连通返回 true，否则返回 false
     */
    public boolean checkStationAccessibility(int departureStationID, int arrivalStationID) {
        log.debug("检查站点连通性: {} -> {}", departureStationID, arrivalStationID);
        log.debug("连通分量根: {} -> {}", stationSet.find(departureStationID), stationSet.find(arrivalStationID));
        // 利用并查集判断连通性（使用便捷方法 connected）
        return stationSet.connected(departureStationID, arrivalStationID);
    }

    /**
     * 深度优先搜索查找所有可达路径
     * <p>
     * 递归地查找从当前站点到目标站点的所有路径，并输出到控制台
     * 
     * @param curIdx 当前站点索引
     * @param arrivalIdx 目标站点索引
     * @param prevStations 已访问的站点列表（路径）
     * @param visited 访问标记数组，用于避免环路
     */
    private void routeDfs(int curIdx, int arrivalIdx,
                          SeqList<Integer> prevStations, boolean[] visited) {
        prevStations.insert(prevStations.length(), curIdx);

        // 已找到一条路径，输出它
        if (curIdx == arrivalIdx) {
            StringBuilder route = new StringBuilder("route found: ");
            for (int i = 0; i < prevStations.length(); i++) {
                route.append(prevStations.visit(i)).append(" ");
            }
            log.info(route.toString());
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
     * 显示从出发站到到达站的所有可达路线
     * <p>
     * 使用深度优先搜索遍历所有可能的路径，并输出到控制台
     * 
     * @param departureStationID 出发站ID
     * @param arrivalStationID 到达站ID
     */
    public void displayRoute(int departureStationID, int arrivalStationID) {
        boolean[] visited = new boolean[routeGraph.NumOfVer()];
        SeqList<Integer> prev = new SeqList<>();
        routeDfs(departureStationID, arrivalStationID, prev, visited);
    }

    /**
     * 使用Dijkstra算法计算最短路径
     * <p>
     * 根据指定的优化目标（价格或时间），计算从出发站到到达站的最优路径，
     * 并输出路径和总代价
     * <p>
     * 时间复杂度：O(V²)，其中 V 是站点数量
     * 
     * @param departureStationID 出发站ID
     * @param arrivalStationID 到达站ID
     * @param type 优化目标：0-按价格最优，1-按时间最优
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
            log.info("No path found.");
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
        StringBuilder pathStr = new StringBuilder("shortest path: ");
        for (int i = 0; i < path.length(); i++) {
            pathStr.append(path.visit(i)).append(" ");
        }
        log.info(pathStr.toString());

        // 输出总代价
        if (type == 1) {
            log.info("Total time: {}", distance[arrivalStationID]);
        } else {
            log.info("Total price: {}", distance[arrivalStationID]);
        }
    }

    /**
     * 从数据库加载所有路线区段到图结构
     * <p>
     * 执行以下操作：
     * <ol>
     *   <li>从数据库加载所有路线区段数据</li>
     *   <li>构建图结构和邻接表</li>
     *   <li>更新并查集的连通性</li>
     *   <li>同步连通分量信息到数据库</li>
     * </ol>
     * 
     * @throws java.sql.SQLException 如果数据库操作失败
     */
    public void loadFromDB() throws java.sql.SQLException {
        // 从管理器加载所有区段
        List<RouteSectionManager.RouteSectionData> sections = routeSectionManager.loadAllSections();
        for (RouteSectionManager.RouteSectionData sectionData : sections) {
            RouteSectionInfo section = new RouteSectionInfo(
                sectionData.trainID,
                new StationID(sectionData.arrivalID),
                sectionData.price,
                sectionData.duration
            );
            int departureID = sectionData.departureID;
            int arrivalID = sectionData.arrivalID;
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
                routeSectionManager.saveStationComponent(departureID, stationSet.find(departureID));
                routeSectionManager.saveStationComponent(arrivalID, stationSet.find(arrivalID));
            } catch (java.sql.SQLException e) {
                log.error("写入站点连通分量失败", e);
            }
        }
    }

    /**
     * 从数据库重建所有图结构和站点连通分量
     * <p>
     * 清空内存中的所有数据结构，重新从数据库加载数据并重建：
     * <ol>
     *   <li>清空图结构、邻接表、并查集和内存池</li>
     *   <li>从数据库重新加载所有路线区段</li>
     *   <li>重建图结构和连通性</li>
     *   <li>清空并重新写入站点连通分量表</li>
     * </ol>
     * <p>
     * 通常在需要完全刷新数据时使用
     * 
     * @throws java.sql.SQLException 如果数据库操作失败
     */
    public void refreshConnectivityFromDB() throws java.sql.SQLException {
        // 清空邻接表、内存池和并查集
        this.routeGraph = new AdjListGraph<>(Config.MAX_STATIONID);
        for (int i = 0; i < adjacency.size(); i++) adjacency.get(i).clear();
        this.stationSet = new DisjointSet(Config.MAX_STATIONID);
        this.routeSectionPool = new SeqList<>();
        // 记录实际出现过的所有站点id
        java.util.HashSet<Integer> usedStations = new java.util.HashSet<>();
        // 从管理器加载所有区段并重新建图和联通分量
        List<RouteSectionManager.RouteSectionData> sections = routeSectionManager.loadAllSections();
        for (RouteSectionManager.RouteSectionData sectionData : sections) {
            int departureID = sectionData.departureID;
            int arrivalID = sectionData.arrivalID;
            usedStations.add(departureID);
            usedStations.add(arrivalID);
            RouteSectionInfo section = new RouteSectionInfo(
                sectionData.trainID,
                new StationID(arrivalID),
                sectionData.price,
                sectionData.duration
            );
            this.routeSectionPool.insert(this.routeSectionPool.length(), section);
            this.routeGraph.insert(departureID, arrivalID, section);
            this.adjacency.get(departureID).add(new Edge(arrivalID, section));
            // 联通分量合并
            stationSet.union(departureID, arrivalID);
        }
        // 清空原有station_component表
        routeSectionManager.clearStationComponents();
        // 重新写入所有当前站点的component_id
        for (int sid : usedStations) {
            routeSectionManager.saveStationComponent(sid, stationSet.find(sid));
        }
    }
}
