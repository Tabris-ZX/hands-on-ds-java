package boyuai.trainsys.datastructure;

/**
 * 链式队列实现
 * @param <T> 队列元素类型
 * */
public class LinkQueue<T> implements Queue<T> {
    // 队列结点
    private static class Node<T> {
        T data;
        Node<T> next;

        Node (T data, Node<T> next) {
            this.data = data;
            this.next = next;
        }

        Node() {
            this.next = null;
        }
    }

    // 创建首尾结点
    Node<T> front, rear;

    // 空参构造函数
    public LinkQueue() {
        front = rear = null;
    }

    /**
     * 判断队列是否为空
     * @return true表示空，false为非空
     */
    @Override
    public boolean isEmpty() {
        return front == null;
    }

    /**
     * 将元素加入队列
     * @param element 待入对元素
     */
    @Override
    public void enQueue(T element) {
        if (isEmpty()) {    // 如果队列为空
            front = rear = new Node<T>(element, null);
        } else {
            Node<T> temp_node = new Node<T>(element, null);
            rear.next = temp_node;
            rear = temp_node;
        }
    }

    /**
     * 取出队首元素，并删除
     * @return 队首元素
     */
    @Override
    public T deQueue() {
        if (isEmpty()) {
            return null;
        } else {
            T result = front.data;
            front = front.next;
            return result;
        }
    }

    /**
     * 获取队首元素
     * @return 队首元素
     */
    @Override
    public T getHead() {
        return front.data;
    }
}
