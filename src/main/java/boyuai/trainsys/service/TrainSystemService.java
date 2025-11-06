package boyuai.trainsys.service;

import boyuai.trainsys.core.TrainSystem;
import boyuai.trainsys.core.TrainScheduler;
import boyuai.trainsys.dto.*;
import boyuai.trainsys.info.TripInfo;
import boyuai.trainsys.info.UserInfo;
import boyuai.trainsys.manager.StationManager;
import boyuai.trainsys.util.FixedString;
import boyuai.trainsys.util.Time;
import boyuai.trainsys.util.Types.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 火车票务系统服务类
 * 封装业务逻辑，提供REST API所需的方法
 */
@Slf4j
@Service
public class TrainSystemService {
    
    private final TrainSystem trainSystem;
    private final StationManager stationManager;
    
    // 会话管理（简单的内存存储，实际应该使用Redis等）
    private final Map<String, UserInfo> sessionMap = new HashMap<>();
    
    public TrainSystemService() throws SQLException {
        this.trainSystem = new TrainSystem();
        this.stationManager = trainSystem.getStationManager();
    }
    
    /**
     * 生成会话ID
     */
    private String generateSessionId(UserInfo user) {
        return "session_" + user.getUserID().value() + "_" + System.currentTimeMillis();
    }
    
    /**
     * 登录
     */
    public ApiResponse<Map<String, Object>> login(LoginRequest request) {
        try {
            trainSystem.login(request.getUserId(), request.getPassword());
            UserInfo currentUser = trainSystem.getCurrentUser();
            
            if (currentUser == null || currentUser.getUserID().value() == -1) {
                return ApiResponse.error("登录失败，用户ID或密码错误");
            }
            
            String sessionId = generateSessionId(currentUser);
            sessionMap.put(sessionId, currentUser);
            
            UserInfoDTO userDTO = new UserInfoDTO();
            userDTO.setUserId(currentUser.getUserID().value());
            userDTO.setUsername(currentUser.getUsername());
            userDTO.setPrivilege(currentUser.getPrivilege());
            
            Map<String, Object> result = new HashMap<>();
            result.put("sessionId", sessionId);
            result.put("user", userDTO);
            
            return ApiResponse.success("登录成功", result);
        } catch (Exception e) {
            log.error("登录异常", e);
            return ApiResponse.error("登录失败: " + e.getMessage());
        }
    }
    
    /**
     * 注册
     */
    public ApiResponse<String> register(RegisterRequest request) {
        try {
            trainSystem.addUser(request.getUserId(), request.getUsername(), request.getPassword());
            return ApiResponse.success("注册成功", null);
        } catch (Exception e) {
            log.error("注册异常", e);
            return ApiResponse.error("注册失败: " + e.getMessage());
        }
    }
    
    /**
     * 登出
     */
    public ApiResponse<String> logout(String sessionId) {
        sessionMap.remove(sessionId);
        trainSystem.logout();
        return ApiResponse.success("登出成功", null);
    }
    
    /**
     * 获取当前用户信息
     */
    public UserInfo getCurrentUser(String sessionId) {
        return sessionMap.get(sessionId);
    }
    
    /**
     * 添加车次（管理员）
     */
    public ApiResponse<String> addTrain(String sessionId, AddTrainRequest request) {
        UserInfo user = getCurrentUser(sessionId);
        if (user == null) {
            return ApiResponse.error(401, "未登录");
        }
        
        trainSystem.setCurrentUser(user);
        
        try {
            // 转换站点名称为站点ID
            int[] stationIds = new int[request.getStations().size()];
            for (int i = 0; i < request.getStations().size(); i++) {
                String stationName = request.getStations().get(i);
                Integer stationId = stationManager.nameToID(stationName);
                if (stationId == null) {
                    return ApiResponse.error("站点不存在: " + stationName);
                }
                stationIds[i] = stationId;
            }
            
            // 转换时长和票价数组
            int[] durations = request.getDurations().stream().mapToInt(i -> i).toArray();
            int[] prices = request.getPrices().stream().mapToInt(i -> i).toArray();
            
            // 解析首发时间（只保留HH:MM格式，数据库只需要时间部分）
            String startTimeStr = request.getStartTime().trim();
            
            // 验证时间格式
            if (!startTimeStr.matches("^\\d{2}:\\d{2}$")) {
                return ApiResponse.error("首发时间格式错误，应为 HH:MM 格式，如 08:00");
            }
            
            // 验证时间范围
            String[] timeParts = startTimeStr.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int min = Integer.parseInt(timeParts[1]);
            if (hour < 0 || hour > 23 || min < 0 || min > 59) {
                return ApiResponse.error("时间范围错误，小时应在0-23之间，分钟应在0-59之间");
            }
            
            // 验证数组长度
            if (durations.length != request.getStations().size() - 1) {
                return ApiResponse.error("区段时长数量错误，应该有 " + (request.getStations().size() - 1) + " 个时长值");
            }
            if (prices.length != request.getStations().size() - 1) {
                return ApiResponse.error("区段票价数量错误，应该有 " + (request.getStations().size() - 1) + " 个票价值");
            }
            
            // 调用添加方法，传递HH:MM格式的时间字符串
            trainSystem.addTrainScheduler(
                new FixedString(request.getTrainId()),
                request.getSeatNum(),
                startTimeStr,  // 直接传递HH:MM格式
                request.getStations().size(),
                stationIds,
                durations,
                prices
            );
            
            // 检查是否真的添加成功（通过查询验证）
            TrainScheduler addedScheduler = trainSystem.getSchedulerManager().getScheduler(
                new FixedString(request.getTrainId())
            );
            if (addedScheduler == null) {
                return ApiResponse.error("车次添加失败，可能是车次ID已存在或数据库操作失败");
            }
            
            return ApiResponse.success("车次添加成功", null);
        } catch (Exception e) {
            log.error("添加车次异常", e);
            return ApiResponse.error("添加车次失败: " + e.getMessage());
        }
    }
    
    /**
     * 查询车次（管理员）
     */
    public ApiResponse<TrainSchedulerDTO> queryTrain(String sessionId, String trainId) {
        UserInfo user = getCurrentUser(sessionId);
        if (user == null) {
            return ApiResponse.error(401, "未登录");
        }
        
        trainSystem.setCurrentUser(user);
        
        try {
            TrainScheduler scheduler = trainSystem.getSchedulerManager().getScheduler(
                new FixedString(trainId)
            );
            
            if (scheduler == null) {
                return ApiResponse.error("车次不存在");
            }
            
            TrainSchedulerDTO dto = new TrainSchedulerDTO();
            dto.setTrainId(scheduler.getTrainID().toString());
            dto.setSeatNum(scheduler.getSeatNum());
            dto.setStartTime(scheduler.getStartTime() != null ? scheduler.getStartTime().toString() : "");
            
            List<String> stations = new ArrayList<>();
            for (int i = 0; i < scheduler.getPassingStationNum(); i++) {
                StationID stationId = scheduler.getStation(i);
                String stationName = stationManager.idToName(stationId.value());
                stations.add(stationName);
            }
            dto.setStations(stations);
            
            List<Integer> durations = new ArrayList<>();
            List<Integer> prices = new ArrayList<>();
            for (int i = 0; i + 1 < scheduler.getPassingStationNum(); i++) {
                durations.add(scheduler.getDuration(i));
                prices.add(scheduler.getPrice(i));
            }
            dto.setDurations(durations);
            dto.setPrices(prices);
            
            return ApiResponse.success(dto);
        } catch (Exception e) {
            log.error("查询车次异常", e);
            return ApiResponse.error("查询车次失败: " + e.getMessage());
        }
    }
    
    /**
     * 发售车票（管理员）
     */
    public ApiResponse<String> releaseTicket(String sessionId, TicketQueryRequest request) {
        UserInfo user = getCurrentUser(sessionId);
        if (user == null) {
            return ApiResponse.error(401, "未登录");
        }
        
        trainSystem.setCurrentUser(user);
        
        try {
            TrainScheduler scheduler = trainSystem.getSchedulerManager().getScheduler(
                new FixedString(request.getTrainId())
            );
            
            if (scheduler == null) {
                return ApiResponse.error("车次不存在");
            }
            
            Time departureTime = new Time(request.getDepartureTime());
            trainSystem.releaseTicket(scheduler, departureTime);
            
            return ApiResponse.success("车票发售成功", null);
        } catch (Exception e) {
            log.error("发售车票异常", e);
            return ApiResponse.error("发售车票失败: " + e.getMessage());
        }
    }
    
    /**
     * 停售车票（管理员）
     */
    public ApiResponse<String> expireTicket(String sessionId, TicketQueryRequest request) {
        UserInfo user = getCurrentUser(sessionId);
        if (user == null) {
            return ApiResponse.error(401, "未登录");
        }
        
        trainSystem.setCurrentUser(user);
        
        try {
            Time departureTime = new Time(request.getDepartureTime());
            trainSystem.expireTicket(new FixedString(request.getTrainId()), departureTime);
            
            return ApiResponse.success("车票停售成功", null);
        } catch (Exception e) {
            log.error("停售车票异常", e);
            return ApiResponse.error("停售车票失败: " + e.getMessage());
        }
    }
    
    /**
     * 查询余票
     */
    public ApiResponse<Integer> queryRemainingTicket(String sessionId, TicketQueryRequest request) {
        UserInfo user = getCurrentUser(sessionId);
        if (user == null) {
            return ApiResponse.error(401, "未登录");
        }
        
        trainSystem.setCurrentUser(user);
        
        try {
            Integer stationId = stationManager.nameToID(request.getDepartureStation());
            if (stationId == null) {
                return ApiResponse.error("站点不存在: " + request.getDepartureStation());
            }
            
            Time departureTime = new Time(request.getDepartureTime());
            int remaining = trainSystem.queryRemainingTicket(
                new FixedString(request.getTrainId()),
                departureTime,
                new StationID(stationId)
            );
            
            return ApiResponse.success(remaining);
        } catch (Exception e) {
            log.error("查询余票异常", e);
            return ApiResponse.error("查询余票失败: " + e.getMessage());
        }
    }
    
    /**
     * 购票
     */
    public ApiResponse<String> buyTicket(String sessionId, BuyTicketRequest request) {
        UserInfo user = getCurrentUser(sessionId);
        if (user == null) {
            return ApiResponse.error(401, "未登录");
        }
        
        trainSystem.setCurrentUser(user);
        
        try {
            Integer stationId = stationManager.nameToID(request.getDepartureStation());
            if (stationId == null) {
                return ApiResponse.error("站点不存在: " + request.getDepartureStation());
            }
            
            Time departureTime = new Time(request.getDepartureTime());
            
            // 检查车次是否存在
            TrainScheduler scheduler = trainSystem.getSchedulerManager().getScheduler(
                new FixedString(request.getTrainId())
            );
            if (scheduler == null) {
                return ApiResponse.error("车次不存在: " + request.getTrainId());
            }
            
            // 检查车票是否已发售
            int remaining = trainSystem.queryRemainingTicket(
                new FixedString(request.getTrainId()),
                departureTime,
                new StationID(stationId)
            );
            if (remaining < 0) {
                return ApiResponse.error("该车次该时间点的车票尚未发售，请先发售车票");
            }
            if (remaining == 0) {
                return ApiResponse.error("余票不足，无法购票");
            }
            
            trainSystem.orderTicket(
                new FixedString(request.getTrainId()),
                departureTime,
                new StationID(stationId)
            );
            
            return ApiResponse.success("购票成功", null);
        } catch (IllegalArgumentException e) {
            log.error("购票参数错误", e);
            return ApiResponse.error("时间格式错误: " + e.getMessage());
        } catch (Exception e) {
            log.error("购票异常", e);
            return ApiResponse.error("购票失败: " + e.getMessage());
        }
    }
    
    /**
     * 退票
     */
    public ApiResponse<String> refundTicket(String sessionId, RefundTicketRequest request) {
        UserInfo user = getCurrentUser(sessionId);
        if (user == null) {
            return ApiResponse.error(401, "未登录");
        }
        
        trainSystem.setCurrentUser(user);
        
        try {
            Integer stationId = stationManager.nameToID(request.getDepartureStation());
            if (stationId == null) {
                return ApiResponse.error("站点不存在: " + request.getDepartureStation());
            }
            
            // 检查用户是否有该订单
            List<TripInfo> userTrips = trainSystem.getTripManager().queryTrip(user.getUserID().value());
            boolean hasOrder = false;
            for (TripInfo trip : userTrips) {
                if (trip.getTrainID().toString().equals(request.getTrainId()) &&
                    trip.getDepartureStation().value() == stationId &&
                    trip.getDepartureTime().toString().equals(request.getDepartureTime()) &&
                    trip.getType() > 0) {  // 只查找购票记录
                    hasOrder = true;
                    break;
                }
            }
            
            if (!hasOrder) {
                return ApiResponse.error("您没有该订单，无法退票");
            }
            
            // 检查车次是否存在
            TrainScheduler scheduler = trainSystem.getSchedulerManager().getScheduler(
                new FixedString(request.getTrainId())
            );
            if (scheduler == null) {
                return ApiResponse.error("车次不存在: " + request.getTrainId());
            }
            
            Time departureTime = new Time(request.getDepartureTime());
            trainSystem.refundTicket(
                new FixedString(request.getTrainId()),
                departureTime,
                new StationID(stationId)
            );
            
            return ApiResponse.success("退票成功", null);
        } catch (IllegalArgumentException e) {
            log.error("退票参数错误", e);
            return ApiResponse.error("时间格式错误: " + e.getMessage());
        } catch (Exception e) {
            log.error("退票异常", e);
            return ApiResponse.error("退票失败: " + e.getMessage());
        }
    }
    
    /**
     * 查询我的订单
     */
    public ApiResponse<List<TripInfoDTO>> queryMyOrders(String sessionId) {
        UserInfo user = getCurrentUser(sessionId);
        if (user == null) {
            return ApiResponse.error(401, "未登录");
        }
        
        trainSystem.setCurrentUser(user);
        
        try {
            List<TripInfo> trips = trainSystem.getTripManager().queryTrip(user.getUserID().value());
            List<TripInfoDTO> dtos = new ArrayList<>();
            
            for (TripInfo trip : trips) {
                TripInfoDTO dto = new TripInfoDTO();
                dto.setTrainId(trip.getTrainID().toString());
                dto.setDepartureStation(stationManager.idToName(trip.getDepartureStation().value()));
                dto.setArrivalStation(stationManager.idToName(trip.getArrivalStation().value()));
                dto.setTicketNumber(trip.getTicketNumber());
                dto.setDuration(trip.getDuration());
                dto.setPrice(trip.getPrice());
                dto.setDepartureTime(trip.getDepartureTime().toString());
                dto.setArrivalTime(trip.getArrivalTime().toString());
                dtos.add(dto);
            }
            
            return ApiResponse.success(dtos);
        } catch (Exception e) {
            log.error("查询订单异常", e);
            return ApiResponse.error("查询订单失败: " + e.getMessage());
        }
    }
    
    /**
     * 查询所有路线
     */
    public ApiResponse<String> findAllRoute(RouteQueryRequest request) {
        try {
            Integer depId = stationManager.nameToID(request.getDepartureStation());
            Integer arrId = stationManager.nameToID(request.getArrivalStation());
            
            if (depId == null || arrId == null) {
                return ApiResponse.error("站点不存在");
            }
            
            String result = trainSystem.findAllRoute(new StationID(depId), new StationID(arrId));
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("查询路线异常", e);
            return ApiResponse.error("查询路线失败: " + e.getMessage());
        }
    }
    
    /**
     * 查询最优路线
     */
    public ApiResponse<String> findBestRoute(RouteQueryRequest request) {
        try {
            Integer depId = stationManager.nameToID(request.getDepartureStation());
            Integer arrId = stationManager.nameToID(request.getArrivalStation());
            
            if (depId == null || arrId == null) {
                return ApiResponse.error("站点不存在");
            }
            
            int preference = "price".equals(request.getPreference()) ? 0 : 1;
            String result = trainSystem.findBestRoute(new StationID(depId), new StationID(arrId), preference);
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("查询最优路线异常", e);
            return ApiResponse.error("查询最优路线失败: " + e.getMessage());
        }
    }
    
    /**
     * 查询站点连通性
     */
    public ApiResponse<Boolean> queryAccessibility(RouteQueryRequest request) {
        try {
            Integer depId = stationManager.nameToID(request.getDepartureStation());
            Integer arrId = stationManager.nameToID(request.getArrivalStation());
            
            if (depId == null || arrId == null) {
                return ApiResponse.error("站点不存在");
            }
            
            boolean accessible = trainSystem.getRailwayGraph().checkStationAccessibility(depId, arrId);
            return ApiResponse.success(accessible);
        } catch (Exception e) {
            log.error("查询连通性异常", e);
            return ApiResponse.error("查询连通性失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有站点列表
     */
    public ApiResponse<List<String>> getAllStations() {
        try {
            List<String> stations = new ArrayList<>(stationManager.getAllStationNames());
            return ApiResponse.success(stations);
        } catch (Exception e) {
            log.error("获取站点列表异常", e);
            return ApiResponse.error("获取站点列表失败: " + e.getMessage());
        }
    }
}

