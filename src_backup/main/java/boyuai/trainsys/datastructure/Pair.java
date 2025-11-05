package boyuai.trainsys.datastructure;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 键值对
 * @param <K> 键类型
 * @param <V> 值类型
 */
@Data
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
    public static <K, V> boolean checkPairLess(Pair<K, V> lhs, Pair<K, V> rhs) {
        if (!lhs.key.equals(rhs.key)) {
            if (lhs.key instanceof Comparable && rhs.key instanceof Comparable) {
                return ((Comparable<K>) lhs.key).compareTo(rhs.key) < 0;
            } else {
                return lhs.key.hashCode() < rhs.key.hashCode();
            }
        } else {
            if (lhs.value instanceof Comparable && rhs.value instanceof Comparable) {
                return ((Comparable<V>) lhs.value).compareTo(rhs.value) < 0;
            } else {
                return lhs.value.hashCode() < rhs.value.hashCode();
            }
        }
    }
    
    /**
     * 比较两个Pair是否相等
     * @param lhs 左操作数
     * @param rhs 右操作数
     * @return 如果lhs == rhs，返回true，否则返回false
     */
    public static <K, V> boolean checkEqual(Pair<K, V> lhs, Pair<K, V> rhs) {
        return lhs.key.equals(rhs.key) && lhs.value.equals(rhs.value);
    }

}
