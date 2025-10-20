package boyuai.trainsys.core;

import boyuai.trainsys.util.Types.*;
import boyuai.trainsys.config.Config;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * 列车时刻表类
 */
@Setter
@Getter
public class TrainScheduler implements Comparable<TrainScheduler> {
    // Setters

    // Getters
    private TrainID trainID;                    // 车次号
    private int seatNum;                        // 额定乘员
    private int passingStationNum;              // 途径站点数
    private final StationID[] stations;               // 途径站点的数组
    private final int[] duration;                     // 每一段历时的数组
    private final int[] price;                        // 每一段票价的数组

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

    /**
     * 在指定位置插入站点
     * @param i 位置索引
     * @param station 要插入的站点
     */
    public void insertStation(int i, StationID station) {
        if (i >= 0 && i <= passingStationNum && passingStationNum < Config.MAX_PASSING_STATION_NUMBER) {
            // 后移元素
            for (int j = passingStationNum; j > i; j--) {
                stations[j] = stations[j - 1];
                if (j > 0) {
                    duration[j] = duration[j - 1];
                    price[j] = price[j - 1];
                }
            }
            stations[i] = station;
            passingStationNum++;
        }
    }

    /**
     * 移除指定位置的站点
     * @param i 位置索引
     */
    public void removeStation(int i) {
        if (i >= 0 && i < passingStationNum) {
            // 前移元素
            for (int j = i; j < passingStationNum - 1; j++) {
                stations[j] = stations[j + 1];
                duration[j] = duration[j + 1];
                price[j] = price[j + 1];
            }
            passingStationNum--;
        }
    }

    /**
     * 查找站点位置
     * @param station 要查找的站点
     * @return 站点位置，未找到返回-1
     */
    public int findStation(StationID station) {
        for (int i = 0; i < passingStationNum; i++) {
            if (stations[i].equals(station)) {
                return i;
            }
        }
        return -1;
    }

    public void setPrice(int[] price) {
        System.arraycopy(price, 0, this.price, 0,
                Math.min(price.length, Config.MAX_PASSING_STATION_NUMBER));
    }

    public void setDuration(int[] duration) {
        System.arraycopy(duration, 0, this.duration, 0,
                Math.min(duration.length, Config.MAX_PASSING_STATION_NUMBER));
    }

    /**
     * 获取所有站点
     * @param stationsOut 输出数组
     */
    public void getStations(StationID[] stationsOut) {
        System.arraycopy(stations, 0, stationsOut, 0, passingStationNum);
    }

    /**
     * 获取所有时长
     * @param durationOut 输出数组
     */
    public void getDuration(int[] durationOut) {
        System.arraycopy(duration, 0, durationOut, 0, passingStationNum);
    }

    /**
     * 获取所有价格
     * @param priceOut 输出数组
     */
    public void getPrice(int[] priceOut) {
        System.arraycopy(price, 0, priceOut, 0, passingStationNum);
    }

    /**
     * 获取指定位置的站点
     * @param i 位置索引
     * @return 站点ID
     */
    public StationID getStation(int i) {
        if (i >= 0 && i < passingStationNum) {
            return stations[i];
        }
        return null;
    }

    /**
     * 获取指定位置的时长
     * @param i 位置索引
     * @return 时长
     */
    public int getDuration(int i) {
        if (i >= 0 && i < passingStationNum) {
            return duration[i];
        }
        return 0;
    }

    /**
     * 获取指定位置的价格
     * @param i 位置索引
     * @return 价格
     */
    public int getPrice(int i) {
        if (i >= 0 && i < passingStationNum) {
            return price[i];
        }
        return 0;
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
        sb.append("TrainScheduler:\n");
        sb.append("trainID: ").append(trainID).append("\n");
        sb.append("seatNum: ").append(seatNum).append("\n");
        sb.append("passingStationNum: ").append(passingStationNum).append("\n");
        sb.append("stations: ");
        for (int i = 0; i < passingStationNum; i++) {
            sb.append(stations[i]).append(" ");
        }
        sb.append("\n");
        return sb.toString();
    }
}
