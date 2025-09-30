package boyuai.trainsys.datastructure;

public class ListTest {
    public static void main(String[] args) {
        seqList<Integer> list = new seqList<>(5);
        list.insert(0, 1);
        list.insert(1, 2);
        list.insert(2, 3);
        list.insert(3, 4);
        list.insert(4, 5);
        list.insert(5, 6);
        System.out.println("List length: " + list.length());
        for (int i = 0; i < list.length(); i++) {
            System.out.println("Element at index " + i + ": " + list.visit(i));
        }
        list.remove(2);
        System.out.println("After removing element at index 2:");
        for (int i = 0; i < list.length(); i++) {
            System.out.println("Element at index " + i + ": " + list.visit(i));
        }
    }
}