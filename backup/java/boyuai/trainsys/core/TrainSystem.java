package boyuai.trainsys.core;

import boyuai.trainsys.config.Config;
import boyuai.trainsys.info.UserInfo;
import boyuai.trainsys.manager.StationManager;
import boyuai.trainsys.manager.UserManager;
import boyuai.trainsys.util.Date;
import boyuai.trainsys.util.PrioritizedWaitingList;
import boyuai.trainsys.util.Types.StationID;
import boyuai.trainsys.util.Types.TrainID;
import boyuai.trainsys.util.Types.UserID;
import lombok.Getter;
import lombok.Setter;

/**
 * 火车票务系统主类
 */
@Setter
@Getter
public class TrainSystem {

    // 系统组件
    private  UserInfo currentUser;
    private  UserManager userManager;
    private static StationManager stationManager;
    private PrioritizedWaitingList waitingList;
    // 其他管理器可以根据需要添加

    /**
     * 初始化系统
     */
    public static void init() {
        stationManager = new StationManager("station.txt");
        userManager = new UserManager("user.dat");
        waitingList = new PrioritizedWaitingList();

        // 开启系统时，自动登录管理员。默认管理员账号ID为0
        System.out.println("admin login");
        UserID adminID = new UserID(0);
        if (userManager.existUser(adminID)) {
            currentUser = userManager.findUser(adminID);
        } else {
            currentUser = new UserInfo(adminID, "admin", "admin", 100);
            userManager.insertUser(adminID, "admin", "admin", 100);
        }
    }

    /**
     * 关闭系统
     */
    public static void shutdown() {
        if (userManager != null) {
            userManager.close();
        }
        // 关闭其他管理器
    }

    // ===== Part 1: 运行计划管理子系统（需要系统管理员权限）=====

    /**
     * 添加列车时刻表
     */
    public static void addTrainScheduler(TrainID trainID, int seatNum, int passingStationNumber,
                                         StationID[] stations, int[] duration, int[] price) {
        // 检查权限
        if (currentUser.getPrivilege() < Config.ADMIN_PRIVILEGE) {
            System.out.println("权限不足");
            return;
        }

        // TODO: 实现添加列车时刻表的逻辑
        System.out.println("添加列车时刻表: " + trainID);
    }

    /**
     * 查询列车时刻表
     */
    public static void queryTrainScheduler(TrainID trainID) {
        // TODO: 实现查询列车时刻表的逻辑
        System.out.println("查询列车时刻表: " + trainID);
    }

    // ===== Part 2: 票务管理子系统（需要系统管理员权限）=====

    /**
     * 发布车票
     */
    public static void releaseTicket(TrainScheduler scheduler, Date date) {
        // 检查权限
        if (currentUser.getPrivilege() < Config.ADMIN_PRIVILEGE) {
            System.out.println("权限不足");
            return;
        }

        // TODO: 实现发布车票的逻辑
        System.out.println("发布车票: " + scheduler.getTrainID() + " 日期: " + date);
    }

    /**
     * 使车票过期
     */
    public static void expireTicket(TrainID trainID, Date date) {
        // 检查权限
        if (currentUser.getPrivilege() < Config.ADMIN_PRIVILEGE) {
            System.out.println("权限不足");
            return;
        }

        // TODO: 实现使车票过期的逻辑
        System.out.println("使车票过期: " + trainID + " 日期: " + date);
    }

    // ===== Part 3: 车票交易子系统 =====

    /**
     * 查询余票
     */
    public static int queryRemainingTicket(TrainID trainID, Date date, StationID departureStation) {
        // TODO: 实现查询余票的逻辑
        System.out.println("查询余票: " + trainID);
        return 0;
    }

    /**
     * 查询我的车票
     */
    public static void queryMyTicket() {
        // TODO: 实现查询我的车票的逻辑
        System.out.println("查询用户 " + currentUser.getUserID() + " 的车票");
    }

    /**
     * 订票
     */
    public static void orderTicket(TrainID trainID, Date date, StationID departureStation) {
        // TODO: 实现订票的逻辑
        System.out.println("订票: " + trainID + " 日期: " + date);
    }

    /**
     * 退票
     */
    public static void refundTicket(TrainID trainID, Date date, StationID departureStation) {
        // TODO: 实现退票的逻辑
        System.out.println("退票: " + trainID + " 日期: " + date);
    }

    // ===== Part 4: 路线查询子系统 =====

    /**
     * 查找所有路线
     */
    public static void findAllRoute(StationID departureID, StationID arrivalID) {
        // TODO: 实现查找所有路线的逻辑
        System.out.println("查找从 " + departureID + " 到 " + arrivalID + " 的所有路线");
    }

    /**
     * 查找最佳路线
     */
    public static void findBestRoute(StationID departureID, StationID arrivalID, int preference) {
        // TODO: 实现查找最佳路线的逻辑
        System.out.println("查找从 " + departureID + " 到 " + arrivalID + " 的最佳路线");
    }

    // ===== Part 5: 用户管理子系统 =====

    /**
     * 登录
     */
    public static void login(UserID userID, String password) {
        UserInfo user = userManager.findUser(userID);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            System.out.println("登录成功: " + user.getUsername());
        } else {
            System.out.println("登录失败: 用户名或密码错误");
        }
    }

    /**
     * 登出
     */
    public static void logout() {
        System.out.println("用户 " + currentUser.getUsername() + " 已登出");
        // 恢复为管理员账户
        currentUser = userManager.findUser(new UserID(0));
    }

    /**
     * 添加用户
     */
    public static void addUser(UserID userID, String username, String password) {
        // 检查权限
        if (currentUser.getPrivilege() < Config.ADMIN_PRIVILEGE) {
            System.out.println("权限不足");
            return;
        }

        if (userManager.existUser(userID)) {
            System.out.println("用户已存在");
            return;
        }

        userManager.insertUser(userID, username, password, 1); // 默认普通用户权限为1
        System.out.println("添加用户成功: " + username);
    }

    /**
     * 根据用户ID查找用户信息
     */
    public static void findUserInfoByUserID(UserID userID) {
        UserInfo user = userManager.findUser(userID);
        if (user != null) {
            System.out.println("用户信息: ID=" + userID + ", 用户名=" + user.getUsername() +
                    ", 权限=" + user.getPrivilege());
        } else {
            System.out.println("用户不存在");
        }
    }

    /**
     * 修改用户密码
     */
    public static void modifyUserPassword(UserID userID, String password) {
        // 检查权限（管理员或用户本人）
        if (currentUser.getPrivilege() < Config.ADMIN_PRIVILEGE &&
                !currentUser.getUserID().equals(userID)) {
            System.out.println("权限不足");
            return;
        }

        userManager.modifyUserPassword(userID, password);
        System.out.println("修改密码成功");
    }

    /**
     * 修改用户权限
     */
    public static void modifyUserPrivilege(UserID userID, int newPrivilege) {
        // 检查权限
            if (currentUser.getPrivilege() < Config.ADMIN_PRIVILEGE) {
            System.out.println("权限不足");
            return;
        }

        userManager.modifyUserPrivilege(userID, newPrivilege);
        System.out.println("修改权限成功");
    }
}
