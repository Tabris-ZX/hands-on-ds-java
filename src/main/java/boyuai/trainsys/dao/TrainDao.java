package boyuai.trainsys.dao;

import boyuai.trainsys.model.AddTrainRequest;
import boyuai.trainsys.util.FixedString;
import boyuai.trainsys.util.TrainScheduler;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;

@Repository
public class TrainDao {

    private final SystemContextDao systemContextDao;

    public TrainDao(SystemContextDao systemContextDao) {
        this.systemContextDao = systemContextDao;
    }

    public void addTrain(AddTrainRequest request, int[] stationIds, int[] durations, int[] prices) {
        systemContextDao.getTrainSystem().addTrainScheduler(
                new FixedString(request.getTrainId()),
                request.getSeatNum(),
                request.getStartTime().trim(),
                request.getStations().size(),
                stationIds,
                durations,
                prices
        );
    }

    public TrainScheduler getScheduler(String trainId) throws SQLException {
        return systemContextDao.getTrainSystem().getSchedulerManager().getScheduler(new FixedString(trainId.trim()));
    }

    public List<TrainScheduler> getAllSchedulers() throws SQLException {
        return systemContextDao.getTrainSystem().getSchedulerManager().getAllSchedulers();
    }
}
