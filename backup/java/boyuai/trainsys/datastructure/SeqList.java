package boyuai.trainsys.datastructure;

public class SeqList<T> implements List<T>{
    T[] data;
    int currentLength;
    int maxSize;

    public SeqList(int initSize) { // 有参构造，初始化顺序表大小为initSize
        this.currentLength = 0; // 修正：初始有效元素应为0
        this.maxSize = initSize;
        this.data = (T[]) new Object[initSize];
    }

    public SeqList() { // 空参构造，初始化顺序表大小为10
        this.currentLength = 0;
        this.maxSize = 10;
        this.data = (T[]) new Object[10];
    }

    @Override
    public void clear() {
        this.currentLength = 0;
    }

    @Override
    public int length() {
        return this.currentLength;
    }

    @Override
    public void insert(int i, T x) {
        if (currentLength == maxSize) {
            doubleSpace();
        }
        // 插入元素时，需要将i及后面的数后移
        for (int j = currentLength; j > i; j--) {
            data[j] = data[j - 1];
        }
        data[i] = x;
        currentLength++;
    }

    @Override
    public void remove(int i) {
        for (int j = i; j < currentLength - 1; j++) {
            data[j] = data[j + 1];
        }
        currentLength--;
    }

    @Override
    public T visit(int i) {
        if (i >= 0 && i < currentLength) {
            return  data[i];
        }
        return null;
    }

    public void doubleSpace() {
        this.maxSize *= 2;
        T[] newData = (T[]) new Object[maxSize];
        for (int i = 0; i < currentLength; i++) {
            newData[i] = data[i];
        }
        this.data = newData;
    }

    // 判断是否为空
    public boolean Empty() {
        return currentLength == 0;
    }

    // 在末尾添加元素
    public void pushBack(T x) {
        insert(currentLength, x);
    }

    // 在末尾删除元素
    public void popBack() {
        remove(currentLength - 1);
    }

    // 搜索指定元素的位置，找不到返回-1
    public int search(T x) {
        for (int i = 0; i < currentLength; i++) {
            if (data[i].equals(x)) {
                return i;
            }
        }
        return -1;
    }

    // 获取末尾元素
    public T back() {
        return visit(currentLength - 1);
    }

    // 获取当前容量
    public int getMaxSize() {
        return maxSize;
    }

    // 转换为数组
    public T[] toArray() {
        T[] result = (T[]) new Object[currentLength];
        for (int i = 0; i <currentLength; i ++) {
            result[i] = data[i];
        }
        return result;
    }

    // toString
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < currentLength; i++) {
            sb.append(data[i]);
            if (i < currentLength - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public void set(int i, T x) {
        if (i < 0 || i >= currentLength) throw new IndexOutOfBoundsException();
        data[i] = x;
    }
}