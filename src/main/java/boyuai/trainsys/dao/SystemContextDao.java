package boyuai.trainsys.dao;

import boyuai.trainsys.util.TrainSystem;
import org.springframework.stereotype.Component;

@Component
public class SystemContextDao {

    private final TrainSystem trainSystem;
    private final StationManager stationManager;

    public SystemContextDao(TrainSystem trainSystem) {
        this.trainSystem = trainSystem;
        this.stationManager = trainSystem.getStationManager();
    }

    public TrainSystem getTrainSystem() {
        return trainSystem;
    }

    public StationManager getStationManager() {
        return stationManager;
    }
}
