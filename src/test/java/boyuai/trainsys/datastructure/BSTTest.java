package boyuai.trainsys.datastructure;

// 测试：BinarySearchTable
public class BSTTest {
    public static void main(String[] args) {
        BinarySearchTable<Integer, String> bst = new BinarySearchTable<>();
        // 测试：insertEntry
        bst.insertEntry(5, "five");
        bst.insertEntry(3, "three");
        bst.insertEntry(7, "seven");
        bst.insertEntry(2, "two");
        bst.insertEntry(4, "four");
        bst.insertEntry(6, "six");
        bst.insertEntry(8, "eight");
        System.out.println("After insertions:");
        for (int i = 1; i <= 8; i++) {
            System.out.println("Key: " + i + ", Value: " + bst.find(i));
        }
        // 测试：find
        System.out.println("Find key 4: " + bst.find(4)); // Expected: four
        System.out.println("Find key 10: " + bst.find(10)); // Expected
    }
}
