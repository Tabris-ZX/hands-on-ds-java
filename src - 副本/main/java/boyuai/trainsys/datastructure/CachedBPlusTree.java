package boyuai.trainsys.datastructure;

import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;

/**
 * 带缓存的B+树实现
 * 使用write-through缓存策略
 * @param <KeyType> 键类型
 * @param <ValueType> 值类型
 */
@Setter
@Getter
public class CachedBPlusTree<KeyType, ValueType> {
    
    private BPlusTree<KeyType, ValueType> storage;  // 持久化存储
    private RedBlackTree<KeyType, ValueType> cache;                  // 内存缓存
    private Comparator<KeyType> comparator;
    
    /**
     * 构造函数
     * @param filename 文件名前缀
     */
    public CachedBPlusTree(String filename) {
        this(filename, null);
    }
    
    /**
     * 构造函数
     * @param filename 文件名前缀
     * @param comparator 键比较器
     */
    public CachedBPlusTree(String filename, Comparator<KeyType> comparator) {
        this.comparator = comparator;
        this.storage = new BPlusTree<>(filename, 100, 100, comparator);
        this.cache = new RedBlackTree<>(comparator);
    }
    
    /**
     * 检查是否包含指定键
     * @param key 键
     * @return 是否包含
     */
    public boolean contains(KeyType key) {
        // 先检查缓存
        if (cache.find(key) != null) {
            return true;
        }
        // 缓存未命中，检查存储
        return !storage.find(key).Empty();
    }
    
    /**
     * 查找指定键的值
     * @param key 键
     * @return 值，未找到返回null
     */
    public ValueType find(KeyType key) {
        // 先检查缓存
        DataType<KeyType, ValueType> cached = cache.find(key);
        if (cached != null) {
            return cached.value;
        }
        
        // 缓存未命中，从存储中查找
        SeqList<ValueType> values = storage.find(key);
        if (!values.Empty()) {
            ValueType value = values.visit(0);
            // 将结果加入缓存
            cache.insert(new DataType<>(key, value));
            return value;
        }
        
        return null;
    }
    
    /**
     * 插入键值对
     * @param key 键
     * @param value 值
     */
    public void insert(KeyType key, ValueType value) {
        // 写入存储（write-through策略）
        storage.insert(key, value);
        // 更新缓存
        cache.insert(new DataType<>(key, value));
    }
    
    /**
     * 删除指定键
     * @param key 键
     */
    public void remove(KeyType key) {
        // 从存储中删除第一个匹配的值
        SeqList<ValueType> values = storage.find(key);
        if (!values.Empty()) {
            storage.remove(key, values.visit(0));
        }
        // 从缓存中删除
        cache.remove(key);
    }
    
    /**
     * 删除指定的键值对
     * @param key 键
     * @param value 值
     */
    public void remove(KeyType key, ValueType value) {
        // 从存储中删除
        storage.remove(key, value);
        // 从缓存中删除
        cache.remove(key);
    }
    
    /**
     * 获取数据总数
     * @return 数据总数
     */
    public int size() {
        return storage.size();
    }
    
    /**
     * 清空缓存和存储
     */
    public void clear() {
        storage.clear();
        // 清空缓存（需要重新实现RedBlackTree的clear方法）
        // cache.clear();
    }
    
    /**
     * 关闭文件
     */
    public void close() {
        storage.close();
    }
    
    /**
     * 获取缓存命中率（需要扩展实现）
     * @return 缓存命中率
     */
    public double getCacheHitRate() {
        // 这里需要添加统计信息，暂时返回0
        return 0.0;
    }
    
    /**
     * 刷新缓存（清空缓存，强制从存储重新加载）
     */
    public void refreshCache() {
        // 清空缓存
        // cache.clear();
    }
}