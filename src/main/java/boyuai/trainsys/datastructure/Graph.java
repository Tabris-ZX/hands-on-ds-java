package boyuai.trainsys.datastructure;


public interface Graph<T> {
    public void insert(int x, int y,  T w);
    public void remove(int x, int y);
    public boolean exist(int x, int y);
    public int NumOfVer();
    public int NumOfEdges();
}
