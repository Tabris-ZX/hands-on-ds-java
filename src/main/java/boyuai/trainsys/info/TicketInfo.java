package boyuai.trainsys.info;

import boyuai.trainsys.util.Date;
import boyuai.trainsys.util.Types.TrainID;
import boyuai.trainsys.util.Types.StationID;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * 车票信息类
 */
@Setter
@Getter
public class TicketInfo implements Comparable<TicketInfo> {
    private TrainID trainID;
    private StationID departureStation;
    private StationID arrivalStation;
    private int seatNum;
    private int price;
    private int duration;
    private Date date;

    /**
     * 默认构造函数
     */
    public TicketInfo() {
    }

    /**
     * 构造函数
     */
    public TicketInfo(TrainID trainID, StationID departureStation, StationID arrivalStation,
                      int seatNum, int price, int duration, Date date) {
        this.trainID = trainID;
        this.departureStation = departureStation;
        this.arrivalStation = arrivalStation;
        this.seatNum = seatNum;
        this.price = price;
        this.duration = duration;
        this.date = date;
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
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainID, departureStation, arrivalStation, seatNum, price, duration, date);
    }

    @Override
    public int compareTo(TicketInfo other) {
        int cmp = this.trainID.compareTo(other.trainID);
        if (cmp != 0) return cmp;

        cmp = Integer.compare(this.departureStation.value(), other.departureStation.value());
        if (cmp != 0) return cmp;

        cmp = Integer.compare(this.arrivalStation.value(), other.arrivalStation.value());
        if (cmp != 0) return cmp;

        cmp = this.date.compareTo(other.date);
        if (cmp != 0) return cmp;

        cmp = Integer.compare(this.seatNum, other.seatNum);
        if (cmp != 0) return cmp;

        cmp = Integer.compare(this.price, other.price);
        if (cmp != 0) return cmp;

        return Integer.compare(this.duration, other.duration);
    }

    public void setTrainID(TrainID trainID) {
        this.trainID = trainID;
    }

    public void setDepartureStation(StationID departureStation) {
        this.departureStation = departureStation;
    }

    public void setArrivalStation(StationID arrivalStation) {
        this.arrivalStation = arrivalStation;
    }

    public void setSeatNum(int seatNum) {
        this.seatNum = seatNum;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "TrainID: " + trainID + "\n" +
                "Departure Station: " + departureStation + "\n" +
                "Arrival Station: " + arrivalStation + "\n" +
                "Seat Number: " + seatNum + "\n" +
                "Price: " + price + "\n" +
                "Duration: " + duration + "\n" +
                "Date: " + date;
    }

}
