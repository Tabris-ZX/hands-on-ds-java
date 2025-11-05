package boyuai.trainsys.datastructure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdjListGraphTest {

    @Test
    void classLoadsSuccessfully() {
        // 引用类以确保编译期可见
        Class<?> clazz = AdjListGraph.class;
        assertTrue(clazz.getSimpleName().contains("AdjListGraph"));
    }
}


