package boyuai.trainsys.datastructure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BPlusTreeTest {

    @Test
    void classLoadsSuccessfully() {
        Class<?> clazz = BPlusTree.class;
        assertTrue(clazz.getSimpleName().contains("BPlusTree"));
    }
}


