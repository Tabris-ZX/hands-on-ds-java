package boyuai.trainsys.core;

import boyuai.trainsys.config.Config;
import boyuai.trainsys.info.PurchaseInfo;
import boyuai.trainsys.info.TripInfo;
import boyuai.trainsys.info.UserInfo;
import boyuai.trainsys.manager.RouteSectionManager;
import boyuai.trainsys.manager.SchedulerManager;
import boyuai.trainsys.manager.StationManager;
import boyuai.trainsys.manager.TicketManager;
import boyuai.trainsys.manager.TripManager;
import boyuai.trainsys.manager.UserManager;
import boyuai.trainsys.util.Time;
import boyuai.trainsys.util.FixedString;
import boyuai.trainsys.util.PrioritizedWaitingList;
import boyuai.trainsys.util.Types.StationID;
import boyuai.trainsys.util.Types.TrainID;
import boyuai.trainsys.util.Types.UserID;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/*
 * part1 运行计划管理子系统（需要系统管理员权限）
 * part2 票务管理子系统（需要系统管理员权限）
 * part3 车票交易子系统
 * part4 路线查询子系统
 * part5 用户管理子系统
 */
@Data
@Slf4j
public class TrainSystem {

    private UserInfo currentUser;
    private final UserManager userManager;
    private final RouteSectionManager routeSectionManager;
    private final RailwayGraph railwayGraph;
    private final SchedulerManager schedulerManager;
    private final TicketManager ticketManager;
    private final PrioritizedWaitingList waitingList;
    private final TripManager tripManager;
    private final StationManager stationManager;

    public TrainSystem() throws java.sql.SQLException {
        this.stationManager = new StationManager();
        this.userManager = new UserManager();
        this.routeSectionManager = new RouteSectionManager();
        this.railwayGraph = new RailwayGraph(routeSectionManager);
        this.schedulerManager = new SchedulerManager();
        this.ticketManager = new TicketManager();
        this.waitingList = new PrioritizedWaitingList();
        this.tripManager = new TripManager();

        // 从数据库加载路线区段数据到图结构
        try {
            railwayGraph.loadFromDB();
            log.info("成功从数据库加载路线区段数据");
        } catch (java.sql.SQLException e) {
            log.error("从数据库加载路线区段数据失败", e);
            // 不抛出异常，允许系统继续运行（可能数据库为空）
        }

        // 默认管理员账号ID为0
        UserID adminID = new UserID(0L);
        if (userManager.existUser(adminID)) {
            currentUser = userManager.findUser(adminID);
        } else {
            currentUser = new UserInfo(adminID, "admin", "admin", Config.ADMIN_PRIVILEGE);
            userManager.insertUser(adminID, "admin", "admin", Config.ADMIN_PRIVILEGE);
        }
    }

    // ===== Part 1: 运行计划管理（管理员） =====
    public void addTrainScheduler(FixedString trainID, int seatNum, String startTime, int passingStationNumber,
                                  int[] stations, int[] duration, int[] price) {
        if (currentUser == null || currentUser.getPrivilege() < Config.ADMIN_PRIVILEGE) {
            log.warn("添加车次权限不足，用户: {}", currentUser != null ? currentUser.getUserID().value() : "null");
            throw new RuntimeException("权限不足，需要管理员权限");
        }
        
        // 检查站点ID是否有效
        for (int i = 0; i < passingStationNumber; i++) {
            if (stations[i] < 0 || stations[i] >= Config.MAX_STATIONID) {
                log.warn("无效的站点ID: {}", stations[i]);
                throw new RuntimeException("无效的站点ID: " + stations[i] + "，站点不存在或ID超出范围");
            }
        }
        
        // 验证数组长度
        if (duration.length != passingStationNumber - 1) {
            throw new RuntimeException("时长数组长度错误，应该有 " + (passingStationNumber - 1) + " 个值");
        }
        if (price.length != passingStationNumber - 1) {
            throw new RuntimeException("票价数组长度错误，应该有 " + (passingStationNumber - 1) + " 个值");
        }
        
        try {
            if (schedulerManager.existScheduler(trainID)) {
                log.warn("车次ID已存在: {}", trainID);
                throw new RuntimeException("车次ID已存在: " + trainID);
            }
            schedulerManager.addScheduler(trainID, seatNum, startTime, passingStationNumber, stations, duration, price);
        } catch (java.sql.SQLException e) {
            log.error("数据库操作异常", e);
            throw new RuntimeException("数据库操作失败: " + e.getMessage(), e);
        }
        
        // 添加到路线图
        for (int i = 0; i + 1 < passingStationNumber; i++) {
            railwayGraph.addRoute(stations[i], stations[i + 1], duration[i], price[i], new TrainID(trainID.toString()));
        }
        log.info("车次添加成功: {}", trainID);
    }

    public void queryTrainScheduler(FixedString trainID) {
        if (currentUser == null || currentUser.getPrivilege() < Config.ADMIN_PRIVILEGE) {
            System.out.println("Permission denied.");
            return;
        }
        try {
            TrainScheduler relatedInfo = schedulerManager.getScheduler(trainID);
            if (relatedInfo == null) {
                System.out.println("Train not found.");
                return;
            }
            System.out.println(relatedInfo);
        } catch (java.sql.SQLException e) {
            log.error("数据库操作异常", e);
            System.out.println("数据库操作异常");
        }
    }

    // ===== Part 2: 票务管理（管理员） =====
    public void releaseTicket(TrainScheduler scheduler, Time departureTime) {
        if (currentUser != null && currentUser.getPrivilege() >= Config.ADMIN_PRIVILEGE) {
            if (scheduler == null) {
                System.out.println("Train not found. Please add train first.");
                return;
            }
            try {
                ticketManager.releaseTicket(scheduler, departureTime);
                System.out.println("Ticket released.");
            } catch (java.sql.SQLException e) {
                log.error("数据库操作异常", e);
                System.out.println("数据库操作异常");
            }
        } else {
            System.out.println("Permission denied.");
        }
    }

    public void expireTicket(FixedString trainID, Time departureTime) {
        if (currentUser != null && currentUser.getPrivilege() >= Config.ADMIN_PRIVILEGE) {
            try {
                ticketManager.expireTicket(trainID, departureTime);
                System.out.println("Ticket expired.");
            } catch (java.sql.SQLException e) {
                log.error("数据库操作异常", e);
                System.out.println("数据库操作异常");
            }
        } else {
            System.out.println("Permission denied。");
        }
    }

    // ===== Part 3: 交易 =====
    public int queryRemainingTicket(FixedString trainID, Time departureTime, StationID departureStation) {
        try {
            return ticketManager.querySeat(trainID, departureTime, departureStation.value());
        } catch (java.sql.SQLException e) {
            log.error("数据库操作异常", e);
            System.out.println("数据库操作异常");
            return -1;
        }
    }

    private boolean trySatisfyOrder() {
        if (waitingList.isEmpty()) return false;
        var purchaseInfo = waitingList.getFrontPurchaseInfo();
        waitingList.removeHeadFromWaitingList();

        System.out.println("Processing request from User " + purchaseInfo.getUserID().value());

        if (purchaseInfo.isOrdering()) {
            int remainingTickets = queryRemainingTicket(new TrainID(purchaseInfo.getTrainID().toString()),
                    purchaseInfo.getDepartureTime(), purchaseInfo.getDepartureStation());
            if (remainingTickets < purchaseInfo.getType()) {
                System.out.println("No enough tickets or scheduler not exists. Order failed.");
                return false;
            } else {
                try {
                    ticketManager.updateSeat(new TrainID(purchaseInfo.getTrainID().toString()), purchaseInfo.getDepartureTime(),
                            purchaseInfo.getDepartureStation().value(), -purchaseInfo.getType());

                    TrainScheduler schedule = schedulerManager.getScheduler(new FixedString(purchaseInfo.getTrainID().toString()));
                    int id = schedule.findStation(purchaseInfo.getDepartureStation());
                    int duration = schedule.getDuration(id);
                    int price = schedule.getPrice(id);
                    StationID arrivalStation = schedule.getStation(id + 1);

                    tripManager.addTrip(purchaseInfo.getUserID().value(), new TripInfo(
                            purchaseInfo.getTrainID(), purchaseInfo.getDepartureStation(), arrivalStation,
                            purchaseInfo.getType(), duration, price, purchaseInfo.getDepartureTime()
                    ));

                    System.out.println("Order succeeded.");
                    return true;
                } catch (java.sql.SQLException e) {
                    log.error("数据库操作异常", e);
                    System.out.println("数据库操作异常");
                    return false;
                }
            }
        } else {
            // 退票逻辑
            try {
                // 检查车次是否存在
                TrainScheduler schedule = schedulerManager.getScheduler(new FixedString(purchaseInfo.getTrainID().toString()));
                if (schedule == null) {
                    log.warn("退票失败：车次不存在 {}", purchaseInfo.getTrainID());
                    return false;
                }
                
                // 检查用户是否有该订单
                List<TripInfo> userTrips = tripManager.queryTrip(purchaseInfo.getUserID().value());
                boolean hasOrder = false;
                TripInfo targetTrip = null;
                for (TripInfo trip : userTrips) {
                    if (trip.getTrainID().equals(purchaseInfo.getTrainID()) &&
                        trip.getDepartureStation().equals(purchaseInfo.getDepartureStation()) &&
                        trip.getDepartureTime().equals(purchaseInfo.getDepartureTime()) &&
                        trip.getType() > 0) {  // 只查找购票记录（type > 0）
                        hasOrder = true;
                        targetTrip = trip;
                        break;
                    }
                }
                
                if (!hasOrder || targetTrip == null) {
                    log.warn("退票失败：用户 {} 没有该订单", purchaseInfo.getUserID().value());
                    return false;
                }
                
                // 更新余票（增加票数）
                ticketManager.updateSeat(new TrainID(purchaseInfo.getTrainID().toString()), purchaseInfo.getDepartureTime(),
                        purchaseInfo.getDepartureStation().value(), -purchaseInfo.getType());

                // 删除订单记录（使用数据库中实际存储的type值，即正数）
                tripManager.removeTrip(purchaseInfo.getUserID().value(), targetTrip);
                
                log.info("退票成功：用户 {} 车次 {} 出发站 {}", 
                    purchaseInfo.getUserID().value(), 
                    purchaseInfo.getTrainID(), 
                    purchaseInfo.getDepartureStation().value());
                System.out.println("Refund succeeded.");
                return true;
            } catch (java.sql.SQLException e) {
                log.error("数据库操作异常", e);
                System.out.println("数据库操作异常: " + e.getMessage());
                return false;
            } catch (Exception e) {
                log.error("退票处理异常", e);
                System.out.println("退票处理异常: " + e.getMessage());
                return false;
            }
        }
    }

    public void queryMyTicket() {
        while (waitingList.isBusy()) trySatisfyOrder();
        try {
            java.util.List<TripInfo> tripInfo = tripManager.queryTrip(currentUser.getUserID().value());
            for (TripInfo t : tripInfo) {
                System.out.println(t);
            }
        } catch (java.sql.SQLException e) {
            log.error("数据库操作异常", e);
            System.out.println("数据库操作异常");
            return;
        }
    }

    public void orderTicket(FixedString trainID, Time departureTime, StationID departureStation) {
        waitingList.addToWaitingList(new PurchaseInfo(currentUser.getUserID(), new TrainID(trainID.toString()), departureTime, departureStation, +1));
        System.out.println("Ordering request has added to waiting list.");
        // 立即处理队列
        while (trySatisfyOrder()) {} // 处理完所有可处理订单
    }

    public void refundTicket(FixedString trainID, Time departureTime, StationID departureStation) {
        while (waitingList.isBusy()) trySatisfyOrder();
        waitingList.addToWaitingList(new PurchaseInfo(currentUser.getUserID(), new TrainID(trainID.toString()), departureTime, departureStation, -1));
        System.out.println("Refunding request has added to waiting list.");
        // 立即处理队列
        while (trySatisfyOrder()) {} // 处理完所有可处理订单
    }

    // ===== Part 4: 路线查询 =====
    public String findAllRoute(StationID departureID, StationID arrivalID) {
        return railwayGraph.displayRoute(departureID.value(), arrivalID.value());
    }

    public String findBestRoute(StationID departureID, StationID arrivalID, int preference) {
        return railwayGraph.shortestPath(departureID.value(), arrivalID.value(), preference);
    }

    // ===== Part 5: 用户管理 =====
    public void login(long userID, String password) {
        if (currentUser != null && currentUser.getUserID().value() != -1) {
            System.out.println("Only one user can login in at the same time.");
            return;
        }
        UserID uid = new UserID(userID);
        try {
            if (!userManager.existUser(uid)) {  // 检查用户是否存在
                System.out.println("User not found. Login failed.");
                return;
            }
            UserInfo userInfo = userManager.findUser(uid);
            if (!userInfo.getPassword().equals(password)) {  // 检查密码是否正确
                System.out.println("Wrong password. Login failed.");
                return;
            }
            currentUser = userInfo;  // 登录成功，设置当前用户
            System.out.println("Login succeeded.");
        } catch (java.sql.SQLException e) {
            log.error("数据库操作异常", e);
            System.out.println("数据库操作异常");
            return;
        }
    }

    public void logout() {
        if (currentUser == null || currentUser.getUserID().value() == -1) {
            System.out.println("No user logined.");
            return;
        }
        currentUser = new UserInfo(new UserID(-1L), "", "", 0);
        System.out.println("Logout succeeded.");
    }

    public void addUser(long userID, String username, String password) {
        UserID uid = new UserID(userID);
        try {
            if (userManager.existUser(uid)) {
                System.out.println("User ID existed.");
                return;
            }
            userManager.insertUser(uid, username, password, 0);
            System.out.println("User added.");
        } catch (java.sql.SQLException e) {
            log.error("数据库操作异常", e);
            System.out.println("数据库操作异常");
            return;
        }
    }


    public void findUserInfoByUserID(long userID) {  // 查询用户信息
        UserID uid = new UserID(userID);
        try {
            if (!userManager.existUser(uid)) {  // 检查用户是否存在
                System.out.println("User not found.");
                return;
            }
            UserInfo userInfo = userManager.findUser(uid);  // 查找用户信息
            if (currentUser == null || currentUser.getUserID().value() == -1 || currentUser.getPrivilege() <= userInfo.getPrivilege()) {
                System.out.println("Permission denied.");
                return;
            }
            System.out.println("UserID: " + userInfo.getUserID().value());  // 输出用户信息
            System.out.println("UserName: " + userInfo.getUsername());  // 输出用户名
            System.out.println("Password: " + userInfo.getPassword());  // 输出密码
            System.out.println("Privilege: " + userInfo.getPrivilege());  // 输出权限
        } catch (java.sql.SQLException e) {
            log.error("数据库操作异常", e);
            System.out.println("数据库操作异常");
            return;
        }
    }

    public void modifyUserPassword(long userID, String newPassword) {  // 修改用户密码
        UserID uid = new UserID(userID);
        try {
            if (!userManager.existUser(uid)) {  // 检查用户是否存在
                System.out.println("User not found.");
                return;
            }
            UserInfo userInfo = userManager.findUser(uid);  // 查找用户信息
            if (currentUser == null || currentUser.getUserID().value() == -1 || currentUser.getPrivilege() <= userInfo.getPrivilege()) {
                System.out.println("Modification forbidden.");
                return;
            }
            userManager.modifyUserPassword(uid, newPassword);  // 修改用户密码
            System.out.println("Modification succeeded.");
        } catch (java.sql.SQLException e) {
            log.error("数据库操作异常", e);
            System.out.println("数据库操作异常");
            return;
        }
    }

    public void modifyUserPrivilege(long userID, int newPrivilege) {  // 修改用户权限
        UserID uid = new UserID(userID);
        try {
            if (!userManager.existUser(uid)) {  // 检查用户是否存在
                System.out.println("User not found.");
                return;
            }
            UserInfo userInfo = userManager.findUser(uid);  // 查找用户信息
            if (currentUser == null || currentUser.getUserID().value() == -1 || currentUser.getPrivilege() <= userInfo.getPrivilege()) {
                System.out.println("Modification forbidden.");
                return;
            }
            userManager.modifyUserPrivilege(uid, newPrivilege);  // 修改用户权限    
            System.out.println("Modifiaction succeeded.");
        } catch (java.sql.SQLException e) {
            log.error("数据库操作异常", e);
            System.out.println("数据库操作异常");
            return;
        }
    }

}
