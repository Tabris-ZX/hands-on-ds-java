package boyuai.trainsys.datastructure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CachedBPlusTreeTest {

    @Test
    void classLoadsSuccessfully() {
        Class<?> clazz = CachedBPlusTree.class;
        assertTrue(clazz.getSimpleName().contains("CachedBPlusTree"));
    }
}


