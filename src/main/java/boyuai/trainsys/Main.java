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

        TrainSystem system = null;
        try {
            system = new TrainSystem();
        } catch (java.sql.SQLException e) {
            System.out.println("数据库初始化失败！");
            e.printStackTrace();
            System.exit(1);
        }
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
        System.out.println("可用命令 (需使用短参 -x value 形式):");
        System.out.println("  用户管理:");
        System.out.println("    register -i <用户ID> -u <用户名> -p <密码>   - 注册用户");
        System.out.println("    login    -i <用户ID> -p <密码>             - 登录");
        System.out.println("    logout                                        - 登出");
        System.out.println("    modify_password   -i <用户ID> -p <新密码>     - 修改密码");
        System.out.println("    modify_privilege  -i <用户ID> -g <权限>       - 修改权限");
        System.out.println("    query_profile     -i <用户ID>                 - 查看用户资料");
        System.out.println();
        System.out.println("  运行计划(管理员):");
        System.out.println("    add_train   -i <车次ID> -m <席位数> -n <站数> -s <站1/站2/...> -t <时长1/时长2/...> -p <票价1/票价2/...>   - 添加列车");
        System.out.println("    query_train -i <车次ID>                       - 查询列车信息");
        System.out.println();
        System.out.println("  票务(管理员):");
        System.out.println("    release_ticket -i <车次ID> -d <日期>          - 发布车票");
        System.out.println("    expire_ticket  -i <车次ID> -d <日期>          - 使车票过期");
        System.out.println();
        System.out.println("  购/退票:");
        System.out.println("    query_remaining -i <车次ID> -d <日期> -f <出发站名>   - 查询余票");
        System.out.println("    buy_ticket      -i <车次ID> -d <日期> -f <出发站名>   - 购票");
        System.out.println("    query_order                                      - 查询我的订单");
        System.out.println("    refund_ticket   -i <车次ID> -d <日期> -f <出发站名>   - 退票");
        System.out.println();
        System.out.println("  路线:");
        System.out.println("    display_route    -s <起点站名> -t <终点站名>           - 显示所有可达路线");
        System.out.println("    query_best_path  -s <起点站名> -t <终点站名> -p <time or price>   - 查询最优路线");
        System.out.println("    query_accessibility -s <起点站名> -t <终点站名>        - 查询站点是否连通");
        System.out.println();
        System.out.println("  系统:");
        System.out.println("    help");
        System.out.println("    exit");
    }
}
