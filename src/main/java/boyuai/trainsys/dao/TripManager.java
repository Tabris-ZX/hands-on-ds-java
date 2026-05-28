package boyuai.trainsys.dao;

import boyuai.trainsys.dao.entity.TripInfoEntity;
import boyuai.trainsys.dao.mapper.TripInfoMapper;
import boyuai.trainsys.dao.support.DbCodec;
import boyuai.trainsys.model.TripInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class TripManager {
    private final TripInfoMapper tripInfoMapper;

    public TripManager(TripInfoMapper tripInfoMapper) {
        this.tripInfoMapper = tripInfoMapper;
    }

    public void addTrip(long userID, TripInfo trip) {
        TripInfoEntity entity = new TripInfoEntity();
        entity.setUserId(userID);
        entity.setTrainId(trip.getTrainID().toString());
        entity.setDepartureStation(trip.getDepartureStation().value());
        entity.setArrivalStation(trip.getArrivalStation().value());
        entity.setType(trip.getType());
        entity.setDuration(trip.getDuration());
        entity.setPrice(trip.getPrice());
        entity.setDepartureTime(trip.getDepartureTime().toString());
        entity.setArrivalTime(trip.getArrivalTime().toString());
        tripInfoMapper.insert(entity);
    }

    public List<TripInfo> queryTrip(long userID) {
        List<TripInfoEntity> entities = tripInfoMapper.selectList(new QueryWrapper<TripInfoEntity>().eq("user_id", userID));
        List<TripInfo> trips = new ArrayList<>();
        for (TripInfoEntity entity : entities) {
            trips.add(DbCodec.toTripInfo(
                    entity.getTrainId(),
                    entity.getDepartureStation(),
                    entity.getArrivalStation(),
                    entity.getType(),
                    entity.getDuration(),
                    entity.getPrice(),
                    entity.getDepartureTime(),
                    entity.getArrivalTime()
            ));
        }
        return trips;
    }

    public void removeTrip(long userID, TripInfo trip) {
        tripInfoMapper.delete(new QueryWrapper<TripInfoEntity>()
                .eq("user_id", userID)
                .eq("train_id", trip.getTrainID().toString())
                .eq("departure_station", trip.getDepartureStation().value())
                .eq("arrival_station", trip.getArrivalStation().value())
                .eq("type", trip.getType())
                .eq("departure_time", trip.getDepartureTime().toString()));
    }
}
