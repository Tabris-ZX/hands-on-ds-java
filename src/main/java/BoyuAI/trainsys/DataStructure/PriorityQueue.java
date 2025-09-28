package BoyuAI.trainsys.DataStructure;

/**
 * 优先级队列（最小堆）
 * @param <T> 队列元素类型，必须实现Comparable接口
 */
public class PriorityQueue<T extends Comparable<T>> extends Queue<T> {
    private int currentSize; // 队列长度
    private T[] array; // 存储队列元素的数组
    private int maxSize; // 容量

    /**
     * 构造函数
     * @param capacity 队列容量
     */
    @SuppressWarnings("unchecked")
    public PriorityQueue(int capacity) {
        array =  (T[]) new Comparable[capacity];
        maxSize = capacity;
        currentSize = 0;
    }

    /**
     * 构造函数，初始化容量100
     */
    public PriorityQueue() {
        this(100);
    }

    /**
     * 判断队列是否为空
     * @return true表示空，false为非空
     */
    @Override
    public boolean isEmpty() {
        return currentSize == 0;
    }

    /**
     * 将元素加入队列
     * @param element 待入对元素
     */
    @Override
    public void enQueue(T element) {
        if (currentSize == maxSize - 1) {
            doubleSpace();
        }

        // 向上过滤
        int hole = ++currentSize;
        for (; hole > 1 && element.compareTo(array[hole / 2]) < 0; hole /= 2) {
            array[hole] = array[hole / 2];
        }
        array[hole] = element;
    }

    /**
     * 取出队首元素，并删除
     * @return 队首元素
     * @throws RuntimeException 队列为空时抛出
     */
    @Override
    public T deQueue() {
        if (currentSize == 0) {
            throw new RuntimeException("队列为空");
        }
        T minItem = array[1];  // 根结点保存的是二叉堆的最小值
        array[1] = array[currentSize--];  // 将二叉堆的最后一个元素移到根结点
        percolateDown(1);  // 将根结点的元素向下过滤
        return minItem;

    }

    /**
     * 获取队首元素
     * @return 队首元素
     * @throws RuntimeException 队列为空时抛出
     */
    @Override
    public T getHead() {
        if (currentSize == 0) {
            return null;
        } else {
            return array[1];
        }
    }


    /**
     * 扩展数组空间
     */
    @SuppressWarnings("unchecked")
    private void doubleSpace() {
        T[] tmp = array;

        maxSize *= 2;
        array = (T[]) new Comparable[maxSize];
        for (int i = 0; i <= currentSize; ++i) {
            array[i] = tmp[i];
        }
    }

    /**
     * 向下过滤
     * @param hole 开始过滤的位置
     */
    private void percolateDown(int hole) {
        int child;
        // 将待过滤结点的值保存在tmp中
        T tmp = array[hole];

        // 向下过滤
        // hole中保存了空结点的位置
        for (; hole * 2 <= currentSize; hole = child) {
            child = hole * 2;  // 找到结点的左儿子
            if (child != currentSize && array[child + 1].compareTo(array[child]) < 0) {
                child++;
            }
            // child变量保存了左右儿子中较小的儿子，并继续向下过滤
            if (array[child].compareTo(tmp) < 0) {
                // 如果tmp比child更大，那么它替换掉该结点，由该结点代替tmp中的值向下过滤
                array[hole] = array[child];
            } else {
                break;  // 当前的空结点是一个符合规定的位置
            }
        }
        array[hole] = tmp;  // 将tmp的值写入空结点
    }
}
