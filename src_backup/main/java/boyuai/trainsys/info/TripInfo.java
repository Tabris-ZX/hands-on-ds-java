package boyuai.trainsys.info;

import boyuai.trainsys.util.Time;
import boyuai.trainsys.util.Types.TrainID;
import boyuai.trainsys.util.Types.StationID;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * 行程信息类
 * 包含出发时间和到达时间的完整行程信息
 */
@Setter
@Getter
public class TripInfo implements Comparable<TripInfo> {
    private TrainID trainID;
    private StationID departureStation;
    private StationID arrivalStation;
    private int ticketNumber;  // 票数（正数为购票，负数为退票）
    private int duration;      // 行程时长（分钟）
    private int price;         // 票价
    private Time departureTime; // 出发时间
    private Time arrivalTime;   // 到达时间

    /**
     * 默认构造函数
     */
    public TripInfo() {
    }

    /**
     * 构造函数
     */
    public TripInfo(TrainID trainID, StationID departureStation, StationID arrivalStation,
                    int ticketNumber, int duration, int price, Time departureTime) {
        this.trainID = trainID;
        this.departureStation = departureStation;
        this.arrivalStation = arrivalStation;
        this.ticketNumber = ticketNumber;
        this.duration = duration;
        this.price = price;
        this.departureTime = departureTime;
        // 自动计算到达时间
        this.arrivalTime = departureTime.addMinutes(duration);
    }
    
    /**
     * 完整构造函数（包含到达时间）
     */
    public TripInfo(TrainID trainID, StationID departureStation, StationID arrivalStation,
                    int ticketNumber, int duration, int price, Time departureTime, Time arrivalTime) {
        this.trainID = trainID;
        this.departureStation = departureStation;
        this.arrivalStation = arrivalStation;
        this.ticketNumber = ticketNumber;
        this.duration = duration;
        this.price = price;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    public int getType() {
        return ticketNumber;
    }
    public void setType(int type) {
        this.ticketNumber = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TripInfo tripInfo = (TripInfo) obj;
        return ticketNumber == tripInfo.ticketNumber &&
                duration == tripInfo.duration &&
                price == tripInfo.price &&
                Objects.equals(trainID, tripInfo.trainID) &&
                Objects.equals(departureStation, tripInfo.departureStation) &&
                Objects.equals(arrivalStation, tripInfo.arrivalStation) &&
                Objects.equals(departureTime, tripInfo.departureTime) &&
                Objects.equals(arrivalTime, tripInfo.arrivalTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainID, departureStation, arrivalStation, ticketNumber, duration, price, departureTime, arrivalTime);
    }

    @Override
    public int compareTo(TripInfo other) {
        int cmp = this.trainID.compareTo(other.trainID);
        if (cmp != 0) return cmp;

        cmp = Integer.compare(this.departureStation.value(), other.departureStation.value());
        if (cmp != 0) return cmp;

        cmp = Integer.compare(this.arrivalStation.value(), other.arrivalStation.value());
        if (cmp != 0) return cmp;

        cmp = this.departureTime.compareTo(other.departureTime);
        if (cmp != 0) return cmp;

        cmp = Integer.compare(this.ticketNumber, other.ticketNumber);
        if (cmp != 0) return cmp;

        cmp = Integer.compare(this.price, other.price);
        if (cmp != 0) return cmp;

        return Integer.compare(this.duration, other.duration);
    }

    @Override
    public String toString() {
        return "TripInfo:\n" +
                "trainID: " + trainID + "\n" +
                "departureStation: " + departureStation + "\n" +
                "arrivalStation: " + arrivalStation + "\n" +
                "departureTime: " + departureTime + "\n" +
                "arrivalTime: " + arrivalTime + "\n" +
                "duration: " + duration + " minutes\n" +
                "price: " + price;
    }
    
    // 兼容旧代码的方法
    @Deprecated
    public Time getDate() {
        return departureTime;
    }
    
    @Deprecated
    public void setDate(Time time) {
        this.departureTime = time;
        if (duration > 0) {
            this.arrivalTime = time.addMinutes(duration);
        }
    }
}
