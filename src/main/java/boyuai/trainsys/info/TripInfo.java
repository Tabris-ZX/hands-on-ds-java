package boyuai.trainsys.info;

import boyuai.trainsys.util.Date;
import boyuai.trainsys.util.Types.TrainID;
import boyuai.trainsys.util.Types.StationID;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * 行程信息类
 */
@Setter
@Getter
public class TripInfo implements Comparable<TripInfo> {
    private TrainID trainID;
    private StationID departureStation;
    private StationID arrivalStation;
    private int ticketNumber;
    private int duration;
    private int price;
    private Date date;

    /**
     * 默认构造函数
     */
    public TripInfo() {
    }

    /**
     * 构造函数
     */
    public TripInfo(TrainID trainID, StationID departureStation, StationID arrivalStation,
                    int ticketNumber, int duration, int price, Date date) {
        this.trainID = trainID;
        this.departureStation = departureStation;
        this.arrivalStation = arrivalStation;
        this.ticketNumber = ticketNumber;
        this.duration = duration;
        this.price = price;
        this.date = date;
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
                Objects.equals(date, tripInfo.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainID, departureStation, arrivalStation, ticketNumber, duration, price, date);
    }

    @Override
    public int compareTo(TripInfo other) {
        int cmp = this.trainID.compareTo(other.trainID);
        if (cmp != 0) return cmp;

        cmp = Integer.compare(this.departureStation.value(), other.departureStation.value());
        if (cmp != 0) return cmp;

        cmp = Integer.compare(this.arrivalStation.value(), other.arrivalStation.value());
        if (cmp != 0) return cmp;

        cmp = this.date.compareTo(other.date);
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
                "departureStation: " + departureStation + "\n" +  // 简化版，不包含站点名称转换
                "arrivalStation: " + arrivalStation + "\n" +
                "duration: " + duration + "\n" +
                "price: " + price + "\n" +
                "date: " + date;
    }
}
