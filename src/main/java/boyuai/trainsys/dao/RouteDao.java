package boyuai.trainsys.dao;

import boyuai.trainsys.util.Types.StationID;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RouteDao {

    private final SystemContextDao systemContextDao;

    public RouteDao(SystemContextDao systemContextDao) {
        this.systemContextDao = systemContextDao;
    }

    public Integer stationNameToId(String stationName) {
        return systemContextDao.getStationManager().nameToID(stationName);
    }

    public String stationIdToName(int stationId) {
        return systemContextDao.getStationManager().idToName(stationId);
    }

    public List<String> getAllStations() {
        return new ArrayList<>(systemContextDao.getStationManager().getAllStationNames());
    }

    public String findAllRoute(int depId, int arrId) {
        return systemContextDao.getTrainSystem().findAllRoute(new StationID(depId), new StationID(arrId));
    }

    public String findBestRoute(int depId, int arrId, int preference) {
        return systemContextDao.getTrainSystem().findBestRoute(new StationID(depId), new StationID(arrId), preference);
    }

    public boolean queryAccessibility(int depId, int arrId) {
        return systemContextDao.getTrainSystem().getRailwayGraph().checkStationAccessibility(depId, arrId);
    }
}
