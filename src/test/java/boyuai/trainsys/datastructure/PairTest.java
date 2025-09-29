package boyuai.trainsys.datastructure;

import boyuai.trainsys.datastructure.Pair;

public class PairTest {
    public static void main(String[] args) {
        Pair<Integer, String> pair1 = new Pair<>(1, "one");
        Pair<Integer, String> pair2 = new Pair<>(2, "two");
        Pair<Integer, String> pair3 = new Pair<>(1, "one");

        // Test checkPairLess
        System.out.println("pair1 < pair2: " + pair1.checkPairLess(pair1, pair2)); // Expected: true
        System.out.println("pair2 < pair1: " + pair1.checkPairLess(pair2, pair1)); // Expected: false
        System.out.println("pair1 < pair3: " + pair1.checkPairLess(pair1, pair3)); // Expected: false

        // Test checkEqual
        System.out.println("pair1 == pair2: " + pair1.checkEqual(pair1, pair2)); // Expected: false
        System.out.println("pair1 == pair3: " + pair1.checkEqual(pair1, pair3)); // Expected: true
    }
}
