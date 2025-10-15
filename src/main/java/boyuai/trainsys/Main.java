package boyuai.trainsys;

import boyuai.trainsys.core.TrainSystem;
import boyuai.trainsys.util.Types.*;
import boyuai.trainsys.util.Date;
import java.util.Scanner;

/**
 * 主程序入口
 */
public class Main {

    public static void main(String[] args) {
        // 初始化系统
        TrainSystem.init();

        Scanner scanner = new Scanner(System.in);
        String command;

        System.out.println("火车票务管理系统已启动");
        System.out.println("输入 'help' 查看可用命令，输入 'exit' 退出系统");

        // 读入指令
        while (true) {
            System.out.print("> ");
            command = scanner.nextLine().trim();

            System.out.println("执行命令: " + command);

            if (parseCommand(command) == 1) { // 读入到exit指令
                break;
            }
        }

        // 关闭系统
        TrainSystem.shutdown();
        scanner.close();
        System.out.println("系统已关闭");
    }

    /**
     * 解析并执行命令
     * @param command 命令字符串
     * @return 如果是退出命令返回1，否则返回0
     */
    private static int parseCommand(String command) {
        if (command.isEmpty()) {
            return 0;
        }

        String[] parts = command.split("\\s+");
        String cmd = parts[0].toLowerCase();

        try {
            switch (cmd) {
                case "exit":
                    return 1;

                case "help":
                    printHelp();
                    break;

                case "login":
                    if (parts.length >= 3) {
                        long userID = Long.parseLong(parts[1]);
                        String password = parts[2];
                        TrainSystem.login(new UserID(userID), password);
                    } else {
                        System.out.println("用法: login <用户ID> <密码>");
                    }
                    break;

                case "logout":
                    TrainSystem.logout();
                    break;

                case "adduser":
                    if (parts.length >= 4) {
                        long userID = Long.parseLong(parts[1]);
                        String username = parts[2];
                        String password = parts[3];
                        TrainSystem.addUser(new UserID(userID), username, password);
                    } else {
                        System.out.println("用法: adduser <用户ID> <用户名> <密码>");
                    }
                    break;

                case "finduser":
                    if (parts.length >= 2) {
                        long userID = Long.parseLong(parts[1]);
                        TrainSystem.findUserInfoByUserID(new UserID(userID));
                    } else {
                        System.out.println("用法: finduser <用户ID>");
                    }
                    break;

                case "modifypassword":
                    if (parts.length >= 3) {
                        long userID = Long.parseLong(parts[1]);
                        String newPassword = parts[2];
                        TrainSystem.modifyUserPassword(new UserID(userID), newPassword);
                    } else {
                        System.out.println("用法: modifypassword <用户ID> <新密码>");
                    }
                    break;

                case "modifyprivilege":
                    if (parts.length >= 3) {
                        long userID = Long.parseLong(parts[1]);
                        int newPrivilege = Integer.parseInt(parts[2]);
                        TrainSystem.modifyUserPrivilege(new UserID(userID), newPrivilege);
                    } else {
                        System.out.println("用法: modifyprivilege <用户ID> <新权限>");
                    }
                    break;

                case "querytrain":
                    if (parts.length >= 2) {
                        String trainID = parts[1];
                        TrainSystem.queryTrainScheduler(new TrainID(trainID));
                    } else {
                        System.out.println("用法: querytrain <车次号>");
                    }
                    break;

                case "queryticket":
                    if (parts.length >= 4) {
                        String trainID = parts[1];
                        String dateStr = parts[2];
                        int stationID = Integer.parseInt(parts[3]);
                        TrainSystem.queryRemainingTicket(new TrainID(trainID),
                                new Date(dateStr),
                                new StationID(stationID));
                    } else {
                        System.out.println("用法: queryticket <车次号> <日期> <出发站ID>");
                    }
                    break;

                case "mytickets":
                    TrainSystem.queryMyTicket();
                    break;

                case "order":
                    if (parts.length >= 4) {
                        String trainID = parts[1];
                        String dateStr = parts[2];
                        int stationID = Integer.parseInt(parts[3]);
                        TrainSystem.orderTicket(new TrainID(trainID),
                                new Date(dateStr),
                                new StationID(stationID));
                    } else {
                        System.out.println("用法: order <车次号> <日期> <出发站ID>");
                    }
                    break;

                case "refund":
                    if (parts.length >= 4) {
                        String trainID = parts[1];
                        String dateStr = parts[2];
                        int stationID = Integer.parseInt(parts[3]);
                        TrainSystem.refundTicket(new TrainID(trainID),
                                new Date(dateStr),
                                new StationID(stationID));
                    } else {
                        System.out.println("用法: refund <车次号> <日期> <出发站ID>");
                    }
                    break;

                case "findroute":
                    if (parts.length >= 3) {
                        int departureID = Integer.parseInt(parts[1]);
                        int arrivalID = Integer.parseInt(parts[2]);
                        TrainSystem.findAllRoute(new StationID(departureID),
                                new StationID(arrivalID));
                    } else {
                        System.out.println("用法: findroute <出发站ID> <到达站ID>");
                    }
                    break;

                case "bestroute":
                    if (parts.length >= 4) {
                        int departureID = Integer.parseInt(parts[1]);
                        int arrivalID = Integer.parseInt(parts[2]);
                        int preference = Integer.parseInt(parts[3]);
                        TrainSystem.findBestRoute(new StationID(departureID),
                                new StationID(arrivalID),
                                preference);
                    } else {
                        System.out.println("用法: bestroute <出发站ID> <到达站ID> <偏好>");
                    }
                    break;

                default:
                    System.out.println("未知命令: " + cmd);
                    System.out.println("输入 'help' 查看可用命令");
                    break;
            }
        } catch (NumberFormatException e) {
            System.out.println("参数格式错误: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("命令执行出错: " + e.getMessage());
        }

        return 0;
    }

    /**
     * 打印帮助信息
     */
    private static void printHelp() {
        System.out.println("可用命令:");
        System.out.println("  用户管理:");
        System.out.println("    login <用户ID> <密码>           - 登录");
        System.out.println("    logout                          - 登出");
        System.out.println("    adduser <用户ID> <用户名> <密码> - 添加用户（需要管理员权限）");
        System.out.println("    finduser <用户ID>               - 查找用户信息");
        System.out.println("    modifypassword <用户ID> <新密码> - 修改密码");
        System.out.println("    modifyprivilege <用户ID> <权限>  - 修改权限（需要管理员权限）");
        System.out.println();
        System.out.println("  列车查询:");
        System.out.println("    querytrain <车次号>              - 查询列车时刻表");
        System.out.println();
        System.out.println("  车票操作:");
        System.out.println("    queryticket <车次> <日期> <站ID> - 查询余票");
        System.out.println("    mytickets                       - 查询我的车票");
        System.out.println("    order <车次> <日期> <出发站ID>   - 订票");
        System.out.println("    refund <车次> <日期> <出发站ID>  - 退票");
        System.out.println();
        System.out.println("  路线查询:");
        System.out.println("    findroute <出发站ID> <到达站ID>  - 查找所有路线");
        System.out.println("    bestroute <出发站> <到达站> <偏好> - 查找最佳路线");
        System.out.println();
        System.out.println("  系统:");
        System.out.println("    help                            - 显示帮助");
        System.out.println("    exit                            - 退出系统");
    }
}
