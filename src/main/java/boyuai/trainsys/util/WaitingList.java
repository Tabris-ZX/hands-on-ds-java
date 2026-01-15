package boyuai.trainsys.util;

import boyuai.trainsys.config.StaticConfig;
import boyuai.trainsys.datastructure.LinkQueue;
import boyuai.trainsys.info.PurchaseInfo;

/**
 * 等待列表（先进先出）
 */
public class WaitingList {
    private LinkQueue<PurchaseInfo> purchaseQueue;
    private int listSize;

    /**
     * 构造函数
     */
    public WaitingList() {
        purchaseQueue = new LinkQueue<>();
        listSize = 0;
    }

    /**
     * 添加到等待列表
     * @param purchaseInfo 购票信息
     */
    public void addToWaitingList(PurchaseInfo purchaseInfo) {
        purchaseQueue.enQueue(purchaseInfo);
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
        return purchaseQueue.getHead();
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
        return listSize >= StaticConfig.BUSY_STATE_THRESHOLD;
    }

    /**
     * 获取列表大小
     * @return 列表大小
     */
    public int getSize() {
        return listSize;
    }
}

