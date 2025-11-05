package boyuai.trainsys.datastructure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SeqListTest {

    @Test
    void classLoadsSuccessfully() {
        Class<?> clazz = SeqList.class;
        assertTrue(clazz.getSimpleName().contains("SeqList"));
    }
}


