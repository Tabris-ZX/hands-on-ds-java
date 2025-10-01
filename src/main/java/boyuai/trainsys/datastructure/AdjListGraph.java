package boyuai.trainsys.datastructure;

import java.util.ArrayList;

// TODO：邻接表图的实现
public class AdjListGraph<T> implements Graph<T>{
    // 创建一个结点类
    public class edgeNode<T> {
        int end;
        T weight;
        edgeNode<T> next;

        public edgeNode(int end, T weight, edgeNode<T> next) {
            this.end = end;
            this.weight = weight;
            this.next = next;
        }
    }

    // 邻接表：每个顶点对应一个链表
    private int vertices; // 顶点数
    private int edges; // 边数
    // 新建一个链表
    ArrayList<edgeNode<T>> adjList;
    // 构造函数
    public AdjListGraph(int vers) {
        this.adjList = new ArrayList<edgeNode<T>>(vers);
        // 初始化所有顶点
        for (int i = 0; i < vers; i++) {
            adjList.add(null);
        }
        this.vertices = vers;
        this.edges = 0;
    }

    /**
     * 插入边
     * @param x 起点
     * @param y 终点
     * @param w 权重
     */
    @Override
    public void insert(int x, int y, T w) {
        // 判断是否超出边界
        if (x < 0 || x >= vertices || y < 0 || y >= vertices) {
            throw new IllegalArgumentException("编号越界");
        }
        // 插入边
        edgeNode<T> newNode = new edgeNode<> (y, w, adjList.get(x));
        adjList.set(x, newNode);
        edges++;
    }

    /**
     * 删除边
     * @param x 起点
     * @param y 终点
     */
    @Override
    public void remove(int x, int y) {
        // 判断是否越界
        if (x < 0 || x >= vertices || y < 0 || y >= vertices ){
            throw new IllegalArgumentException("编号越界");
        }
        // 删除边（遍历：直到）
        edgeNode<T> prev = null;
        edgeNode<T> curr = adjList.get(x);
        while (curr != null) {
            if (curr.end == y) {
                if (prev == null) {
                    adjList.set(x, curr.next);
                } else {
                    prev.next = curr.next;
                }
                edges--;
                return;
            }
            prev = curr;
            curr = curr.next;
        }
    }

    /**
     * 判断边是否存在
     * @param x 起点
     * @param y 终点
     * @return 是否存在
     */
    @Override
    public boolean exist(int x, int y) {
        // 判断是否越界
        if (x < 0 || x >= vertices || y < 0 || y >= vertices ){
            throw new IllegalArgumentException("编号越界");
        }
        // 遍历链表
        edgeNode<T> curr = adjList.get(x);
        while (curr != null) {
            if (curr.end == y) {
                return true;
            }
            curr = curr.next;
        }
        return false;
    }

    /**
     * 获取顶点数
     * @return 顶点数
     */
    @Override
    public int NumOfVer() {
        return vertices;
    }

    /**
     * 获取边数
     * @return 边数
     */
    @Override
    public int NumOfEdges() {
        return edges;
    }
}
