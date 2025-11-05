package boyuai.trainsys.info;

import boyuai.trainsys.util.Types.TrainID;
import boyuai.trainsys.util.Types.StationID;
import lombok.Getter;
import lombok.Setter;

/**
 * 路线段信息类
 */
@Setter
@Getter
public class RouteSectionInfo {
    private TrainID trainID;
    private StationID arrivalStation;
    private int price;
    private int duration;

    /**
     * 默认构造函数
     */
    public RouteSectionInfo() {
    }

    /**
     * 构造函数
     * @param trainID 火车ID
     * @param arrivalStation 到达站
     * @param price 价格
     * @param duration 时长
     */
    public RouteSectionInfo(TrainID trainID, StationID arrivalStation, int price, int duration) {
        this.trainID = trainID;
        this.arrivalStation = arrivalStation;
        this.price = price;
        this.duration = duration;
    }

    /**
     * 复制构造函数
     * @param other 要复制的RouteSectionInfo对象
     */
    public RouteSectionInfo(RouteSectionInfo other) {
        this.trainID = new TrainID(other.trainID);
        this.arrivalStation = new StationID(other.arrivalStation.value());
        this.price = other.price;
        this.duration = other.duration;
    }
}
