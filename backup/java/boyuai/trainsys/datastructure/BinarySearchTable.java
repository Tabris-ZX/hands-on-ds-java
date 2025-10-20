package boyuai.trainsys.datastructure;

// TODO：BST 二分查找表



/**
 * 二分查找表
 * @param <KeyType> 键类型，继承课比较类
 * @param <ValueType> 值类型
 */
public class BinarySearchTable<KeyType extends Comparable<KeyType>, ValueType> {
    // 使用seqList作为底层存储逻辑
    public SeqList<Pair<KeyType, ValueType>> data;
    // 构造函数（空参：size == 0）
    public BinarySearchTable() {
        this.data = new SeqList<>(10);
    }
    // 构造函数（指定初始容量）
    public BinarySearchTable(int size) {
        this.data = new SeqList<> (size);
    }

    /** 交换data中i和j位置的元素 */
    private void swap(int i, int j) {
        Pair<KeyType, ValueType> temp = data.visit(i);
        data.set(i, data.visit(j));
        data.set(j, temp);
    }

    /**
     * 快排的递归实现
     * 对指定范围内的元素进行排序
     * @param left 左边界
     * @param right 右边界
     */
    public void quickSort(int left, int right) {
        if (left >= right) return;
        int i = left, j = right;
        Pair<KeyType, ValueType> pivot = data.visit(left);
        while (i < j) {
            while (i < j && data.visit(j).getKey().compareTo(pivot.getKey()) >= 0) j--;
            while (i < j && data.visit(i).getKey().compareTo(pivot.getKey()) <= 0) i++;
            if (i < j) swap(i, j);
        }
        swap(left, i);
        quickSort(left, i - 1);
        quickSort(i + 1, right);
    }


    /**
     * 插入新元素（保持有序，无需快排）
     * @param key 键
     * @param value 值
     */
    public void insertEntry(KeyType key, ValueType value) {
        // 二分查找插入位置
        int left = 0, right = data.length() - 1;
        while (left <= right) {
            int mid = (left + right) / 2;
            int cmp = data.visit(mid).getKey().compareTo(key);
            if (cmp == 0) {
                // key已存在，更新value
                data.set(mid, new Pair<>(key, value));
                return;
            } else if (cmp < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        // left即为插入位置
        data.insert(left, new Pair<>(key, value));
    }

    /**
     * 查找指定键的值
     * @param key 键
     * @return 值
     */
    public ValueType find(KeyType key) {
        // 二分查找
        int left = 0, right = data.length() - 1;
        while (left <= right) {
            int mid = (left + right) / 2;
            if (data.visit(mid).getKey().compareTo(key) == 0) {
                return data.visit(mid).getValue();
            } else if (data.visit(mid).getKey().compareTo(key) < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return null; // 未找到
    }


    public void sortEntry() {
        quickSort(0, data.length() - 1);
    }

}
