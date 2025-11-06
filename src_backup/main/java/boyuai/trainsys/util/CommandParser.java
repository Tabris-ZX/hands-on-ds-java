package boyuai.trainsys.util;

import boyuai.trainsys.core.RailwayGraph;
import boyuai.trainsys.core.TrainSystem;
import boyuai.trainsys.info.*;
import boyuai.trainsys.manager.*;
import boyuai.trainsys.util.Types.*;
import boyuai.trainsys.datastructure.SeqList;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 命令解析器
 * 负责解析用户输入的命令并调用相应的系统功能
 */
@Slf4j
public class CommandParser {

    // 存放参数，例如 argMap.get('u') 存放的是 -u 后面的参数
    private static final Map<Character, String> argMap = new HashMap<>();

    // 外部依赖引用（仅保留实际使用的字段）
    private final RailwayGraph railwayGraph;
    private final SchedulerManager schedulerManager;
    private final StationManager stationManager;
    private final TrainSystem trainSystem;

    public CommandParser(TrainSystem trainSystem) {
        this.trainSystem = trainSystem;
        this.railwayGraph = trainSystem.getRailwayGraph();
        this.schedulerManager = trainSystem.getSchedulerManager();
        this.stationManager = trainSystem.getStationManager();
    }

    /**
     * 将字符串按照分隔符分割
     */
    private SeqList<String> splitTokens(String command, char separator) {
        SeqList<String> tokens = new SeqList<>();
        String[] parts = command.split(String.valueOf(separator));
        for (String part : parts) {
            tokens.insert(tokens.length(), part.trim());
        }
        return tokens;
    }

    /**
     * 将字符串转换为数字
     */
    private long stringToNumber(String str) {
        if (str == null || str.isEmpty()) {
            return 0;
        }
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * 解析并执行命令
     * @param command 输入的命令字符串
     * @return 0-正常，1-退出，-1-错误
     */
    public int parseCommand(String command) {
        int exitCode = 0;
        argMap.clear();

        // 分割命令
        SeqList<String> tokens = splitTokens(command, ' ');
        if (tokens.length() == 0) {
            return -1;
        }

        // 解析参数
        for (int i = 1; i < tokens.length(); i += 2) {
            String token = tokens.visit(i);
            if (token.startsWith("-") && token.length() > 1) {
                if (i + 1 < tokens.length()) {
                    argMap.put(token.charAt(1), tokens.visit(i + 1));
                }
            } else {
                exitCode = -1;
            }
        }

        String commandName = tokens.visit(0);

        if (exitCode != -1) {
            try {
                switch (commandName) {
                    case "register":
                        trainSystem.addUser(
                                stringToNumber(argMap.get('i')),
                                argMap.get('u'),
                                argMap.get('p')
                        );
                        break;

                    case "login":
                        trainSystem.login(
                                stringToNumber(argMap.get('i')),
                                argMap.get('p')
                        );
                        break;

                    case "logout":
                        trainSystem.logout();
                        break;

                    case "modify_password":
                        trainSystem.modifyUserPassword(
                                stringToNumber(argMap.get('i')),
                                argMap.get('p')
                        );
                        break;

                    case "modify_privilege":
                        trainSystem.modifyUserPrivilege(
                                stringToNumber(argMap.get('i')),
                                (int)stringToNumber(argMap.get('g'))
                        );
                        break;

                    case "query_profile":
                        trainSystem.findUserInfoByUserID(
                                stringToNumber(argMap.get('i'))
                        );
                        break;

                    case "add_train":
                        parseAddTrain();
                        break;

                    case "query_train":
                        trainSystem.queryTrainScheduler(
                                new FixedString(argMap.get('i'))
                        );
                        break;

                    case "release_ticket":
                        trainSystem.releaseTicket(
                                schedulerManager.getScheduler(new FixedString(argMap.get('i'))),
                                new Date(argMap.get('d'))
                        );
                        break;

                    case "expire_ticket":
                        trainSystem.expireTicket(
                                new FixedString(argMap.get('i')),
                                new Date(argMap.get('d'))
                        );
                        break;

                    case "display_route":
                        trainSystem.findAllRoute(
                                stationManager.getStationID(argMap.get('s')),
                                stationManager.getStationID(argMap.get('t'))
                        );
                        break;

                    case "query_best_path":
                        parseBestPath();
                        break;

                    case "query_remaining":
                        int remaining = trainSystem.queryRemainingTicket(
                                new FixedString(argMap.get('i')),
                                new Date(argMap.get('d')),
                                stationManager.getStationID(argMap.get('f'))
                        );
                        System.out.println("Remaining ticket:" + remaining);
                        break;

                    case "buy_ticket":
                        trainSystem.orderTicket(
                                new FixedString(argMap.get('i')),
                                new Date(argMap.get('d')),
                                stationManager.getStationID(argMap.get('f'))
                        );
                        break;

                    case "query_order":
                        trainSystem.queryMyTicket();
                        break;

                    case "refund_ticket":
                        trainSystem.refundTicket(
                                new FixedString(argMap.get('i')),
                                new Date(argMap.get('d')),
                                stationManager.getStationID(argMap.get('f'))
                        );
                        break;

                    case "query_accessibility":
                        boolean result = railwayGraph.checkStationAccessibility(
                                stationManager.getStationID(argMap.get('s')).value(),
                                stationManager.getStationID(argMap.get('t')).value()
                        );
                        if (result) {
                            System.out.println("Accessible.");
                        } else {
                            System.out.println("Not accessible.");
                        }
                        break;

                    case "exit":
                        exitCode = 1;
                        break;

                    default:
                        System.out.println("Invalid command: " + commandName);
                        exitCode = -1;
                        break;
                }
            } catch (Exception e) {
                log.error("执行命令时发生错误", e);
                System.out.println("Error executing command: " + e.getMessage());
                exitCode = -1;
            }
        }

        return exitCode;
    }

    /**
     * 解析添加列车命令
     */
    private void parseAddTrain() {
        SeqList<String> stationsString = splitTokens(argMap.get('s'), '/');
        SeqList<String> pricesString = splitTokens(argMap.get('p'), '/');
        SeqList<String> durationsString = splitTokens(argMap.get('t'), '/');

        int[] stations = new int[stationsString.length()];
        int[] prices = new int[pricesString.length()];
        int[] durations = new int[durationsString.length()];

        for (int i = 0; i < stationsString.length(); i++) {
            stations[i] = stationManager.getStationID(stationsString.visit(i)).value();
        }

        for (int i = 0; i < pricesString.length(); i++) {
            prices[i] = (int)stringToNumber(pricesString.visit(i));
        }

        for (int i = 0; i < durationsString.length(); i++) {
            durations[i] = (int)stringToNumber(durationsString.visit(i));
        }

        trainSystem.addTrainScheduler(
                new FixedString(argMap.get('i')),
                (int)stringToNumber(argMap.get('m')),
                (int)stringToNumber(argMap.get('n')),
                stations,
                durations,
                prices
        );
    }

    /**
     * 解析查询最佳路径命令
     */
    private void parseBestPath() {
        int preference = -1;
        String pref = argMap.get('p');

        if ("time".equals(pref)) {
            preference = 1;
        } else if ("price".equals(pref)) {
            preference = 0;
        } else {
            System.out.println("Invalid preference: " + pref);
        }

        if (preference != -1) {
            trainSystem.findBestRoute(
                    stationManager.getStationID(argMap.get('s')),
                    stationManager.getStationID(argMap.get('t')),
                    preference
            );
        }
    }
}
