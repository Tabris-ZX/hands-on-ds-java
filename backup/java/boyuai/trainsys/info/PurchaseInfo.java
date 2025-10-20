package boyuai.trainsys.info;

import boyuai.trainsys.util.Date;
import boyuai.trainsys.util.Types.*;
import lombok.Getter;
import lombok.Setter;


/**
 * 购票信息类
 */
@Getter
@Setter
public class PurchaseInfo implements Comparable<PurchaseInfo> {
    // Getters and Setters
    private UserID userID;
    private TrainID trainID;
    private Date date;
    private StationID departureStation;
    private int type;  // 1表示购票，-1表示退票

    /**
     * 默认构造函数
     */
    public PurchaseInfo() {
    }

    /**
     * 构造函数
     * @param userID 用户ID
     * @param trainID 火车ID
     * @param date 日期
     * @param departureStation 出发站
     * @param type 类型（1表示购票，-1表示退票）
     */
    public PurchaseInfo(UserID userID, TrainID trainID, Date date, StationID departureStation, int type) {
        this.userID = userID;
        this.trainID = trainID;
        this.date = date;
        this.departureStation = departureStation;
        this.type = type;
    }

    /**
     * 是否为购票
     * @return 如果是购票返回true
     */
    public boolean isOrdering() {
        return type >= 0;
    }

    /**
     * 是否为退票
     * @return 如果是退票返回true
     */
    public boolean isRefunding() {
        return type < 0;
    }

    @Override
    public int compareTo(PurchaseInfo other) {
        // 按用户ID比较
        int cmp = Long.compare(this.userID.value(), other.userID.value());
        if (cmp != 0) return cmp;

        // 按火车ID比较
        cmp = this.trainID.compareTo(other.trainID);
        if (cmp != 0) return cmp;

        // 按日期比较
        cmp = this.date.compareTo(other.date);
        if (cmp != 0) return cmp;

        // 按出发站比较
        cmp = Integer.compare(this.departureStation.value(), other.departureStation.value());
        if (cmp != 0) return cmp;

        // 按类型比较
        return Integer.compare(this.type, other.type);
    }

}
