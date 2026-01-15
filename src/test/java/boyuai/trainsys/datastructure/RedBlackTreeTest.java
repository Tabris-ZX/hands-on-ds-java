package boyuai.trainsys.datastructure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RedBlackTreeTest {

    @Test
    void classLoadsSuccessfully() {
        Class<?> clazz = RedBlackTree.class;
        assertTrue(clazz.getSimpleName().contains("RedBlackTree"));
    }
}


