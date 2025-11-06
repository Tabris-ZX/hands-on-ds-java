package boyuai.trainsys.datastructure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DisjointSetTest {

    @Test
    void classLoadsSuccessfully() {
        Class<?> clazz = DisjointSet.class;
        assertTrue(clazz.getSimpleName().contains("DisjointSet"));
    }
}


