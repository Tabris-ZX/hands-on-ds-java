package boyuai.trainsys.datastructure;

/**
 * 数据类型，包含键值对
 * @param <KeyType> 键类型
 * @param <ValueType> 值类型
 */
class DataType<KeyType, ValueType> {
    KeyType key;
    ValueType value;
    
    public DataType(KeyType key, ValueType value) {
        this.key = key;
        this.value = value;
    }
}

/**
 * 动态查找表接口（内存查找表）
 * @param <KeyType> 键类型
 * @param <ValueType> 值类型
 */
interface DynamicSearchTable<KeyType, ValueType> {
    /**
     * 查找指定键的数据
     * @param x 键
     * @return 找到的数据，未找到返回null
     */
    DataType<KeyType, ValueType> find(KeyType x);
    
    /**
     * 插入数据
     * @param x 要插入的数据
     */
    void insert(DataType<KeyType, ValueType> x);
    
    /**
     * 删除指定键的数据
     * @param x 键
     */
    void remove(KeyType x);
}

/**
 * 存储查找表接口（持久化查找表）
 * @param <KeyType> 键类型
 * @param <ValueType> 值类型
 */
interface StorageSearchTable<KeyType, ValueType> {
    /**
     * 查找指定键的所有值
     * @param key 键
     * @return 值列表
     */
    SeqList<ValueType> find(KeyType key);
    
    /**
     * 插入键值对
     * @param key 键
     * @param val 值
     */
    void insert(KeyType key, ValueType val);
    
    /**
     * 删除指定的键值对
     * @param key 键
     * @param val 值
     */
    void remove(KeyType key, ValueType val);
    
    /**
     * 获取表中元素的数量
     * @return 元素数量
     */
    int size();
}
