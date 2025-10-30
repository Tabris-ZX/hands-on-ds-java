package boyuai.trainsys.datastructure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SearchTableTest {

    @Test
    void classLoadsSuccessfully() {
        Class<?> clazz = StorageSearchTable.class;
        assertTrue(clazz.getSimpleName().contains("StorageSearchTable"));
    }
}


