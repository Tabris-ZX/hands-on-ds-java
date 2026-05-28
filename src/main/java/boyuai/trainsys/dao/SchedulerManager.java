package boyuai.trainsys.dao;

import boyuai.trainsys.dao.entity.TrainSchedulerEntity;
import boyuai.trainsys.dao.mapper.TrainSchedulerMapper;
import boyuai.trainsys.dao.support.DbCodec;
import boyuai.trainsys.util.FixedString;
import boyuai.trainsys.util.TrainScheduler;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class SchedulerManager {
    private final TrainSchedulerMapper trainSchedulerMapper;

    public SchedulerManager(TrainSchedulerMapper trainSchedulerMapper) {
        this.trainSchedulerMapper = trainSchedulerMapper;
    }

    public void addScheduler(FixedString trainID, int seatNum, String startTime, int passingStationNumber, int[] stations, int[] duration, int[] price) {
        TrainSchedulerEntity entity = new TrainSchedulerEntity();
        entity.setTrainId(trainID.toString());
        entity.setSeatNum(seatNum);
        entity.setStartTime(startTime);
        entity.setPassingNum(passingStationNumber);
        entity.setStations(DbCodec.joinIntArray(stations, passingStationNumber));
        entity.setDuration(DbCodec.joinIntArray(duration, passingStationNumber - 1));
        entity.setPrice(DbCodec.joinIntArray(price, passingStationNumber - 1));
        if (trainSchedulerMapper.selectById(entity.getTrainId()) == null) {
            trainSchedulerMapper.insert(entity);
        } else {
            trainSchedulerMapper.updateById(entity);
        }
    }

    public boolean existScheduler(FixedString trainID) {
        return trainSchedulerMapper.selectById(trainID.toString()) != null;
    }

    public TrainScheduler getScheduler(FixedString trainID) {
        TrainSchedulerEntity entity = trainSchedulerMapper.selectById(trainID.toString());
        if (entity == null) {
            return null;
        }
        return DbCodec.toTrainScheduler(entity.getTrainId(), entity.getSeatNum(), entity.getStartTime(), entity.getStations(), entity.getDuration(), entity.getPrice());
    }

    public void removeScheduler(FixedString trainID) {
        trainSchedulerMapper.deleteById(trainID.toString());
    }

    public List<TrainScheduler> getAllSchedulers() {
        List<TrainSchedulerEntity> entities = trainSchedulerMapper.selectList(new QueryWrapper<TrainSchedulerEntity>().orderByAsc("train_id"));
        List<TrainScheduler> schedulers = new ArrayList<>();
        for (TrainSchedulerEntity entity : entities) {
            schedulers.add(DbCodec.toTrainScheduler(entity.getTrainId(), entity.getSeatNum(), entity.getStartTime(), entity.getStations(), entity.getDuration(), entity.getPrice()));
        }
        return schedulers;
    }
}
