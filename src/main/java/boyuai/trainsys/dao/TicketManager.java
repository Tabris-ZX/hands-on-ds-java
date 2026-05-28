package boyuai.trainsys.dao;

import boyuai.trainsys.dao.entity.TicketInfoEntity;
import boyuai.trainsys.dao.mapper.TicketInfoMapper;
import boyuai.trainsys.util.FixedString;
import boyuai.trainsys.util.Time;
import boyuai.trainsys.util.TrainScheduler;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class TicketManager {
    private final TicketInfoMapper ticketInfoMapper;

    public TicketManager(TicketInfoMapper ticketInfoMapper) {
        this.ticketInfoMapper = ticketInfoMapper;
    }

    public int querySeat(FixedString trainID, Time departureTime, int stationID) {
        TicketInfoEntity entity = ticketInfoMapper.selectOne(new QueryWrapper<TicketInfoEntity>()
                .eq("train_id", trainID.toString())
                .eq("departure_time", departureTime.toString())
                .eq("departure_station", stationID)
                .last("LIMIT 1"));
        return entity == null || entity.getSeatNum() == null ? -1 : entity.getSeatNum();
    }

    public int updateSeat(FixedString trainID, Time departureTime, int stationID, int delta) {
        TicketInfoEntity entity = ticketInfoMapper.selectOne(new QueryWrapper<TicketInfoEntity>()
                .eq("train_id", trainID.toString())
                .eq("departure_time", departureTime.toString())
                .eq("departure_station", stationID)
                .last("LIMIT 1"));
        if (entity == null) {
            return -1;
        }
        int oldSeatNum = entity.getSeatNum() == null ? 0 : entity.getSeatNum();
        entity.setSeatNum(oldSeatNum + delta);
        ticketInfoMapper.updateById(entity);
        return entity.getPrice() == null ? -1 : entity.getPrice();
    }

    public void releaseTicket(TrainScheduler scheduler, Time baseTime) {
        int passingStationNum = scheduler.getPassingStationNum();
        for (int i = 0; i + 1 < passingStationNum; i++) {
            Time departureTime = scheduler.getDepartureTimeAt(i, baseTime);
            QueryWrapper<TicketInfoEntity> query = new QueryWrapper<TicketInfoEntity>()
                    .eq("train_id", scheduler.getTrainID().toString())
                    .eq("departure_time", departureTime.toString())
                    .eq("departure_station", scheduler.getStation(i).value())
                    .last("LIMIT 1");
            TicketInfoEntity entity = ticketInfoMapper.selectOne(query);
            if (entity == null) {
                entity = new TicketInfoEntity();
            }
            entity.setTrainId(scheduler.getTrainID().toString());
            entity.setDepartureTime(departureTime.toString());
            entity.setDepartureStation(scheduler.getStation(i).value());
            entity.setArrivalStation(scheduler.getStation(i + 1).value());
            entity.setSeatNum(scheduler.getSeatNum());
            entity.setPrice(scheduler.getPrice(i));
            entity.setDuration(scheduler.getDuration(i));

            if (entity.getId() == null) {
                ticketInfoMapper.insert(entity);
            } else {
                ticketInfoMapper.updateById(entity);
            }
        }
    }

    public void expireTicket(FixedString trainID, Time departureTime) {
        ticketInfoMapper.delete(new QueryWrapper<TicketInfoEntity>()
                .eq("train_id", trainID.toString())
                .eq("departure_time", departureTime.toString()));
    }

    public List<Object[]> getAllReleasedTickets() {
        List<TicketInfoEntity> entities = ticketInfoMapper.selectList(new QueryWrapper<TicketInfoEntity>()
                .ge("seat_num", 0)
                .orderByAsc("train_id", "departure_time", "departure_station"));
        return toTicketRows(entities);
    }

    public List<Object[]> getAllTickets() {
        List<TicketInfoEntity> entities = ticketInfoMapper.selectList(new QueryWrapper<TicketInfoEntity>()
                .orderByAsc("train_id", "departure_time", "departure_station"));
        return toTicketRows(entities);
    }

    private List<Object[]> toTicketRows(List<TicketInfoEntity> entities) {
        List<Object[]> tickets = new ArrayList<>();
        for (TicketInfoEntity entity : entities) {
            Object[] ticket = new Object[7];
            ticket[0] = entity.getTrainId();
            ticket[1] = entity.getDepartureTime();
            ticket[2] = entity.getDepartureStation();
            ticket[3] = entity.getArrivalStation();
            ticket[4] = entity.getSeatNum();
            ticket[5] = entity.getPrice();
            ticket[6] = entity.getDuration();
            tickets.add(ticket);
        }
        return tickets;
    }
}
