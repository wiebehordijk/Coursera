import edu.princeton.cs.algs4.*;

public class BfsPaths {

    private int edgeTo[];
    private int distTo[];
    private boolean marked[];
    private int s;

    public BfsPaths(Graph G, int s) {
        this.s = s;
        edgeTo = new int[G.V()];
        distTo = new int[G.V()];
        marked = new boolean[G.V()];
        bfs(G);
    }

    private void bfs(Graph G) {
        Queue<Integer> Q = new Queue<>();
        Q.enqueue(s);

        while (!Q.isEmpty()) {
            int v = Q.dequeue();
            for (int w: G.adj(v))
                if (!marked[w])
                    {
                        Q.enqueue(w);
                        edgeTo[w] = v;
                        distTo[w] = distTo[v] + 1;
                        marked[w] = true;
                    }
        }
    }

    public boolean hasPathTo(int v) {
        return marked[v];
    }

    public Iterable<Integer> pathTo(int v) {
        if (!marked[v]) return null;
        Stack S = new Stack();
        for (int x = v; x != s; x = edgeTo[x]) {
            S.push(x);
        }
        return S;
    }
}
