public class Seam {
    private final PictureDAG dag;
    private final double[][] energy;
    private double[] distTo;
    private int[] edgeTo;

    public Seam(PictureDAG dag, double[][] energy) {
        this.dag = dag;
        this.energy = energy;
        distTo = new double[dag.v()];
        edgeTo = new int[dag.v()];
        for (int i = 0; i < dag.v(); i++)
            distTo[i] = Double.POSITIVE_INFINITY;

        for (int r: dag.roots())
            distTo[r] = energy[dag.x(r)][dag.y(r)];

        for (int v: dag) {
            for (int w: dag.adj(v)) {
                relax(v, w);
            }
        }
    }

    private void relax(int v, int w) {
        if (distTo[w] > distTo[v] + weight(w)) {
            distTo[w] = distTo[v] + weight(w);
            edgeTo[w] = v;
        }
    }

    private double weight(int v) {
        return energy[dag.x(v)][dag.y(v)];
    }

    public int[] path() {
        int sink = -1;
        double lowest = Double.POSITIVE_INFINITY;
        for (int i: dag.sinks()) {
            if (distTo[i] < lowest) {
                sink = i;
                lowest = distTo[i];
            }
        }

        int[] result = new int[dag.seamLength()];
        int v = sink;
        for (int i = dag.seamLength() - 1; i >= 0; i--) {
            result[i] = v;
            v = edgeTo[v];
        }

        return result;
    }
}
