package boyuai.trainsys;

import boyuai.trainsys.core.TrainSystem;
import boyuai.trainsys.util.CommandParser;

import java.io.File;
import java.util.Scanner;

/**
 * 主程序入口
 */
public class Main {

    public static void main(String[] args) {
        // 初始化系统（实例化）
        System.out.println("当前工作目录: " + new File("").getAbsolutePath());

        TrainSystem system = new TrainSystem();
        CommandParser parser = new CommandParser(system);

        Scanner scanner = new Scanner(System.in);
        String command;

        System.out.println("火车票务管理系统已启动");
        System.out.println("输入 'help' 查看可用命令，输入 'exit' 退出系统");

        // 读入指令
        while (true) {
            System.out.print("> ");
            command = scanner.nextLine().trim();

            System.out.println("执行命令: " + command);
            if (command.equals("help")) {
                printHelp();
                continue;
            }

            if (parser.parseCommand(command) == 1) { // 读入到exit指令
                break;
            }
        }

        // 关闭系统
        scanner.close();
        System.out.println("系统已关闭");
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
