import edu.princeton.cs.algs4.*;

public class Test {

    public static void main(String args[]) {
        In in = new In("tinyG.txt");
        Graph graph = new Graph(in);

        DfsPaths dp = new DfsPaths(graph, 0);

        for (int v = 0; v < graph.V(); v++) {
            if (dp.hasPathTo(v))
                System.out.println(v + ": " + dp.pathTo(v).toString());
        }

        BfsPaths bp = new BfsPaths(graph, 0);

        for (int v = 0; v < graph.V(); v++) {
            if (bp.hasPathTo(v))
                System.out.println(v + ": " + bp.pathTo(v).toString());
        }

    }

}
