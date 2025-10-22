package boyuai.trainsys.manager;

import boyuai.trainsys.info.TripInfo;
import boyuai.trainsys.datastructure.BPlusTree;
import boyuai.trainsys.datastructure.SeqList;

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
        this.tripInfo = new BPlusTree<>(filename);
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

}
