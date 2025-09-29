package BoyuAI.trainsys.DataStructure;


/**
 * 抽象基类
 * @param <T> 队列元素类型
 * */
public  interface Queue<T> {
    // 判断队列是否为空
    // @return true表示空，false为非空
    public abstract boolean isEmpty();
    // 将元素加入队列
    // @param element 待加入的元素
    public abstract void enQueue(T element);
    // 取出队首元素，并删除
    // @return 队首元素
    public abstract T deQueue();
    // 获取队首元素
    // @return 队首元素
    public abstract T getHead();
}


