package boyuai.trainsys.manager;

import boyuai.trainsys.info.TripInfo;
import boyuai.trainsys.datastructure.BPlusTree;
import boyuai.trainsys.datastructure.SeqList;
import boyuai.trainsys.util.Types.*;

/**
 * 行程管理器
 * 负责管理用户的购票记录和行程信息
 */
public class TripManager {

    // 数据成员：一个从 UserID 到 [一组 TripInfo]，展现此用户购买了什么票
    private BPlusTree<Long, TripInfo> tripInfo;

    /**
     * 构造函数
     * @param filename 数据文件名
     */
    public TripManager(String filename) {
        this.tripInfo = new BPlusTree<>(filename, Long.class, TripInfo.class);
    }

    /**
     * 添加行程记录
     * @param userID 用户ID
     * @param trip 行程信息
     */
    public void addTrip(long userID, TripInfo trip) {
        tripInfo.insert(userID, trip);
    }

    /**
     * 查询用户的所有行程
     * @param userID 用户ID
     * @return 用户的多个 TripInfo
     */
    public SeqList<TripInfo> queryTrip(long userID) {
        SeqList<TripInfo> trips = tripInfo.find(userID);
        if (trips == null) {
            return new SeqList<>();
        }
        return trips;
    }

    /**
     * 删除用户的某个行程记录
     * @param userID 用户ID
     * @param trip 要删除的行程信息
     */
    public void removeTrip(long userID, TripInfo trip) {
        tripInfo.remove(userID, trip);
    }

    /**
     * 查询用户是否有特定的行程
     * @param userID 用户ID
     * @param trip 要查询的行程信息
     * @return 是否存在该行程
     */
    public boolean hasTrip(long userID, TripInfo trip) {
        SeqList<TripInfo> trips = queryTrip(userID);
        for (int i = 0; i < trips.length(); i++) {
            if (trips.visit(i).equals(trip)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取用户的行程数量
     * @param userID 用户ID
     * @return 行程数量
     */
    public int getTripCount(long userID) {
        SeqList<TripInfo> trips = queryTrip(userID);
        return trips.length();
    }

    /**
     * 清除用户的所有行程记录
     * @param userID 用户ID
     */
    public void clearUserTrips(long userID) {
        SeqList<TripInfo> trips = queryTrip(userID);
        for (int i = 0; i < trips.length(); i++) {
            tripInfo.remove(userID, trips.visit(i));
        }
    }

    /**
     * 打印用户的所有行程信息
     * @param userID 用户ID
     */
    public void printUserTrips(long userID) {
        SeqList<TripInfo> trips = queryTrip(userID);

        if (trips.length() == 0) {
            System.out.println("No trips found for user " + userID);
            return;
        }

        System.out.println("User " + userID + " trips:");
        System.out.println("=====================================");

        for (int i = 0; i < trips.length(); i++) {
            System.out.println("Trip " + (i + 1) + ":");
            System.out.println(trips.visit(i));
            System.out.println("-------------------------------------");
        }

        System.out.println("Total trips: " + trips.length());
    }

    /**
     * 更新行程信息
     * @param userID 用户ID
     * @param oldTrip 旧的行程信息
     * @param newTrip 新的行程信息
     */
    public void updateTrip(long userID, TripInfo oldTrip, TripInfo newTrip) {
        // 先删除旧的，再添加新的
        removeTrip(userID, oldTrip);
        addTrip(userID, newTrip);
    }

    /**
     * 获取所有用户的行程统计信息
     * @return 统计信息字符串
     */
    public String getStatistics() {
        // 这里需要遍历B+树获取统计信息
        // 具体实现依赖于BPlusTree的遍历接口
        StringBuilder stats = new StringBuilder();
        stats.append("Trip Statistics:\n");
        stats.append("================\n");
        // 添加更多统计信息...
        return stats.toString();
    }

    /**
     * 保存数据到文件
     */
    public void save() {
        // B+树应该自动持久化到文件
    }

    /**
     * 从文件加载数据
     */
    public void load() {
        // B+树应该在构造时自动从文件加载
    }
}
