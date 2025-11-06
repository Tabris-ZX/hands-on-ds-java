package boyuai.trainsys.datastructure;

import lombok.Getter;

/**
 * 并查集（Disjoint Set Union）
 * 支持路径压缩和按秩合并优化
 */
@Getter
public class DisjointSet {

    private int size;
    private int[] parent;
    private int[] rank; // 按秩合并所用秩（近似树高）
    
    /**
     * 构造函数
     * @param n 元素个数
     */
    public DisjointSet(int n) {
        size = n;
        parent = new int[size];
        rank = new int[size];
        // 初始化：每个元素的父节点都是自己，秩为0
        for (int i = 0; i < size; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }
    
    /**
     * 合并两个集合
     * @param root1 第一个集合的根
     * @param root2 第二个集合的根
     */
    public void join(int root1, int root2) {
        if (root1 == root2) return;
        // 使用按秩合并：秩小的挂到秩大的根上
        if (rank[root1] < rank[root2]) {
            parent[root1] = root2;
        } else if (rank[root1] > rank[root2]) {
            parent[root2] = root1;
        } else {
            parent[root2] = root1;
            rank[root1]++;
        }
    }
    
    /**
     * 查找元素x的根节点，并进行路径压缩
     * @param x 要查找的元素
     * @return 根节点
     */
    public int find(int x) {
        if (parent[x] == x) {
            return x; // x是根节点
        }
        // 路径压缩：将x直接连接到根节点
        return parent[x] = find(parent[x]);
    }

    /**
     * 便捷方式：合并包含x和y的两个集合
     * @param x 节点x
     * @param y 节点y
     */
    public void union(int x, int y) {
        join(find(x), find(y));
    }

    /**
     * 判断两个元素是否属于同一个集合
     * @param x 节点x
     * @param y 节点y
     * @return 是否同集合
     */
    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }
}