package boyuai.trainsys.manager;

import boyuai.trainsys.TrainScheduler;
import boyuai.trainsys.datastructure.BPlusTree;
import boyuai.trainsys.datastructure.SeqList;
import boyuai.trainsys.util.FixedString;
import boyuai.trainsys.util.Types.*;

/**
 * 列车调度管理器
 * 负责管理所有列车的运行计划
 */
public class SchedulerManager {

    // 使用B+树存储列车调度信息，以列车ID为索引
    private final BPlusTree<FixedString, TrainScheduler> schedulerInfo;

    /**
     * 构造函数
     * @param filename 数据文件名
     */
    public SchedulerManager(String filename) {
        this.schedulerInfo = new BPlusTree<>(filename, FixedString.class, TrainScheduler.class);
    }

    /**
     * 添加一个运行计划
     * @param trainID 列车ID
     * @param seatNum 座位数量
     * @param passingStationNumber 途径站点数
     * @param stations 站点数组
     * @param duration 各段运行时间数组
     * @param price 各段票价数组
     */
    public void addScheduler(FixedString trainID, int seatNum,
                             int passingStationNumber, int[] stations,
                             int[] duration, int[] price) {
        TrainScheduler scheduler = new TrainScheduler();
        scheduler.setTrainID(trainID);

        // 添加所有站点
        for (int i = 0; i < passingStationNumber; i++) {
            scheduler.addStation(stations[i]);
        }

        // 设置运行时间和票价
        scheduler.setDuration(duration);
        scheduler.setPrice(price);
        scheduler.setSeatNumber(seatNum);

        // 插入到B+树中
        schedulerInfo.insert(trainID, scheduler);
    }

    /**
     * 查询某个ID的运行计划是否存在
     * @param trainID 列车ID
     * @return 是否存在
     */
    public boolean existScheduler(FixedString trainID) {
        return schedulerInfo.contains(trainID);
    }

    /**
     * 查询某个ID的运行计划
     * @param trainID 列车ID
     * @return 运行计划对象，如果不存在返回null
     */
    public TrainScheduler getScheduler(FixedString trainID) {
        SeqList<TrainScheduler> relatedInfo = schedulerInfo.find(trainID);
        // 一个trainID理应只对应一个运行计划，但由于这里的B+树是一对多的B+树，所以需要使用seqList
        if (relatedInfo != null && relatedInfo.length() > 0) {
            return relatedInfo.visit(0);
        }
        return null;
    }

    /**
     * 删除某个ID的运行计划
     * @param trainID 列车ID
     */
    public void removeScheduler(FixedString trainID) {
        SeqList<TrainScheduler> relatedInfo = schedulerInfo.find(trainID);
        if (relatedInfo != null) {
            for (int i = 0; i < relatedInfo.length(); i++) {
                schedulerInfo.remove(trainID, relatedInfo.visit(i));
            }
        }
    }

    /**
     * 获取所有列车调度计划
     * @return 所有列车调度计划的列表
     */
    public SeqList<TrainScheduler> getAllSchedulers() {
        SeqList<TrainScheduler> allSchedulers = new SeqList<>();
        // 这里需要遍历B+树获取所有调度计划
        // 具体实现依赖于BPlusTree的遍历接口
        return allSchedulers;
    }

    /**
     * 更新运行计划
     * @param trainID 列车ID
     * @param scheduler 新的运行计划
     */
    public void updateScheduler(FixedString trainID, TrainScheduler scheduler) {
        // 先删除旧的，再添加新的
        removeScheduler(trainID);
        schedulerInfo.insert(trainID, scheduler);
    }

    /**
     * 保存数据到文件
     */
    public void save() {
        // B+树应该自动持久化到文件
        // 这里可以调用B+树的保存方法（如果有的话）
    }

    /**
     * 从文件加载数据
     */
    public void load() {
        // B+树应该在构造时自动从文件加载
        // 这里可以调用B+树的加载方法（如果有的话）
    }
}
