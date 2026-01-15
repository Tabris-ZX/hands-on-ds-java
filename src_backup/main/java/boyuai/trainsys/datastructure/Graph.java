package boyuai.trainsys.datastructure;


public interface Graph<T> {
    void insert(int x, int y,  T w);
    void remove(int x, int y);
    boolean exist(int x, int y);
    int NumOfVer();
    int NumOfEdges();
}
