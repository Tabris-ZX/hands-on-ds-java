package boyuai.trainsys.datastructure;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * 简化的B+树实现（内存存储）
 * 用于解决序列化问题
 * @param <KeyType> 键类型
 * @param <ValueType> 值类型
 */
public class SimpleBPlusTree<KeyType, ValueType> {
    
    private final Map<KeyType, List<ValueType>> storage;
    
    public SimpleBPlusTree() {
        this.storage = new HashMap<>();
    }
    
    public SimpleBPlusTree(String filename) {
        this.storage = new HashMap<>();
        // 忽略文件名，使用内存存储
    }
    
    public SeqList<ValueType> find(KeyType key) {
        SeqList<ValueType> result = new SeqList<>();
        List<ValueType> values = storage.get(key);
        if (values != null) {
            for (ValueType value : values) {
                result.pushBack(value);
            }
        }
        return result;
    }
    
    public void insert(KeyType key, ValueType value) {
        storage.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }
    
    public void remove(KeyType key, ValueType value) {
        List<ValueType> values = storage.get(key);
        if (values != null) {
            values.remove(value);
            if (values.isEmpty()) {
                storage.remove(key);
            }
        }
    }
    
    public int size() {
        return storage.size();
    }
}
