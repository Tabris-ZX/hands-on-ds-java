package boyuai.trainsys.info;

import boyuai.trainsys.util.Time;
import boyuai.trainsys.util.Types.TrainID;
import boyuai.trainsys.util.Types.StationID;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * 车票信息类
 * 包含发车时间的车票信息
 */
@Setter
@Getter
public class TicketInfo implements Comparable<TicketInfo> {
    private TrainID trainID;
    private StationID departureStation;
    private StationID arrivalStation;
    private int seatNum;       // 余票数量
    private int price;         // 票价
    private int duration;      // 运行时长（分钟）
    private Time departureTime; // 出发时间

    /**
     * 默认构造函数
     */
    public TicketInfo() {
    }

    /**
     * 构造函数
     */
    public TicketInfo(TrainID trainID, StationID departureStation, StationID arrivalStation,
                      int seatNum, int price, int duration, Time departureTime) {
        this.trainID = trainID;
        this.departureStation = departureStation;
        this.arrivalStation = arrivalStation;
        this.seatNum = seatNum;
        this.price = price;
        this.duration = duration;
        this.departureTime = departureTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TicketInfo that = (TicketInfo) obj;
        return seatNum == that.seatNum &&
                price == that.price &&
                duration == that.duration &&
                Objects.equals(trainID, that.trainID) &&
                Objects.equals(departureStation, that.departureStation) &&
                Objects.equals(arrivalStation, that.arrivalStation) &&
                Objects.equals(departureTime, that.departureTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainID, departureStation, arrivalStation, seatNum, price, duration, departureTime);
    }

    @Override
    public int compareTo(TicketInfo other) {
        int cmp = this.trainID.compareTo(other.trainID);
        if (cmp != 0) return cmp;

        cmp = Integer.compare(this.departureStation.value(), other.departureStation.value());
        if (cmp != 0) return cmp;

        cmp = Integer.compare(this.arrivalStation.value(), other.arrivalStation.value());
        if (cmp != 0) return cmp;

        cmp = this.departureTime.compareTo(other.departureTime);
        if (cmp != 0) return cmp;

        cmp = Integer.compare(this.seatNum, other.seatNum);
        if (cmp != 0) return cmp;

        cmp = Integer.compare(this.price, other.price);
        if (cmp != 0) return cmp;

        return Integer.compare(this.duration, other.duration);
    }

    @Override
    public String toString() {
        Time arrivalTime = departureTime.addMinutes(duration);
        return "TrainID: " + trainID + "\n" +
                "Departure Station: " + departureStation + "\n" +
                "Arrival Station: " + arrivalStation + "\n" +
                "Departure Time: " + departureTime + "\n" +
                "Arrival Time: " + arrivalTime + "\n" +
                "Seat Number: " + seatNum + "\n" +
                "Price: " + price + "\n" +
                "Duration: " + duration + " minutes";
    }
    
    // 兼容旧代码的方法
    @Deprecated
    public Time getDate() {
        return departureTime;
    }
    
    @Deprecated
    public void setDate(Time time) {
        this.departureTime = time;
    }

}
