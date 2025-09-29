package boyuai.trainsys.util;


import boyuai.trainsys.core.RailwayGraph;
import boyuai.trainsys.info.UserInfo;
import boyuai.trainsys.manager.*;


import java.util.*;

/**
 * 命令解析器
 * 把用户输入的命令行解析成参数，然后调用对应的系统方法
 */
public class CommandParser {

    // 当前用户和系统管理器（在 C++ 里是 extern，这里可以用单例或依赖注入）
    private static UserInfo currentUser;
    private static UserManager userManager;
    private static RailwayGraph railwayGraph;
    private static SchedulerManager schedulerManager;
    private static TicketManager ticketManager;
    private static PrioritizedWaitingList waitingList;
    private static TripManager tripManager;
    private static StationManager stationManager;

    // 存放参数，例如 argMap.get("u") 对应 -u 后面的值
    private static final Map<String, String> argMap = new HashMap<>();

    /**
     * 判断字符是否字符串结尾
     */
    private static boolean isStringEnding(char ch) {
        return ch == '\0' || ch == '\n' || ch == '\r';
    }

    /**
     * 按分隔符切分字符串
     */
    private static List<String> splitTokens(String command, char separator) {
        List<String> tokens = new ArrayList<>();
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < command.length(); i++) {
            char c = command.charAt(i);
            if (c == separator) {
                tokens.add(token.toString());
                token.setLength(0);
            } else {
                token.append(c);
            }
        }
        if (token.length() > 0) {
            tokens.add(token.toString());
        }
        return tokens;
    }

    /**
     * 字符串转数字
     */
    private static long stringToNumber(String str) {
        return Long.parseLong(str);
    }

    /**
     * 解析并执行命令
     * @param command 用户输入的命令
     * @return exitCode = 1 表示退出，0 表示继续，-1 表示错误
     */
    public static int parseCommand(String command) {
        int exitCode = 0;

        List<String> tokens = splitTokens(command, ' ');
        argMap.clear();

        for (int i = 1; i < tokens.size(); i += 2) {
            if (tokens.get(i).startsWith("-")) {
                argMap.put(tokens.get(i).substring(1), tokens.get(i + 1));
            } else {
                exitCode = -1;
            }
        }

        String commandName = tokens.get(0);

        if (exitCode != -1) {
            switch (commandName) {
                case "register" -> userManager.addUser(
                        stringToNumber(argMap.get("i")),
                        argMap.get("u"),
                        argMap.get("p")
                );
                case "login" -> userManager.login(
                        stringToNumber(argMap.get("i")),
                        argMap.get("p")
                );
                case "logout" -> userManager.logout();
                case "modify_password" -> userManager.modifyUserPassword(
                        stringToNumber(argMap.get("i")),
                        argMap.get("p")
                );
                case "modify_privilege" -> userManager.modifyUserPrivilege(
                        stringToNumber(argMap.get("i")),
                        stringToNumber(argMap.get("g"))
                );
                case "query_profile" -> userManager.findUserInfoByUserID(
                        stringToNumber(argMap.get("i"))
                );
                case "add_train" -> {
                    List<String> stationsString = splitTokens(argMap.get("s"), '|');
                    List<String> pricesString = splitTokens(argMap.get("p"), '|');
                    List<String> durationsString = splitTokens(argMap.get("t"), '|');

                    StationID[] stations = stationsString.stream()
                            .map(stationManager::getStationID)
                            .toArray(StationID[]::new);

                    int[] prices = pricesString.stream()
                            .mapToInt(s -> (int) stringToNumber(s))
                            .toArray();

                    int[] durations = durationsString.stream()
                            .mapToInt(s -> (int) stringToNumber(s))
                            .toArray();

                    schedulerManager.addTrainScheduler(
                            new FixedString(argMap.get("i")), // TrainID
                            (int) stringToNumber(argMap.get("m")),
                            (int) stringToNumber(argMap.get("n")),
                            stations,
                            durations,
                            prices
                    );
                }
                case "query_train" -> schedulerManager.queryTrainScheduler(
                        new FixedString(argMap.get("i"))
                );
                case "release_ticket" -> ticketManager.releaseTicket(
                        schedulerManager.getScheduler(new FixedString(argMap.get("i"))),
                        new Date(argMap.get("d"))
                );
                case "expire_ticket" -> ticketManager.expireTicket(
                        new FixedString(argMap.get("i")),
                        new Date(argMap.get("d"))
                );
                case "display_route" -> railwayGraph.findAllRoute(
                        stationManager.getStationID(argMap.get("s")),
                        stationManager.getStationID(argMap.get("t"))
                );
                case "query_best_path" -> {
                    int preference = switch (argMap.get("p")) {
                        case "time" -> 1;
                        case "price" -> 0;
                        default -> -1;
                    };
                    if (preference != -1) {
                        railwayGraph.findBestRoute(
                                stationManager.getStationID(argMap.get("s")),
                                stationManager.getStationID(argMap.get("t")),
                                preference
                        );
                    } else {
                        System.out.println("Invalid preference: " + argMap.get("p"));
                    }
                }
                case "query_remaining" -> {
                    int remaining = ticketManager.queryRemainingTicket(
                            new FixedString(argMap.get("i")),
                            new Date(argMap.get("d")),
                            stationManager.getStationID(argMap.get("f"))
                    );
                    System.out.println("Remaining ticket: " + remaining);
                }
                case "buy_ticket" -> ticketManager.orderTicket(
                        new FixedString(argMap.get("i")),
                        new Date(argMap.get("d")),
                        stationManager.getStationID(argMap.get("f"))
                );
                case "query_order" -> ticketManager.queryMyTicket();
                case "refund_ticket" -> ticketManager.refundTicket(
                        new FixedString(argMap.get("i")),
                        new Date(argMap.get("d")),
                        stationManager.getStationID(argMap.get("f"))
                );
                case "query_accessibility" -> {
                    boolean result = railwayGraph.checkStationAccessibility(
                            stationManager.getStationID(argMap.get("s")),
                            stationManager.getStationID(argMap.get("t"))
                    );
                    System.out.println(result ? "Accessible." : "Not accessible.");
                }
                case "exit" -> exitCode = 1;
                default -> {
                    System.out.println("Invalid command: " + commandName);
                    exitCode = -1;
                }
            }
        }

        return exitCode;
    }
}
