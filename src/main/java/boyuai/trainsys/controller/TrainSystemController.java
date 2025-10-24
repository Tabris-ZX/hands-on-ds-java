package boyuai.trainsys.controller;

import boyuai.trainsys.core.TrainSystem;
import boyuai.trainsys.info.UserInfo;
import boyuai.trainsys.util.Date;
import boyuai.trainsys.util.FixedString;
import boyuai.trainsys.util.Types.UserID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 火车票务管理系统 API 控制器
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TrainSystemController {

    @Autowired
    private TrainSystem trainSystem;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            long userId = Long.parseLong(request.get("userId").toString());
            String password = request.get("password").toString();
            
            trainSystem.login(userId, password);
            
            response.put("success", true);
            response.put("message", "登录成功");
            response.put("user", Map.of(
                "userId", trainSystem.getCurrentUser().getUserID().value(),
                "username", trainSystem.getCurrentUser().getUsername(),
                "privilege", trainSystem.getCurrentUser().getPrivilege()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            long userId = Long.parseLong(request.get("userId").toString());
            String username = request.get("username").toString();
            String password = request.get("password").toString();
            
            trainSystem.addUser(userId, username, password);
            
            response.put("success", true);
            response.put("message", "注册成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        Map<String, Object> response = new HashMap<>();
        try {
            trainSystem.logout();
            response.put("success", true);
            response.put("message", "登出成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 查询用户信息
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserInfo(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            trainSystem.findUserInfoByUserID(userId);
            
            UserInfo userInfo = trainSystem.getUserManager().findUser(new UserID(userId));
            response.put("success", true);
            response.put("user", Map.of(
                "userId", userInfo.getUserID().value(),
                "username", userInfo.getUsername(),
                "privilege", userInfo.getPrivilege()
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 修改用户权限
     */
    @PutMapping("/user/{userId}/privilege")
    public ResponseEntity<Map<String, Object>> modifyPrivilege(@PathVariable Long userId, @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            int newPrivilege = Integer.parseInt(request.get("privilege").toString());
            trainSystem.modifyUserPrivilege(userId, newPrivilege);
            
            response.put("success", true);
            response.put("message", "权限修改成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 修改用户密码
     */
    @PutMapping("/user/{userId}/password")
    public ResponseEntity<Map<String, Object>> modifyPassword(@PathVariable Long userId, @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String newPassword = request.get("password").toString();
            trainSystem.modifyUserPassword(userId, newPassword);
            
            response.put("success", true);
            response.put("message", "密码修改成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 添加列车
     */
    @PostMapping("/train")
    public ResponseEntity<Map<String, Object>> addTrain(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String trainId = request.get("trainId").toString();
            int seatNum = Integer.parseInt(request.get("seatNum").toString());
            int stationCount = Integer.parseInt(request.get("stationCount").toString());
            
            @SuppressWarnings("unchecked")
            java.util.List<String> stationsList = (java.util.List<String>) request.get("stations");
            String[] stations = stationsList.toArray(new String[0]);
            
            @SuppressWarnings("unchecked")
            java.util.List<Integer> durationsList = (java.util.List<Integer>) request.get("durations");
            int[] durations = durationsList.stream().mapToInt(Integer::intValue).toArray();
            
            @SuppressWarnings("unchecked")
            java.util.List<Integer> pricesList = (java.util.List<Integer>) request.get("prices");
            int[] prices = pricesList.stream().mapToInt(Integer::intValue).toArray();
            
            // 转换站点名称为ID
            int[] stationIds = new int[stations.length];
            for (int i = 0; i < stations.length; i++) {
                stationIds[i] = trainSystem.getStationManager().getStationID(stations[i]).value();
            }
            
            trainSystem.addTrainScheduler(new FixedString(trainId), seatNum, stationCount, stationIds, durations, prices);
            
            response.put("success", true);
            response.put("message", "列车添加成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 查询列车信息
     */
    @GetMapping("/train/{trainId}")
    public ResponseEntity<Map<String, Object>> getTrainInfo(@PathVariable String trainId) {
        Map<String, Object> response = new HashMap<>();
        try {
            trainSystem.queryTrainScheduler(new FixedString(trainId));
            
            // 这里需要从schedulerManager获取详细信息
            var scheduler = trainSystem.getSchedulerManager().getScheduler(new FixedString(trainId));
            if (scheduler != null) {
                response.put("success", true);
                response.put("train", Map.of(
                    "trainId", trainId,
                    "seatNum", scheduler.getSeatNum(),
                    "stationCount", scheduler.getPassingStationNum()
                ));
            } else {
                response.put("success", false);
                response.put("message", "列车不存在");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 发布车票
     */
    @PostMapping("/ticket/release")
    public ResponseEntity<Map<String, Object>> releaseTicket(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String trainId = request.get("trainId").toString();
            String dateStr = request.get("date").toString();
            
            var scheduler = trainSystem.getSchedulerManager().getScheduler(new FixedString(trainId));
            if (scheduler == null) {
                response.put("success", false);
                response.put("message", "列车不存在");
                return ResponseEntity.badRequest().body(response);
            }
            
            Date date = new Date(dateStr);
            trainSystem.releaseTicket(scheduler, date);
            
            response.put("success", true);
            response.put("message", "车票发布成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 过期车票
     */
    @PostMapping("/ticket/expire")
    public ResponseEntity<Map<String, Object>> expireTicket(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String trainId = request.get("trainId").toString();
            String dateStr = request.get("date").toString();
            
            Date date = new Date(dateStr);
            trainSystem.expireTicket(new FixedString(trainId), date);
            
            response.put("success", true);
            response.put("message", "车票已过期");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 查询余票
     */
    @GetMapping("/ticket/remaining")
    public ResponseEntity<Map<String, Object>> queryRemaining(@RequestParam String trainId, 
                                                               @RequestParam String date, 
                                                               @RequestParam String departureStation) {
        Map<String, Object> response = new HashMap<>();
        try {
            int remainingSeats = trainSystem.queryRemainingTicket(new FixedString(trainId), new Date(date), 
                trainSystem.getStationManager().getStationID(departureStation));
            
            response.put("success", true);
            response.put("trainId", trainId);
            response.put("date", date);
            response.put("departureStation", departureStation);
            response.put("remainingSeats", remainingSeats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 购票
     */
    @PostMapping("/ticket/buy")
    public ResponseEntity<Map<String, Object>> buyTicket(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String trainId = request.get("trainId").toString();
            String date = request.get("date").toString();
            String departureStation = request.get("departureStation").toString();
            
            trainSystem.orderTicket(new FixedString(trainId), new Date(date), 
                trainSystem.getStationManager().getStationID(departureStation));
            
            response.put("success", true);
            response.put("message", "购票成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 查询订单
     */
    @GetMapping("/ticket/orders")
    public ResponseEntity<Map<String, Object>> queryOrders() {
        Map<String, Object> response = new HashMap<>();
        try {
            trainSystem.queryMyTicket();
            
            response.put("success", true);
            response.put("orders", new java.util.ArrayList<>()); // 这里需要从实际查询结果获取
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 退票
     */
    @PostMapping("/ticket/refund")
    public ResponseEntity<Map<String, Object>> refundTicket(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String trainId = request.get("trainId").toString();
            String date = request.get("date").toString();
            String departureStation = request.get("departureStation").toString();
            
            trainSystem.refundTicket(new FixedString(trainId), new Date(date), 
                trainSystem.getStationManager().getStationID(departureStation));
            
            response.put("success", true);
            response.put("message", "退票成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 显示路线
     */
    @GetMapping("/route/display")
    public ResponseEntity<Map<String, Object>> displayRoute(@RequestParam String startStation, 
                                                            @RequestParam String endStation) {
        Map<String, Object> response = new HashMap<>();
        try {
            trainSystem.findAllRoute(
                trainSystem.getStationManager().getStationID(startStation),
                trainSystem.getStationManager().getStationID(endStation)
            );
            
            response.put("success", true);
            response.put("startStation", startStation);
            response.put("endStation", endStation);
            response.put("routes", new java.util.ArrayList<>()); // 这里需要从实际查询结果获取
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 查询最佳路径
     */
    @GetMapping("/route/best")
    public ResponseEntity<Map<String, Object>> findBestPath(@RequestParam String startStation, 
                                                            @RequestParam String endStation, 
                                                            @RequestParam String preference) {
        Map<String, Object> response = new HashMap<>();
        try {
            int pref = "time".equals(preference) ? 0 : 1; // 0=时间优先, 1=价格优先
            trainSystem.findBestRoute(
                trainSystem.getStationManager().getStationID(startStation),
                trainSystem.getStationManager().getStationID(endStation),
                pref
            );
            
            response.put("success", true);
            response.put("startStation", startStation);
            response.put("endStation", endStation);
            response.put("path", new java.util.ArrayList<>()); // 这里需要从实际查询结果获取
            response.put("totalTime", 0);
            response.put("totalPrice", 0);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 可达性查询
     */
    @GetMapping("/route/accessibility")
    public ResponseEntity<Map<String, Object>> checkAccessibility(@RequestParam String startStation, 
                                                                @RequestParam String endStation) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean accessible = trainSystem.getRailwayGraph().checkStationAccessibility(
                trainSystem.getStationManager().getStationID(startStation).value(),
                trainSystem.getStationManager().getStationID(endStation).value()
            );
            
            response.put("success", true);
            response.put("startStation", startStation);
            response.put("endStation", endStation);
            response.put("accessible", accessible);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
