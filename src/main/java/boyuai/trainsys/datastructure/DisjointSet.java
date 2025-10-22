package boyuai.trainsys.datastructure;

import lombok.Data;
import lombok.Getter;

/**
 * 并查集（Disjoint Set Union）
 * 支持路径压缩和按秩合并优化
 */
@Getter
public class DisjointSet {

    private int size;
    private int[] parent;
    
    /**
     * 构造函数
     * @param n 元素个数
     */
    public DisjointSet(int n) {
        size = n;
        parent = new int[size];
        // 初始化：每个元素的父节点都是自己（-1表示根节点）
        for (int i = 0; i < size; i++) {
            parent[i] = -1;
        }
    }
    
    /**
     * 合并两个集合
     * @param root1 第一个集合的根
     * @param root2 第二个集合的根
     */
    public void join(int root1, int root2) {
        if (root1 == root2) return;
        
        // 按秩合并：将较小的树合并到较大的树下
        if (parent[root1] > parent[root2]) {
            parent[root2] += parent[root1];
            parent[root1] = root2;
        } else {
            parent[root1] += parent[root2];
            parent[root2] = root1;
        }
    }
    
    /**
     * 查找元素x的根节点，并进行路径压缩
     * @param x 要查找的元素
     * @return 根节点
     */
    public int find(int x) {
        if (parent[x] < 0) {
            return x; // x是根节点
        }
        // 路径压缩：将x直接连接到根节点
        return parent[x] = find(parent[x]);
    }

}