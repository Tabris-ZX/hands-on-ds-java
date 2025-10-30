package boyuai.trainsys.datastructure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PairTest {

    @Test
    void compareAndEqual_shouldWork() {
        Pair<Integer, String> pair1 = new Pair<>(1, "one");
        Pair<Integer, String> pair2 = new Pair<>(2, "two");
        Pair<Integer, String> pair3 = new Pair<>(1, "one");

        assertTrue(Pair.checkPairLess(pair1, pair2));
        assertFalse(Pair.checkPairLess(pair2, pair1));
        assertFalse(Pair.checkPairLess(pair1, pair3));

        assertFalse(Pair.checkEqual(pair1, pair2));
        assertTrue(Pair.checkEqual(pair1, pair3));
    }
}
