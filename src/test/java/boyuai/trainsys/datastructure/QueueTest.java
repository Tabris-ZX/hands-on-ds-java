package boyuai.trainsys.datastructure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class QueueTest {

    @Test
    void linkQueue_basicEnqueueDequeue() {
        Queue<Integer> q = new LinkQueue<>();
        assertTrue(q.isEmpty());
        q.enQueue(1);
        q.enQueue(2);
        q.enQueue(3);
        assertFalse(q.isEmpty());
        assertEquals(1, q.deQueue());
        assertEquals(2, q.deQueue());
        assertEquals(3, q.deQueue());
        assertTrue(q.isEmpty());
    }

    @Test
    void priorityQueue_basicEnqueueDequeue() {
        Queue<Integer> pq = new boyuai.trainsys.datastructure.PriorityQueue<>();
        assertTrue(pq.isEmpty());
        pq.enQueue(3);
        pq.enQueue(1);
        pq.enQueue(2);
        assertFalse(pq.isEmpty());
        assertEquals(1, pq.deQueue());
        assertEquals(2, pq.deQueue());
        assertEquals(3, pq.deQueue());
        assertTrue(pq.isEmpty());
    }
}
