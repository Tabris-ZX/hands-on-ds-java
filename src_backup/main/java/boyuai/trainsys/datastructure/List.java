package boyuai.trainsys.datastructure;

/**
 * 列表接口
 */
public interface List<T> {
    void clear(); // 清空列表
    int length(); // 返回列表长度
    void insert(int i, T x); // 在i位置插入x
    void remove(int i); // 删除i位置的元素
    T visit(int i); // 返回位置i的元素
}
