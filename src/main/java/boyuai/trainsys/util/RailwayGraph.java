package boyuai.trainsys.util;

import boyuai.trainsys.config.StaticConfig;
import boyuai.trainsys.dao.RouteSectionManager;
import boyuai.trainsys.model.RouteSectionInfo;
import boyuai.trainsys.util.Types.StationID;
import boyuai.trainsys.util.Types.TrainID;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Data
@Slf4j
public class RailwayGraph {

    private AdjListGraph<RouteSectionInfo> routeGraph;
    private List<List<Edge>> adjacency;
    private DisjointSet stationSet;
    private SeqList<RouteSectionInfo> routeSectionPool;
    private final RouteSectionManager routeSectionManager;

    public RailwayGraph(RouteSectionManager routeSectionManager) {
        this.routeSectionManager = routeSectionManager;
        this.routeGraph = new AdjListGraph<>(StaticConfig.MAX_STATIONID);
        this.adjacency = new ArrayList<>(StaticConfig.MAX_STATIONID);
        for (int i = 0; i < StaticConfig.MAX_STATIONID; i++) {
            adjacency.add(new ArrayList<>());
        }
        this.stationSet = new DisjointSet(StaticConfig.MAX_STATIONID);
        this.routeSectionPool = new SeqList<>();
    }

    private static class Edge {
        int end;
        RouteSectionInfo info;

        Edge(int end, RouteSectionInfo info) {
            this.end = end;
            this.info = info;
        }
    }

    public void addRoute(int departureStationID, int arrivalStationID, int duration, int price, TrainID trainID) {
        RouteSectionInfo section = new RouteSectionInfo(trainID, new StationID(arrivalStationID), price, duration);
        routeSectionPool.insert(routeSectionPool.length(), section);
        routeGraph.insert(departureStationID, arrivalStationID, section);
        adjacency.get(departureStationID).add(new Edge(arrivalStationID, section));

        int x = stationSet.find(departureStationID);
        int y = stationSet.find(arrivalStationID);
        stationSet.join(x, y);
        routeSectionManager.saveStationComponent(departureStationID, stationSet.find(departureStationID));
        routeSectionManager.saveStationComponent(arrivalStationID, stationSet.find(arrivalStationID));
        routeSectionManager.saveSection(trainID, departureStationID, arrivalStationID, duration, price);
    }

    public boolean checkStationAccessibility(int departureStationID, int arrivalStationID) {
        return stationSet.connected(departureStationID, arrivalStationID);
    }

    private void routeDfs(int curIdx, int arrivalIdx, SeqList<Integer> prevStations, boolean[] visited, StringBuilder result) {
        prevStations.insert(prevStations.length(), curIdx);
        if (curIdx == arrivalIdx) {
            StringBuilder route = new StringBuilder("route found: ");
            for (int i = 0; i < prevStations.length(); i++) {
                route.append(prevStations.visit(i)).append(" ");
            }
            result.append(route).append("\n");
            prevStations.remove(prevStations.length() - 1);
            return;
        }

        visited[curIdx] = true;
        for (Edge e : adjacency.get(curIdx)) {
            if (!visited[e.end]) {
                routeDfs(e.end, arrivalIdx, prevStations, visited, result);
            }
        }
        visited[curIdx] = false;
        prevStations.remove(prevStations.length() - 1);
    }

    public String displayRoute(int departureStationID, int arrivalStationID) {
        if (!checkStationAccessibility(departureStationID, arrivalStationID)) {
            return "未找到路径";
        }
        boolean[] visited = new boolean[routeGraph.NumOfVer()];
        SeqList<Integer> prev = new SeqList<>();
        StringBuilder result = new StringBuilder();
        routeDfs(departureStationID, arrivalStationID, prev, visited, result);
        if (result.length() == 0) {
            return "未找到路径";
        }
        return result.toString().trim();
    }

    public String shortestPath(int departureStationID, int arrivalStationID, int type) {
        int numOfVer = routeGraph.NumOfVer();
        int[] prev = new int[numOfVer];
        boolean[] known = new boolean[numOfVer];
        long[] distance = new long[numOfVer];

        for (int i = 0; i < numOfVer; i++) {
            prev[i] = i;
            known[i] = false;
            distance[i] = Long.MAX_VALUE / 2;
        }

        distance[departureStationID] = 0;
        prev[departureStationID] = departureStationID;

        for (int i = 1; i < numOfVer; i++) {
            int u = -1;
            long min = Long.MAX_VALUE / 2;
            for (int j = 0; j < numOfVer; j++) {
                if (!known[j] && distance[j] < min) {
                    min = distance[j];
                    u = j;
                }
            }
            if (u == -1) {
                break;
            }

            known[u] = true;
            for (Edge e : adjacency.get(u)) {
                int weight = type == 1 ? e.info.getDuration() : e.info.getPrice();
                if (!known[e.end] && distance[e.end] > min + weight) {
                    distance[e.end] = min + weight;
                    prev[e.end] = u;
                }
            }
        }

        if (distance[arrivalStationID] == Long.MAX_VALUE / 2) {
            return "未找到路径";
        }

        SeqList<Integer> path = new SeqList<>();
        int u = arrivalStationID;
        while (u != departureStationID) {
            path.insert(0, u);
            u = prev[u];
        }
        path.insert(0, departureStationID);

        StringBuilder result = new StringBuilder("最短路径: ");
        for (int i = 0; i < path.length(); i++) {
            result.append(path.visit(i));
            if (i < path.length() - 1) {
                result.append(" -> ");
            }
        }
        result.append("\n");
        if (type == 1) {
            result.append("总时间: ").append(distance[arrivalStationID]).append(" 分钟");
        } else {
            result.append("总价格: ").append(distance[arrivalStationID]).append(" 元");
        }
        return result.toString();
    }

    public void loadFromDB() {
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
            routeSectionPool.insert(routeSectionPool.length(), section);
            routeGraph.insert(departureID, arrivalID, section);
            adjacency.get(departureID).add(new Edge(arrivalID, section));
            stationSet.join(stationSet.find(departureID), stationSet.find(arrivalID));
            routeSectionManager.saveStationComponent(departureID, stationSet.find(departureID));
            routeSectionManager.saveStationComponent(arrivalID, stationSet.find(arrivalID));
        }
    }

    public void refreshConnectivityFromDB() {
        this.routeGraph = new AdjListGraph<>(StaticConfig.MAX_STATIONID);
        for (List<Edge> edges : adjacency) {
            edges.clear();
        }
        this.stationSet = new DisjointSet(StaticConfig.MAX_STATIONID);
        this.routeSectionPool = new SeqList<>();

        HashSet<Integer> usedStations = new HashSet<>();
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
            routeSectionPool.insert(routeSectionPool.length(), section);
            routeGraph.insert(departureID, arrivalID, section);
            adjacency.get(departureID).add(new Edge(arrivalID, section));
            stationSet.union(departureID, arrivalID);
        }

        routeSectionManager.clearStationComponents();
        for (int sid : usedStations) {
            routeSectionManager.saveStationComponent(sid, stationSet.find(sid));
        }
    }
}
