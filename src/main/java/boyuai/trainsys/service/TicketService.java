package boyuai.trainsys.service;

import boyuai.trainsys.config.StaticConfig;
import boyuai.trainsys.dao.RouteDao;
import boyuai.trainsys.dao.TicketDao;
import boyuai.trainsys.dao.TrainDao;
import boyuai.trainsys.model.*;
import boyuai.trainsys.model.TripInfo;
import boyuai.trainsys.model.UserInfo;
import boyuai.trainsys.util.TrainScheduler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {

    private final TicketDao ticketDao;
    private final TrainDao trainDao;
    private final RouteDao routeDao;
    private final UserService userService;

    public TicketService(TicketDao ticketDao, TrainDao trainDao, RouteDao routeDao, UserService userService) {
        this.ticketDao = ticketDao;
        this.trainDao = trainDao;
        this.routeDao = routeDao;
        this.userService = userService;
    }

    public ApiResponse<String> releaseTicket(String sessionId, TicketQueryRequest request) {
        UserInfo user = userService.getCurrentUser(sessionId);
        if (user == null) {
            return ApiResponse.error(401, "未登录");
        }
        userService.bindCurrentUser(sessionId);

        try {
            TrainScheduler scheduler = trainDao.getScheduler(request.getTrainId());
            if (scheduler == null) {
                return ApiResponse.error("车次不存在");
            }
            ticketDao.releaseTicket(scheduler, request);
            return ApiResponse.success("车票发售成功", null);
        } catch (Exception e) {
            return ApiResponse.error("车票发售失败: " + e.getMessage());
        }
    }

    public ApiResponse<String> expireTicket(String sessionId, TicketQueryRequest request) {
        if (userService.getCurrentUser(sessionId) == null) {
            return ApiResponse.error(401, "未登录");
        }
        userService.bindCurrentUser(sessionId);

        try {
            ticketDao.expireTicket(request);
            return ApiResponse.success("车票停售成功", null);
        } catch (Exception e) {
            return ApiResponse.error("车票停售失败: " + e.getMessage());
        }
    }

    public ApiResponse<Integer> queryRemainingTicket(String sessionId, TicketQueryRequest request) {
        if (userService.getCurrentUser(sessionId) == null) {
            return ApiResponse.error(401, "未登录");
        }
        userService.bindCurrentUser(sessionId);

        try {
            Integer stationId = routeDao.stationNameToId(request.getDepartureStation());
            if (stationId == null) {
                return ApiResponse.error("站点不存在: " + request.getDepartureStation());
            }
            return ApiResponse.success(
                    ticketDao.queryRemainingTicket(request.getTrainId(), request.getDepartureTime(), stationId)
            );
        } catch (Exception e) {
            return ApiResponse.error("查询余票失败: " + e.getMessage());
        }
    }

    public ApiResponse<String> buyTicket(String sessionId, BuyTicketRequest request) {
        if (userService.getCurrentUser(sessionId) == null) {
            return ApiResponse.error(401, "未登录");
        }
        userService.bindCurrentUser(sessionId);

        try {
            Integer stationId = routeDao.stationNameToId(request.getDepartureStation());
            if (stationId == null) {
                return ApiResponse.error("站点不存在: " + request.getDepartureStation());
            }

            TrainScheduler scheduler = trainDao.getScheduler(request.getTrainId());
            if (scheduler == null) {
                return ApiResponse.error("车次不存在: " + request.getTrainId());
            }

            int remaining = ticketDao.queryRemainingTicket(request.getTrainId(), request.getDepartureTime(), stationId);
            if (remaining < 0) {
                return ApiResponse.error("该车次该时间的车票尚未发售，请先发售车票");
            }
            if (remaining == 0) {
                return ApiResponse.error("余票不足，无法购票");
            }

            ticketDao.orderTicket(request, stationId);
            return ApiResponse.success("购票成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error("时间格式错误: " + e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("购票失败: " + e.getMessage());
        }
    }

    public ApiResponse<String> refundTicket(String sessionId, RefundTicketRequest request) {
        UserInfo user = userService.getCurrentUser(sessionId);
        if (user == null) {
            return ApiResponse.error(401, "未登录");
        }
        userService.bindCurrentUser(sessionId);

        try {
            Integer stationId = routeDao.stationNameToId(request.getDepartureStation());
            if (stationId == null) {
                return ApiResponse.error("站点不存在: " + request.getDepartureStation());
            }

            boolean hasOrder = false;
            for (TripInfo trip : ticketDao.queryUserTrips(user.getUserID().value())) {
                if (trip.getTrainID().toString().equals(request.getTrainId())
                        && trip.getDepartureStation().value() == stationId
                        && trip.getDepartureTime().toString().equals(request.getDepartureTime())
                        && trip.getType() > 0) {
                    hasOrder = true;
                    break;
                }
            }

            if (!hasOrder) {
                return ApiResponse.error("您没有该订单，无法退票");
            }

            if (trainDao.getScheduler(request.getTrainId()) == null) {
                return ApiResponse.error("车次不存在: " + request.getTrainId());
            }

            ticketDao.refundTicket(request, stationId);
            return ApiResponse.success("退票成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error("时间格式错误: " + e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("退票失败: " + e.getMessage());
        }
    }

    public ApiResponse<List<TripInfoDTO>> queryMyOrders(String sessionId) {
        UserInfo user = userService.getCurrentUser(sessionId);
        if (user == null) {
            return ApiResponse.error(401, "未登录");
        }
        userService.bindCurrentUser(sessionId);

        try {
            List<TripInfoDTO> dtos = new ArrayList<>();
            for (TripInfo trip : ticketDao.queryUserTrips(user.getUserID().value())) {
                TripInfoDTO dto = new TripInfoDTO();
                dto.setTrainId(trip.getTrainID().toString());
                dto.setDepartureStation(routeDao.stationIdToName(trip.getDepartureStation().value()));
                dto.setArrivalStation(routeDao.stationIdToName(trip.getArrivalStation().value()));
                dto.setTicketNumber(trip.getTicketNumber());
                dto.setDuration(trip.getDuration());
                dto.setPrice(trip.getPrice());
                dto.setDepartureTime(trip.getDepartureTime().toString());
                dto.setArrivalTime(trip.getArrivalTime().toString());
                dtos.add(dto);
            }
            return ApiResponse.success(dtos);
        } catch (Exception e) {
            return ApiResponse.error("查询订单失败: " + e.getMessage());
        }
    }

    public ApiResponse<List<TicketInfoDTO>> getTicketList(String sessionId) {
        UserInfo user = userService.getCurrentUser(sessionId);
        if (user == null) {
            return ApiResponse.error(401, "未登录");
        }
        userService.bindCurrentUser(sessionId);

        try {
            List<Object[]> tickets = user.getPrivilege() >= StaticConfig.ADMIN_PRIVILEGE
                    ? ticketDao.getAllTickets()
                    : ticketDao.getAllReleasedTickets();

            List<TicketInfoDTO> dtos = new ArrayList<>();
            for (Object[] ticket : tickets) {
                TicketInfoDTO dto = new TicketInfoDTO();
                dto.setTrainId((String) ticket[0]);
                dto.setDepartureTime((String) ticket[1]);
                dto.setDepartureStation(routeDao.stationIdToName((Integer) ticket[2]));
                dto.setArrivalStation(routeDao.stationIdToName((Integer) ticket[3]));
                dto.setSeatNum((Integer) ticket[4]);
                dto.setPrice((Integer) ticket[5]);
                dto.setDuration((Integer) ticket[6]);
                dtos.add(dto);
            }
            return ApiResponse.success(dtos);
        } catch (Exception e) {
            return ApiResponse.error("获取车票列表失败: " + e.getMessage());
        }
    }
}
