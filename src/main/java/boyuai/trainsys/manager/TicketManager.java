package boyuai.trainsys.manager;

import boyuai.trainsys.core.TrainScheduler;
import boyuai.trainsys.info.TicketInfo;
import boyuai.trainsys.datastructure.BPlusTree;
import boyuai.trainsys.datastructure.SeqList;
import boyuai.trainsys.util.Date;
import boyuai.trainsys.util.FixedString;
import boyuai.trainsys.util.Types.*;

/**
 * 票务管理器
 * 负责管理车票的查询、购买、退票等操作
 */
public class TicketManager {

    // 数据成员：一个从 trainID 到 [一组 TicketInfo] 的 B+ 树索引
    private BPlusTree<FixedString, TicketInfo> ticketInfo;

    /**
     * 构造函数
     * @param filename 数据文件名
     */
    public TicketManager(String filename) {
        this.ticketInfo = new BPlusTree<>(filename);
    }

    /**
     * 查询余票数量
     * 给定车次号、乘车日期、始发站（由于分段购票原则，始发站确定即终点站确定），查询余票数量
     * 由于 B+ 树索引只能从 TrainID 到 该车次号 所有日期、所有区间段的余票信息
     * 在拿到 TicketInfo 数组后，需要暴力枚举日期与始发站，才能查到票数
     *
     * @param trainID 列车ID
     * @param date 日期
     * @param stationID 始发站ID
     * @return 余票数量，-1表示未找到
     */
    public int querySeat(FixedString trainID, Date date, int stationID) {
        SeqList<TicketInfo> relatedInfo = ticketInfo.find(trainID);

        if (relatedInfo == null) {
            return -1;
        }

        for (int i = 0; i < relatedInfo.length(); i++) {
            TicketInfo info = relatedInfo.visit(i);
            if (info.getDate().equals(date) && info.getDepartureStation().value() == stationID) {
                return info.getSeatNum();
            }
        }

        return -1; // 出错，没有找到符合条件的车票
    }

    /**
     * 更新余票数量
     * 给定车次号、乘车日期、始发站、购票或退票，修改余票数量，索引方式同上
     *
     * @param trainID 列车ID
     * @param date 日期
     * @param stationID 始发站ID
     * @param delta 变化量（1表示购票，-1表示退票）
     * @return 票价，-1表示错误
     */
    public int updateSeat(FixedString trainID, Date date, int stationID, int delta) {
        SeqList<TicketInfo> relatedInfo = ticketInfo.find(trainID);

        if (relatedInfo == null) {
            return -1;
        }

        for (int i = 0; i < relatedInfo.length(); i++) {
            TicketInfo info = relatedInfo.visit(i);
            if (info.getDate().equals(date) && info.getDepartureStation().value() == stationID) {
                // 生成更新后的票务信息（B+树不支持 modify，采用删后插）
                TicketInfo updatedInfo = new TicketInfo(
                        info.getTrainID(),
                        info.getDepartureStation(),
                        info.getArrivalStation(),
                        info.getSeatNum() + delta,
                        info.getPrice(),
                        info.getDuration(),
                        info.getDate()
                );

                // 更新B+树中的信息
                ticketInfo.remove(trainID, info);
                ticketInfo.insert(trainID, updatedInfo);

                return updatedInfo.getPrice();
            }
        }

        return -1; // 出错，没有找到符合条件的车票
    }

    /**
     * 开售车票
     * 给定列车运行计划，开售列车运行计划分段、逐日的车票，导入车票管理系统
     * 在此处new TicketInfo的空间，构建 B+ 树等，TicketInfo 从 TrainScheduler 拷信息
     *
     * @param scheduler 列车调度计划
     * @param date 开售日期
     */
    public void releaseTicket(TrainScheduler scheduler, Date date) {
        int passingStationNum = scheduler.getPassingStationNum();

        // 为每个区间段创建车票信息
        for (int i = 0; i + 1 < passingStationNum; i++) {
            TicketInfo newTicket = new TicketInfo();
            newTicket.setTrainID(scheduler.getTrainID());
            newTicket.setDepartureStation(scheduler.getStation(i));
            newTicket.setArrivalStation(scheduler.getStation(i + 1));
            newTicket.setSeatNum(scheduler.getSeatNum());
            newTicket.setPrice(scheduler.getPrice(i));
            newTicket.setDuration(scheduler.getDuration(i));
            newTicket.setDate(date);

            // 插入到B+树中
            ticketInfo.insert(newTicket.getTrainID(), newTicket);
        }
    }

    /**
     * 停售车票
     * 给定车次与日期，停售该列车运行计划分段、逐日的车票
     *
     * @param trainID 列车ID
     * @param date 日期
     */
    public void expireTicket(FixedString trainID, Date date) {
        SeqList<TicketInfo> relatedInfo = ticketInfo.find(trainID);

        if (relatedInfo == null) {
            return;
        }

        // 删除所有匹配日期的车票信息
        for (int i = 0; i < relatedInfo.length(); i++) {
            TicketInfo info = relatedInfo.visit(i);
            if (info.getDate().equals(date)) {
                ticketInfo.remove(trainID, info);
            }
        }
    }

}
