package boyuai.trainsys.core;

import boyuai.trainsys.config.Config;
import boyuai.trainsys.info.PurchaseInfo;
import boyuai.trainsys.info.TripInfo;
import boyuai.trainsys.info.UserInfo;
import boyuai.trainsys.manager.SchedulerManager;
import boyuai.trainsys.manager.StationManager;
import boyuai.trainsys.manager.TicketManager;
import boyuai.trainsys.manager.TripManager;
import boyuai.trainsys.manager.UserManager;
import boyuai.trainsys.util.Date;
import boyuai.trainsys.util.FixedString;
import boyuai.trainsys.util.PrioritizedWaitingList;
import boyuai.trainsys.util.Types.StationID;
import boyuai.trainsys.util.Types.TrainID;
import boyuai.trainsys.util.Types.UserID;
import lombok.Getter;

/**
 * 火车票务系统主类（Java实现，保持与C++版本一致的行为）
 */
@Getter
public class TrainSystem {

    private UserInfo currentUser;
    private final UserManager userManager;
    private final RailwayGraph railwayGraph;
    private final SchedulerManager schedulerManager;
    private final TicketManager ticketManager;
    private final PrioritizedWaitingList waitingList;
    private final TripManager tripManager;
    private final StationManager stationManager;

    public TrainSystem() {
        this.stationManager = new StationManager("data/station.txt");
        this.userManager = new UserManager("data/users");
        this.railwayGraph = new RailwayGraph();
        this.schedulerManager = new SchedulerManager("data/schedulers");
        this.ticketManager = new TicketManager("data/tickets");
        this.waitingList = new PrioritizedWaitingList();
        this.tripManager = new TripManager("data/trips");

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
    public void addTrainScheduler(FixedString trainID, int seatNum, int passingStationNumber,
                                  int[] stations, int[] duration, int[] price) {
        if (currentUser == null || currentUser.getPrivilege() < Config.ADMIN_PRIVILEGE) {
            System.out.println("Permission denied.");
            return;
        }
        if (schedulerManager.existScheduler(trainID)) {
            System.out.println("TrainID existed.");
            return;
        }
        schedulerManager.addScheduler(trainID, seatNum, passingStationNumber, stations, duration, price);
        for (int i = 0; i + 1 < passingStationNumber; i++) {
            railwayGraph.addRoute(stations[i], stations[i + 1], duration[i], price[i], new TrainID(trainID.toString()));
        }
        System.out.println("Train added.");
    }

    public void queryTrainScheduler(FixedString trainID) {
        if (currentUser == null || currentUser.getPrivilege() < Config.ADMIN_PRIVILEGE) {
            System.out.println("Permission denied.");
            return;
        }
        TrainScheduler relatedInfo = schedulerManager.getScheduler(trainID);
        if (relatedInfo == null) {
            System.out.println("Train not found.");
            return;
        }
        System.out.println(relatedInfo);
    }

    // ===== Part 2: 票务管理（管理员） =====
    public void releaseTicket(TrainScheduler scheduler, Date date) {
        if (currentUser != null && currentUser.getPrivilege() >= Config.ADMIN_PRIVILEGE) {
            ticketManager.releaseTicket(scheduler, date);
            System.out.println("Ticket released.");
        } else {
            System.out.println("Permission denied.");
        }
    }

    public void expireTicket(FixedString trainID, Date date) {
        if (currentUser != null && currentUser.getPrivilege() >= Config.ADMIN_PRIVILEGE) {
            ticketManager.expireTicket(trainID, date);
            System.out.println("Ticket expired.");
        } else {
            System.out.println("Permission denied.");
        }
    }

    // ===== Part 3: 交易 =====
    public int queryRemainingTicket(FixedString trainID, Date date, StationID departureStation) {
        return ticketManager.querySeat(trainID, date, departureStation.value());
    }

    private boolean trySatisfyOrder() {
        if (waitingList.isEmpty()) return false;
        var purchaseInfo = waitingList.getFrontPurchaseInfo();
        waitingList.removeHeadFromWaitingList();

        System.out.println("Processing request from User " + purchaseInfo.getUserID().value());

        if (purchaseInfo.isOrdering()) {
            int remainingTickets = queryRemainingTicket(new TrainID(purchaseInfo.getTrainID().toString()),
                    purchaseInfo.getDate(), purchaseInfo.getDepartureStation());
            if (remainingTickets < purchaseInfo.getType()) {
                System.out.println("No enough tickets or scheduler not exists. Order failed.");
                return false;
            } else {
                ticketManager.updateSeat(new TrainID(purchaseInfo.getTrainID().toString()), purchaseInfo.getDate(),
                        purchaseInfo.getDepartureStation().value(), -purchaseInfo.getType());

                TrainScheduler schedule = schedulerManager.getScheduler(new FixedString(purchaseInfo.getTrainID().toString()));
                int id = schedule.findStation(purchaseInfo.getDepartureStation());
                int duration = schedule.getDuration(id);
                int price = schedule.getPrice(id);
                StationID arrivalStation = schedule.getStation(id + 1);

                tripManager.addTrip(currentUser.getUserID().value(), new TripInfo(
                        purchaseInfo.getTrainID(), purchaseInfo.getDepartureStation(), arrivalStation,
                        purchaseInfo.getType(), duration, price, purchaseInfo.getDate()
                ));

                System.out.println("Order succeeded.");
                return true;
            }
        } else {
            ticketManager.updateSeat(new TrainID(purchaseInfo.getTrainID().toString()), purchaseInfo.getDate(),
                    purchaseInfo.getDepartureStation().value(), -purchaseInfo.getType());

            TrainScheduler schedule = schedulerManager.getScheduler(new FixedString(purchaseInfo.getTrainID().toString()));
            int id = schedule.findStation(purchaseInfo.getDepartureStation());
            int duration = schedule.getDuration(id);
            int price = schedule.getPrice(id);
            StationID arrivalStation = schedule.getStation(id + 1);

            tripManager.removeTrip(currentUser.getUserID().value(), new TripInfo(
                    purchaseInfo.getTrainID(), purchaseInfo.getDepartureStation(), arrivalStation,
                    -purchaseInfo.getType(), duration, price, purchaseInfo.getDate()
            ));
            System.out.println("Refund succeeded.");
            return true;
        }
    }

    public void queryMyTicket() {
        while (waitingList.isBusy()) trySatisfyOrder();
        var tripInfo = tripManager.queryTrip(currentUser.getUserID().value());
        for (int i = 0; i < tripInfo.length(); i++) {
            System.out.println(tripInfo.visit(i));
        }
    }

    public void orderTicket(FixedString trainID, Date date, StationID departureStation) {
        while (waitingList.isBusy()) trySatisfyOrder();
        waitingList.addToWaitingList(new PurchaseInfo(currentUser.getUserID(), new TrainID(trainID.toString()), date, departureStation, +1));
        System.out.println("Ordering request has added to waiting list.");
    }

    public void refundTicket(FixedString trainID, Date date, StationID departureStation) {
        while (waitingList.isBusy()) trySatisfyOrder();
        waitingList.addToWaitingList(new PurchaseInfo(currentUser.getUserID(), new TrainID(trainID.toString()), date, departureStation, -1));
        System.out.println("Refunding request has added to waiting list.");
    }

    // ===== Part 4: 路线查询 =====
    public void findAllRoute(StationID departureID, StationID arrivalID) {
        if (!railwayGraph.checkStationAccessibility(departureID.value(), arrivalID.value())) {
            System.out.println("Disconnected. No route found.");
            return;
        }
        railwayGraph.displayRoute(departureID.value(), arrivalID.value());
    }

    public void findBestRoute(StationID departureID, StationID arrivalID, int preference) {
        if (!railwayGraph.checkStationAccessibility(departureID.value(), arrivalID.value())) {
            System.out.println("Disconnected. No route found.");
            return;
        }
        railwayGraph.shortestPath(departureID.value(), arrivalID.value(), preference);
    }

    // ===== Part 5: 用户管理 =====
    public void login(long userID, String password) {
        if (currentUser != null && currentUser.getUserID().value() != -1) {
            System.out.println("Only one user can login in at the same time.");
            return;
        }
        UserID uid = new UserID(userID);
        if (!userManager.existUser(uid)) {
            System.out.println("User not found. Login failed.");
            return;
        }
        UserInfo userInfo = userManager.findUser(uid);
        if (!userInfo.getPassword().equals(password)) {
            System.out.println("Wrong password. Login failed.");
            return;
        }
        currentUser = userInfo;
        System.out.println("Login succeeded.");
    }

    public void logout() {
        if (currentUser == null || currentUser.getUserID().value() == -1) {
            System.out.println("No user logined.");
            return;
        }
        currentUser.getUserID().value();
        currentUser = new UserInfo(new UserID(-1L), "", "", 0);
    }

    public void addUser(long userID, String username, String password) {
        UserID uid = new UserID(userID);
        if (userManager.existUser(uid)) {
            System.out.println("User ID existed.");
            return;
        }
        if (currentUser == null || currentUser.getUserID().value() == -1) {
            System.out.println("Permission denied.");
            return;
        }
        userManager.insertUser(uid, username, password, 0);
        System.out.println("User added.");
    }

    public void findUserInfoByUserID(long userID) {
        UserID uid = new UserID(userID);
        if (!userManager.existUser(uid)) {
            System.out.println("User not found.");
            return;
        }
        UserInfo userInfo = userManager.findUser(uid);
        if (currentUser == null || currentUser.getUserID().value() == -1 || currentUser.getPrivilege() <= userInfo.getPrivilege()) {
            System.out.println("Permission denied.");
            return;
        }
        System.out.println("UserID: " + userInfo.getUserID().value());
        System.out.println("UserName: " + userInfo.getUsername());
        System.out.println("Password: " + userInfo.getPassword());
        System.out.println("Privilege: " + userInfo.getPrivilege());
    }

    public void modifyUserPassword(long userID, String newPassword) {
        UserID uid = new UserID(userID);
        if (!userManager.existUser(uid)) {
            System.out.println("User not found.");
            return;
        }
        UserInfo userInfo = userManager.findUser(uid);
        if (currentUser == null || currentUser.getUserID().value() == -1 || currentUser.getPrivilege() <= userInfo.getPrivilege()) {
            System.out.println("Modification forbidden.");
            return;
        }
        userManager.modifyUserPassword(uid, newPassword);
        System.out.println("Modification succeeded.");
    }

    public void modifyUserPrivilege(long userID, int newPrivilege) {
        UserID uid = new UserID(userID);
        if (!userManager.existUser(uid)) {
            System.out.println("User not found.");
            return;
        }
        UserInfo userInfo = userManager.findUser(uid);
        if (currentUser == null || currentUser.getUserID().value() == -1 || currentUser.getPrivilege() <= userInfo.getPrivilege()) {
            System.out.println("Modification forbidden.");
            return;
        }
        userManager.modifyUserPrivilege(uid, newPrivilege);
        System.out.println("Modifiaction succeeded.");
    }
}
