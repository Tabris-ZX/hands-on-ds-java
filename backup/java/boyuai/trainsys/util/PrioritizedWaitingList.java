package boyuai.trainsys.util;

import boyuai.trainsys.config.Config;
import boyuai.trainsys.datastructure.PriorityQueue;
import boyuai.trainsys.info.PurchaseInfo; /**
 * 优先级等待列表
 */
public class PrioritizedWaitingList {
    private PriorityQueue<PurchaseInfoWrapper> purchaseQueue;
    private int listSize;

    /**
     * 包装类，使PurchaseInfo可以在优先队列中比较
     */
    private static class PurchaseInfoWrapper implements Comparable<PurchaseInfoWrapper> {
        PurchaseInfo purchaseInfo;
        int priority;

        PurchaseInfoWrapper(PurchaseInfo purchaseInfo, int priority) {
            this.purchaseInfo = purchaseInfo;
            this.priority = priority;
        }

        @Override
        public int compareTo(PurchaseInfoWrapper other) {
            // 优先级高的排在前面（负数表示this优先级更高）
            return Integer.compare(other.priority, this.priority);
        }
    }

    /**
     * 构造函数
     */
    public PrioritizedWaitingList() {
        purchaseQueue = new PriorityQueue<>();
        listSize = 0;
    }

    /**
     * 添加到等待列表（需要在外部提供优先级）
     * @param purchaseInfo 购票信息
     * @param priority 优先级
     */
    public void addToWaitingList(PurchaseInfo purchaseInfo, int priority) {
        purchaseQueue.enQueue(new PurchaseInfoWrapper(purchaseInfo, priority));
        listSize++;
    }

    /**
     * 从等待列表头部移除
     */
    public void removeHeadFromWaitingList() {
        if (!purchaseQueue.isEmpty()) {
            purchaseQueue.deQueue();
            listSize--;
        }
    }

    /**
     * 获取队首的购票信息
     * @return 购票信息
     */
    public PurchaseInfo getFrontPurchaseInfo() {
        PurchaseInfoWrapper wrapper = purchaseQueue.getHead();
        return wrapper != null ? wrapper.purchaseInfo : null;
    }

    /**
     * 判断是否为空
     * @return 如果为空返回true
     */
    public boolean isEmpty() {
        return purchaseQueue.isEmpty();
    }

    /**
     * 判断是否繁忙
     * @return 如果繁忙返回true
     */
    public boolean isBusy() {
        return listSize > Config.BUSY_STATE_THRESHOLD;
    }

    /**
     * 获取列表大小
     * @return 列表大小
     */
    public int getSize() {
        return listSize;
    }
}
