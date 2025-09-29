package boyuai.trainsys.datastructure;
/**
 * 键值对
 * @param <K> 键类型
 * @param <V> 值类型
 */
public class Pair<K, V> {
    private K key;
    private V value;
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
    /**
     * 比较两个Pair的大小，先比较key，再比较value
     * @param lhs 左操作数
     * @param rhs 右操作数
     * @return 如果lhs < rhs，返回true，否则返回false
     */
    public boolean checkPairLess(Pair<K, V> lhs, Pair<K, V> rhs) {
        if (lhs.key != rhs.key) {
            return lhs.key.hashCode() < rhs.key.hashCode();
        } else {
            return lhs.value.hashCode() < rhs.value.hashCode();
        }
    }
    /**
     * 比较两个Pair是否相等
     * @param lhs 左操作数
     * @param rhs 右操作数
     * @return 如果lhs == rhs，返回true，否则返回false
     */
    public boolean checkEqual(Pair<K, V> lhs, Pair<K, V> rhs) {
        return lhs.key.equals(rhs.key) && lhs.value.equals(rhs.value);
    }
}
