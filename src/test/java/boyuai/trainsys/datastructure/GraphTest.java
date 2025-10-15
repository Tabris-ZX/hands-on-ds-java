package boyuai.trainsys.datastructure;

public class GraphTest {
    public static void main(String[] args) {
        AdjListGraph<Integer> graph = new AdjListGraph<>(5);
        graph.insert(0, 1, 10);
        graph.insert(0, 2, 20);
        graph.insert(1, 2, 30);
        graph.insert(2, 0, 40);
        graph.insert(2, 3, 50);
        graph.insert(3, 3, 60);

        System.out.println("Number of vertices: " + graph.NumOfVer());
        System.out.println("Number of edges: " + graph.NumOfEdges());

        System.out.println("Edge from 0 to 1 exists: " + graph.exist(0, 1));
        System.out.println("Edge from 1 to 3 exists: " + graph.exist(1, 3));

        graph.remove(2, 3);
        System.out.println("Edge from 2 to 3 exists after removal: " + graph.exist(2, 3));
        System.out.println("Number of edges after removal: " + graph.NumOfEdges());
    }
}
