package boyuai.trainsys.service;

import boyuai.trainsys.model.AddTrainRequest;
import boyuai.trainsys.model.ApiResponse;
import boyuai.trainsys.model.TrainSchedulerDTO;
import boyuai.trainsys.util.TrainScheduler;
import boyuai.trainsys.util.Types.StationID;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import boyuai.trainsys.dao.RouteDao;
import boyuai.trainsys.dao.TrainDao;

@Service
public class TrainService {

    private final TrainDao trainDao;
    private final RouteDao routeDao;
    private final UserService userService;

    public TrainService(TrainDao trainDao, RouteDao routeDao, UserService userService) {
        this.trainDao = trainDao;
        this.routeDao = routeDao;
        this.userService = userService;
    }

    public ApiResponse<String> addTrain(String sessionId, AddTrainRequest request) {
        if (userService.getCurrentUser(sessionId) == null) {
            return ApiResponse.error(401, "未登录");
        }

        userService.bindCurrentUser(sessionId);

        try {
            int[] stationIds = new int[request.getStations().size()];
            for (int i = 0; i < request.getStations().size(); i++) {
                Integer stationId = routeDao.stationNameToId(request.getStations().get(i));
                if (stationId == null) {
                    return ApiResponse.error("站点不存在: " + request.getStations().get(i));
                }
                stationIds[i] = stationId;
            }

            String startTimeStr = request.getStartTime().trim();
            if (!startTimeStr.matches("^\\d{2}:\\d{2}$")) {
                return ApiResponse.error("首发时间格式错误，应为 HH:MM，例如 08:00");
            }

            int[] durations = request.getDurations().stream().mapToInt(i -> i).toArray();
            int[] prices = request.getPrices().stream().mapToInt(i -> i).toArray();

            trainDao.addTrain(request, stationIds, durations, prices);
            if (trainDao.getScheduler(request.getTrainId()) == null) {
                return ApiResponse.error("车次添加失败");
            }

            return ApiResponse.success("车次添加成功", null);
        } catch (Exception e) {
            return ApiResponse.error("车次添加失败: " + e.getMessage());
        }
    }

    public ApiResponse<TrainSchedulerDTO> queryTrain(String sessionId, String trainId) {
        if (userService.getCurrentUser(sessionId) == null) {
            return ApiResponse.error(401, "未登录");
        }

        userService.bindCurrentUser(sessionId);

        try {
            TrainScheduler scheduler = trainDao.getScheduler(trainId);
            if (scheduler == null) {
                return ApiResponse.error("车次不存在: " + trainId);
            }
            return ApiResponse.success(toDto(scheduler));
        } catch (Exception e) {
            return ApiResponse.error("查询车次失败: " + e.getMessage());
        }
    }

    public ApiResponse<List<TrainSchedulerDTO>> getAllTrainSchedulers(String sessionId) {
        if (userService.getCurrentUser(sessionId) == null) {
            return ApiResponse.error(401, "未登录");
        }

        userService.bindCurrentUser(sessionId);

        try {
            List<TrainSchedulerDTO> result = new ArrayList<>();
            for (TrainScheduler scheduler : trainDao.getAllSchedulers()) {
                result.add(toDto(scheduler));
            }
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("获取车次列表失败: " + e.getMessage());
        }
    }

    private TrainSchedulerDTO toDto(TrainScheduler scheduler) {
        TrainSchedulerDTO dto = new TrainSchedulerDTO();
        dto.setTrainId(scheduler.getTrainID().toString());
        dto.setSeatNum(scheduler.getSeatNum());
        dto.setStartTime(scheduler.getStartTime() != null ? scheduler.getStartTime().toString() : "");

        List<String> stations = new ArrayList<>();
        List<Integer> durations = new ArrayList<>();
        List<Integer> prices = new ArrayList<>();

        for (int i = 0; i < scheduler.getPassingStationNum(); i++) {
            StationID stationId = scheduler.getStation(i);
            stations.add(stationId != null ? routeDao.stationIdToName(stationId.value()) : null);
        }

        for (int i = 0; i + 1 < scheduler.getPassingStationNum(); i++) {
            durations.add(scheduler.getDuration(i));
            prices.add(scheduler.getPrice(i));
        }

        dto.setStations(stations);
        dto.setDurations(durations);
        dto.setPrices(prices);
        return dto;
    }
}
