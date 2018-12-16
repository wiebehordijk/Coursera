import edu.princeton.cs.algs4.*;

public class DfsPaths {
    boolean[] marked;
    int[] edgeTo;
    int s;

    public DfsPaths(Graph G, int s) {
        marked = new boolean[G.V()];
        edgeTo = new int[G.V()];
        this.s = s;
        dfs(G, s);
    }

    private void dfs(Graph G, int v) {
        marked[v] = true;
        for (int w: G.adj(v))
            if (!marked[w])
                {
                    edgeTo[w] = v;
                    dfs(G, w);
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
        S.push(s);
        return S;
    }
}
