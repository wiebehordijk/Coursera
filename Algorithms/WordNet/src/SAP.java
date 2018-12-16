import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class SAP {

    private final Digraph G;
    private int commonAncestor;
    private int distance;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null)
            throw new IllegalArgumentException("G is null");
        this.G = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        return length(Arrays.asList(v), Arrays.asList(w));
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        return ancestor(Arrays.asList(v), Arrays.asList(w));
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (findCommonAncestor(v, w)) {
            return distance;
        }
        else
            return -1;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (findCommonAncestor(v, w))
            return commonAncestor;
        else
            return -1;
    }

    private boolean findCommonAncestor(Iterable<Integer> v, Iterable<Integer> w) {
        BreadthFirstDirectedPaths vPaths = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths wPaths = new BreadthFirstDirectedPaths(G, w);

        boolean found = false;
        for (int i = 0; i < G.V(); i++) {
            if (vPaths.hasPathTo(i) && wPaths.hasPathTo(i)) {
                int length = vPaths.distTo(i) + wPaths.distTo(i);
                if (!found) {
                    found = true;
                    commonAncestor = i;
                    distance = length;
                }
                else if (length < distance) {
                    commonAncestor = i;
                    distance = length;
                }
            }
        }
        return found;
    }


    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}