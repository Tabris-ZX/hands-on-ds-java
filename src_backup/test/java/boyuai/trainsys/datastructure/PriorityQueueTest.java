package boyuai.trainsys.datastructure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PriorityQueueTest {

    @Test
    void classLoadsSuccessfully() {
        Class<?> clazz = PriorityQueue.class;
        assertTrue(clazz.getSimpleName().contains("PriorityQueue"));
    }
}


