package boyuai.trainsys.core;

import boyuai.trainsys.util.Time;
import boyuai.trainsys.util.Types.*;
import boyuai.trainsys.config.Config;
import lombok.Data;

import java.util.Objects;

/**
 * 列车调度计划类
 * <p>
 * 存储单个列车的完整调度信息，包括：
 * <ul>
 *   <li>车次ID和座位数</li>
 *   <li>首发时间（首站发车时刻）</li>
 *   <li>经过的所有站点</li>
 *   <li>各区段的运行时长</li>
 *   <li>各区段的票价</li>
 * </ul>
 * <p>
 * 实现 Comparable 接口，支持按车次ID排序
 * 
 * @author hands-on-ds
 * @version 1.0
 * @since 1.0
 */
@Data
public class TrainScheduler implements Comparable<TrainScheduler> {
    /** 车次ID */
    private TrainID trainID;
    
    /** 座位数（额定乘员） */
    private int seatNum;
    
    /** 首发时间（首站发车时刻），格式 HH:MM MM-DD */
    private Time startTime;
    
    /** 实际经过的站点数量 */
    private int passingStationNum;
    
    /** 经过站点的数组 */
    private StationID[] stations;
    
    /** 各区段运行时长数组（分钟） */
    private int[] duration;
    
    /** 各区段票价数组 */
    private int[] price;

    /**
     * 构造函数
     * <p>
     * 初始化固定大小的数组，容量为 {@link Config#MAX_PASSING_STATION_NUMBER}
     */
    public TrainScheduler() {
        this.stations = new StationID[Config.MAX_PASSING_STATION_NUMBER];
        this.duration = new int[Config.MAX_PASSING_STATION_NUMBER];
        this.price = new int[Config.MAX_PASSING_STATION_NUMBER];
        this.passingStationNum = 0;
    }

    /**
     * 添加站点到列车经停站列表
     * <p>
     * 将站点添加到列表末尾，如果已达最大容量则忽略
     * 
     * @param station 要添加的站点ID
     */
    public void addStation(StationID station) {
        if (passingStationNum < Config.MAX_PASSING_STATION_NUMBER) {
            stations[passingStationNum++] = station;
        }
    }

    /**
     * 在指定位置插入站点
     * <p>
     * 将后续站点后移，然后在指定索引处插入新站点
     * 
     * @param i 插入位置的索引
     * @param station 要插入的站点ID
     */
    public void insertStation(int i, StationID station) {
        for (int j = passingStationNum; j > i; j--) {
            stations[j] = stations[j - 1];
        }
        stations[i] = station;
        passingStationNum++;
    }

    /**
     * 删除指定位置的站点
     * <p>
     * 将后续站点前移，覆盖要删除的站点
     * 
     * @param i 要删除站点的索引
     */
    public void removeStation(int i) {
        for (int j = i; j < passingStationNum - 1; j++) {
            stations[j] = stations[j + 1];
        }
        passingStationNum--;
    }

    /**
     * 查找指定站点在列表中的索引
     * 
     * @param stationID 要查找的站点ID
     * @return 站点索引，如果未找到返回 -1
     */
    public int findStation(StationID stationID) {
        for (int i = 0; i + 1 < passingStationNum; i++) {
            if (stations[i].equals(stationID)) return i;
        }
        return -1;
    }

    /**
     * 设置各区段票价
     * 
     * @param price 票价数组
     */
    public void setPrice(int[] price) {
        for (int i = 0; i < price.length; i++) {
            this.price[i] = price[i];
        }
    }

    /**
     * 设置各区段运行时长
     * 
     * @param duration 时长数组（分钟）
     */
    public void setDuration(int[] duration) {
        for (int i = 0; i < duration.length; i++) {
            this.duration[i] = duration[i];
        }
    }

    /**
     * 设置车次ID
     * 
     * @param id 车次ID
     */
    public void setTrainID(TrainID id) {
        this.trainID = id;
    }

    /**
     * 设置座位数
     * 
     * @param seatNum 座位数
     */
    public void setSeatNumber(int seatNum) {
        this.seatNum = seatNum;
    }

    /**
     * 获取所有站点（复制到提供的数组）
     * 
     * @param stations 用于接收站点数据的数组
     */
    public void getStations(StationID[] stations) {
        for (int i = 0; i < passingStationNum; i++) {
            stations[i] = this.stations[i];
        }
    }

    /**
     * 获取所有区段时长（复制到提供的数组）
     * 
     * @param duration 用于接收时长数据的数组
     */
    public void getDuration(int[] duration) {
        for (int i = 0; i + 1 < passingStationNum; i++) {
            duration[i] = this.duration[i];
        }
    }

    /**
     * 获取所有区段票价（复制到提供的数组）
     * 
     * @param price 用于接收票价数据的数组
     */
    public void getPrice(int[] price) {
        for (int i = 0; i + 1 < passingStationNum; i++) {
            price[i] = this.price[i];
        }
    }

    /**
     * 获取指定索引的站点
     * 
     * @param i 站点索引
     * @return 站点ID
     */
    public StationID getStation(int i) {
        return stations[i];
    }

    /**
     * 获取指定区段的运行时长
     * 
     * @param i 区段索引
     * @return 运行时长（分钟）
     */
    public int getDuration(int i) {
        return duration[i];
    }

    /**
     * 获取指定区段的票价
     * 
     * @param i 区段索引
     * @return 票价
     */
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

    /**
     * 计算指定站点的发车时间
     * 
     * @param stationIndex 站点索引
     * @param baseDate 基准日期（用于确定具体哪一天的车次）
     * @return 该站点的发车时间
     */
    public Time getDepartureTimeAt(int stationIndex, Time baseDate) {
        if (startTime == null) {
            // 如果没有设置首发时间，使用基准日期
            return baseDate;
        }
        
        // 计算从首站到该站的总时长
        int totalDuration = 0;
        for (int i = 0; i < stationIndex && i < duration.length; i++) {
            totalDuration += duration[i];
        }
        
        // 基于首发时间和基准日期计算
        Time departureTime = new Time(
            startTime.getHour(),
            startTime.getMin(),
            baseDate.getDate().getMon(),
            baseDate.getDate().getMday()
        );
        
        return departureTime.addMinutes(totalDuration);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TrainID: ").append(trainID).append("\n");
        sb.append("SeatNum: ").append(seatNum).append("\n");
        sb.append("Start Time: ").append(startTime != null ? startTime : "Not set").append("\n");
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
