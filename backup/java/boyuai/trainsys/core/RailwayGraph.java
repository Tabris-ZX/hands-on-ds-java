package boyuai.trainsys.core;

import boyuai.trainsys.info.RouteSectionInfo;
import boyuai.trainsys.datastructure.AdjListGraph;
import boyuai.trainsys.datastructure.DisjointSet;
import boyuai.trainsys.datastructure.SeqList;
import boyuai.trainsys.config.Config;
import boyuai.trainsys.util.FixedString;
import boyuai.trainsys.util.Types.*;
import java.util.*;

/**
 * 铁路网络图
 * 用于管理站点间的路线信息和路径查询
 */
public class RailwayGraph {

    // 使用邻接表图存储路线信息
    private AdjListGraph<RouteSectionInfo> routeGraph;

    // 并查集用于快速判断站点连通性
    private DisjointSet stationSet;

    // 路段信息内存池
    private SeqList<RouteSectionInfo> routeSectionPool;

    /**
     * 构造函数
     */
    public RailwayGraph() {
        this.routeGraph = new AdjListGraph<>(Config.MAX_STATIONID);
        this.stationSet = new DisjointSet(Config.MAX_STATIONID);
        this.routeSectionPool = new SeqList<>();
    }

    /**
     * 向运行图中添加一条路线
     * @param departureStationID 出发站ID
     * @param arrivalStationID 到达站ID
     * @param duration 运行时间
     * @param price 票价
     * @param trainID 列车ID
     */
    public void addRoute(int departureStationID, int arrivalStationID,
                         int duration, int price, FixedString trainID) {
        // 新建一条 SectionInfo 并将其放入内存池
        RouteSectionInfo section = new RouteSectionInfo(trainID, arrivalStationID, price, duration);
        routeSectionPool.insert(routeSectionPool.length(), section);

        // 向图中插入一条边
        routeGraph.insert(departureStationID, arrivalStationID, section);

        // 用不相交集将节点标记为连通
        int x = stationSet.find(departureStationID);
        int y = stationSet.find(arrivalStationID);
        stationSet.join(x, y);
    }

    /**
     * 检查两个站点是否连通
     * @param departureStationID 出发站ID
     * @param arrivalStationID 到达站ID
     * @return 是否连通
     */
    public boolean checkStationAccessibility(int departureStationID, int arrivalStationID) {
        // 利用并查集判断连通性
        return stationSet.find(departureStationID) == stationSet.find(arrivalStationID);
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
        AdjListGraph.EdgeNode<RouteSectionInfo> p = routeGraph.getVerList()[curIdx];
        while (p != null) {
            if (!visited[p.getEnd()]) {
                routeDfs(p.getEnd(), arrivalIdx, prevStations, visited);
            }
            p = p.getNext();
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
        boolean[] visited = new boolean[routeGraph.numOfVer()];
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
        int numOfVer = routeGraph.numOfVer();

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
            AdjListGraph.EdgeNode<RouteSectionInfo> p = routeGraph.getVerList()[u];
            while (p != null) {
                int weight = 0;
                // 根据 type 信息选择边权 weight
                if (type == 1) {
                    weight = p.getWeight().getDuration();
                } else {
                    weight = p.getWeight().getPrice();
                }

                if (!known[p.getEnd()] && distance[p.getEnd()] > min + weight) {
                    distance[p.getEnd()] = min + weight;
                    prev[p.getEnd()] = u;
                }
                p = p.getNext();
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

    /**
     * 获取路线图（用于其他模块访问）
     * @return 路线图
     */
    public AdjListGraph<RouteSectionInfo> getRouteGraph() {
        return routeGraph;
    }

    /**
     * 获取站点集合（用于其他模块访问）
     * @return 站点并查集
     */
    public DisjointSet getStationSet() {
        return stationSet;
    }
}
