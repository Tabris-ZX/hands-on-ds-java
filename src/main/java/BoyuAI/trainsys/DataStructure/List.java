package BoyuAI.trainsys.DataStructure;

// TODO：列表

/**
 * 列表接口
 */
public interface List<T> {
    public abstract void clear(); // 清空列表
    public abstract int length(); // 返回列表长度
    public abstract void insert(int i, T x); // 在i位置插入x
    public abstract void remove(int i); // 删除i位置的元素
    public abstract T visit(int i); // 返回位置i的元素
}
