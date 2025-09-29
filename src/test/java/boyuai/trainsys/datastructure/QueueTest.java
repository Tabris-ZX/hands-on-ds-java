package boyuai.trainsys.datastructure;

import boyuai.trainsys.datastructure.LinkQueue;
import boyuai.trainsys.datastructure.Queue;

public class QueueTest {
    public static void main(String[] args) {
        /**
         * 测试LinkQueue
         */
        Queue<Integer> q = new LinkQueue<>();
        System.out.println("LinkQueue测试：");
        System.out.println("队列是否为空：" + q.isEmpty());
        q.enQueue(1);
        q.enQueue(2);
        q.enQueue(3);
        System.out.println("队列是否为空：" + q.isEmpty());
        System.out.println("出队元素：" + q.deQueue());
        System.out.println("出队元素：" + q.deQueue());
        System.out.println("出队元素：" + q.deQueue());
        System.out.println("队列是否为空：" + q.isEmpty());
        System.out.println();
        /**
         * 测试PriorityQueue
         */
        Queue<Integer> pq = new boyuai.trainsys.datastructure.PriorityQueue<>();
        System.out.println("PriorityQueue测试：");
        System.out.println("队列是否为空：" + pq.isEmpty());
        pq.enQueue(3);
        pq.enQueue(1);
        pq.enQueue(2);
        System.out.println("队列是否为空：" + pq.isEmpty());
        System.out.println("出队元素：" + pq.deQueue());
        System.out.println("出队元素：" + pq.deQueue());
        System.out.println("出队元素：" + pq.deQueue());
        System.out.println("队列是否为空：" + pq.isEmpty());
    }
}
