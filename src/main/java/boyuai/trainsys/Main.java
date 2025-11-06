package boyuai.trainsys;

import boyuai.trainsys.core.TrainSystem;
import boyuai.trainsys.util.CommandParser;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Scanner;

/**
 * 火车票务管理系统主程序入口
 * <p>
 * 提供命令行交互界面，支持：
 * <ul>
 *   <li>用户管理（注册、登录、权限管理）</li>
 *   <li>列车调度管理（添加车次、查询车次）</li>
 *   <li>票务管理（发布车票、停售车票）</li>
 *   <li>购退票操作</li>
 *   <li>路线查询（连通性、所有路线、最优路径）</li>
 * </ul>
 * 
 * @author hands-on-ds
 * @version 1.0
 * @since 1.0
 */
@Slf4j
public class Main {

    /**
     * 主程序入口
     * <p>
     * 执行流程：
     * <ol>
     *   <li>初始化火车票务管理系统</li>
     *   <li>创建命令解析器</li>
     *   <li>进入命令行交互循环</li>
     *   <li>处理用户输入的命令</li>
     *   <li>关闭系统资源</li>
     * </ol>
     * 
     * @param args 命令行参数（未使用）
     */
    public static void main(String[] args) {
        // 初始化系统（实例化）
        log.info("当前工作目录: {}", new File("").getAbsolutePath());

        TrainSystem system = null;
        try {
            system = new TrainSystem();
        } catch (java.sql.SQLException e) {
            log.error("数据库初始化失败！", e);
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
     * <p>
     * 显示系统支持的所有命令及其使用方法，包括：
     * <ul>
     *   <li>用户管理命令</li>
     *   <li>运行计划管理命令（需管理员权限）</li>
     *   <li>票务管理命令（需管理员权限）</li>
     *   <li>购退票命令</li>
     *   <li>路线查询命令</li>
     *   <li>系统命令</li>
     * </ul>
     */
    private static void printHelp() {
        String userCommands = """
                ========== 普通用户功能 ==========
                【账户操作】
                  register -i <用户ID> -u <用户名> -p <密码>
                  login -i <用户ID> -p <密码>
                  logout
                
                【购票查询】(时间格式: HH:MM_MM-DD)
                  query_remaining -i <车次> -d <时间> -f <出发站>
                  buy_ticket -i <车次> -d <时间> -f <出发站>
                  query_order
                  refund_ticket -i <车次> -d <时间> -f <出发站>
                
                【路线查询】
                  display_route -s <起点> -t <终点>
                  query_best_path -s <起点> -t <终点> -p <time|price>
                  query_accessibility -s <起点> -t <终点>
                
                【系统】
                  help
                  exit
                """;
        
        String adminCommands = """
                ========== 管理员功能 ==========
                【用户管理】
                  modify_password -i <用户ID> -p <新密码>
                  modify_privilege -i <用户ID> -g <权限值>
                  query_profile -i <用户ID>
                
                【列车调度】
                  add_train -i <车次> -m <座位> -n <站数> -s <站1/站2/...> -t <首发时间/时长1/...> -p <票价1/...>
                            注: -t 第一个值是首发时间(HH:MM)，后面是各区段时长(分钟)
                            例: -t 08:00/120/90 表示8:00发车，区段时长120和90分钟
                  query_train -i <车次>
                
                【票务管理】(时间格式: HH:MM_MM-DD，用下划线连接)
                  release_ticket -i <车次> -d <时间>
                  expire_ticket -i <车次> -d <时间>
                """;
        
        String examples = """
                ========== 使用示例 ==========
                # 用户注册和购票
                register -i 1001 -u 张三 -p 123456
                login -i 1001 -p 123456
                buy_ticket -i D2282 -d 08:00_06-15 -f 北京
                query_order
                
                # 管理员添加列车和发售车票
                login -i 0 -p admin
                add_train -i D2282 -m 1000 -n 3 -s 北京/上海/广州 -t 08:00/120/90 -p 150/120
                release_ticket -i D2282 -d 08:00_06-15
                
                注意: 时间格式 HH:MM_MM-DD (下划线连接日期，如 08:00_06-15)
                """;
        
        System.out.println(userCommands);
        System.out.println(adminCommands);
        System.out.println(examples);
    }
}
