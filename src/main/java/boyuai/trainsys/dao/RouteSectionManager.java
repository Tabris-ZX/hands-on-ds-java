package boyuai.trainsys.dao;

import boyuai.trainsys.dao.entity.RouteSectionEntity;
import boyuai.trainsys.dao.entity.StationComponentEntity;
import boyuai.trainsys.dao.mapper.RouteSectionMapper;
import boyuai.trainsys.dao.mapper.StationComponentMapper;
import boyuai.trainsys.util.Types.TrainID;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class RouteSectionManager {
    private final RouteSectionMapper routeSectionMapper;
    private final StationComponentMapper stationComponentMapper;

    public RouteSectionManager(RouteSectionMapper routeSectionMapper, StationComponentMapper stationComponentMapper) {
        this.routeSectionMapper = routeSectionMapper;
        this.stationComponentMapper = stationComponentMapper;
    }

    public static class RouteSectionData {
        public final TrainID trainID;
        public final int departureID;
        public final int arrivalID;
        public final int price;
        public final int duration;

        public RouteSectionData(TrainID trainID, int departureID, int arrivalID, int price, int duration) {
            this.trainID = trainID;
            this.departureID = departureID;
            this.arrivalID = arrivalID;
            this.price = price;
            this.duration = duration;
        }
    }

    public void saveSection(TrainID trainID, int departureID, int arrivalID, int duration, int price) {
        RouteSectionEntity entity = new RouteSectionEntity();
        entity.setTrainId(trainID.toString());
        entity.setDepartureId(departureID);
        entity.setArrivalId(arrivalID);
        entity.setDuration(duration);
        entity.setPrice(price);
        routeSectionMapper.insert(entity);
    }

    public List<RouteSectionData> loadAllSections() {
        List<RouteSectionEntity> entities = routeSectionMapper.selectList(new QueryWrapper<>());
        List<RouteSectionData> result = new ArrayList<>();
        for (RouteSectionEntity entity : entities) {
            result.add(new RouteSectionData(
                    new TrainID(entity.getTrainId()),
                    entity.getDepartureId(),
                    entity.getArrivalId(),
                    entity.getPrice(),
                    entity.getDuration()
            ));
        }
        return result;
    }

    public void saveStationComponent(int stationId, int componentId) {
        StationComponentEntity entity = new StationComponentEntity();
        entity.setStationId(stationId);
        entity.setComponentId(componentId);
        if (stationComponentMapper.selectById(stationId) == null) {
            stationComponentMapper.insert(entity);
        } else {
            stationComponentMapper.updateById(entity);
        }
    }

    public void clearStationComponents() {
        stationComponentMapper.delete(new QueryWrapper<>());
    }

    public void close() {
    }
}
