package boyuai.trainsys.core;

import boyuai.trainsys.util.Types.*;
import boyuai.trainsys.config.Config;
import lombok.Data;

import java.util.Objects;

/**
 * 列车时刻表类
 */
@Data
public class TrainScheduler implements Comparable<TrainScheduler> {
    // Setters

    // Getters
    private TrainID trainID;                    // 车次号
    private int seatNum;                        // 额定乘员
    private int passingStationNum;              // 途径站点数
    private StationID[] stations;               // 途径站点的数组
    private int[] duration;                     // 每一段历时的数组
    private int[] price;                        // 每一段票价的数组

    /**
     * 构造函数
     */
    public TrainScheduler() {
        this.stations = new StationID[Config.MAX_PASSING_STATION_NUMBER];
        this.duration = new int[Config.MAX_PASSING_STATION_NUMBER];
        this.price = new int[Config.MAX_PASSING_STATION_NUMBER];
        this.passingStationNum = 0;
    }

    /**
     * 添加站点
     * @param station 要添加的站点
     */
    public void addStation(StationID station) {
        if (passingStationNum < Config.MAX_PASSING_STATION_NUMBER) {
            stations[passingStationNum++] = station;
        }
    }

    public void insertStation(int i, StationID station) {
        for (int j = passingStationNum; j > i; j--) {
            stations[j] = stations[j - 1];
        }
        stations[i] = station;
        passingStationNum++;
    }

    public void removeStation(int i) {
        for (int j = i; j < passingStationNum - 1; j++) {
            stations[j] = stations[j + 1];
        }
        passingStationNum--;
    }

    public int findStation(StationID stationID) {
        for (int i = 0; i + 1 < passingStationNum; i++) {
            if (stations[i].equals(stationID)) return i;
        }
        return -1;
    }

    public void setPrice(int[] price) {
        for (int i = 0; i < price.length; i++) {
            this.price[i] = price[i];
        }
    }

    public void setDuration(int[] duration) {
        for (int i = 0; i < duration.length; i++) {
            this.duration[i] = duration[i];
        }
    }

    public void setTrainID(TrainID id) {
        this.trainID = id;
    }

    public void setSeatNumber(int seatNum) {
        this.seatNum = seatNum;
    }

    public void getStations(StationID[] stations) {
        for (int i = 0; i < passingStationNum; i++) {
            stations[i] = this.stations[i];
        }
    }

    public void getDuration(int[] duration) {
        for (int i = 0; i + 1 < passingStationNum; i++) {
            duration[i] = this.duration[i];
        }
    }

    public void getPrice(int[] price) {
        for (int i = 0; i + 1 < passingStationNum; i++) {
            price[i] = this.price[i];
        }
    }

    public StationID getStation(int i) {
        return stations[i];
    }

    public int getDuration(int i) {
        return duration[i];
    }

    public int getPrice(int i) {
        return price[i];
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TrainScheduler that = (TrainScheduler) obj;
        return Objects.equals(trainID, that.trainID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainID);
    }

    @Override
    public int compareTo(TrainScheduler other) {
        return this.trainID.compareTo(other.trainID);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TrainID: ").append(trainID).append("\n");
        sb.append("SeatNum: ").append(seatNum).append("\n");
        sb.append("PassingStationNum: ").append(passingStationNum).append("\n");
        sb.append("Stations: ");
        for (int i = 0; i < passingStationNum; i++) {
            sb.append(stations[i].value()).append(" ");
        }
        sb.append("\n");
        sb.append("Duration: ");
        for (int i = 0; i + 1 < passingStationNum; i++) {
            sb.append(duration[i]).append(" ");
        }
        sb.append("\n");
        sb.append("Price: ");
        for (int i = 0; i + 1 < passingStationNum; i++) {
            sb.append(price[i]).append(" ");
        }
        sb.append("\n");
        return sb.toString();
    }
}
