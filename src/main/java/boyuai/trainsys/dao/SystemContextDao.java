package boyuai.trainsys.dao;

import boyuai.trainsys.core.TrainSystem;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
public class SystemContextDao {

    private final TrainSystem trainSystem;
    private final StationManager stationManager;

    public SystemContextDao() throws SQLException {
        this.trainSystem = new TrainSystem();
        this.stationManager = trainSystem.getStationManager();
    }

    public TrainSystem getTrainSystem() {
        return trainSystem;
    }

    public StationManager getStationManager() {
        return stationManager;
    }
}
