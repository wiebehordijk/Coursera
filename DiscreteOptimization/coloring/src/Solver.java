import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.selectors.variables.VariableSelectorWithTies;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.variables.IntVar;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Solver {

    private final static boolean SUBMISSION = true;
    private static Graph graph;
    private static int[] colors;
    private static int numColors;
    private static boolean isOptimal;

    public static void main(String[] args) {
        String filename = args[0];
        In in = new In(filename);
        graph = new Graph(in);
        colors = new int[graph.V()];

        SolveChoco2();

        PrintSolution();
    }

    private static void PrintSolution() {
        StdOut.print(numColors);
        if (isOptimal)
            StdOut.println(" 1");
        else
            StdOut.println(" 0");

        for (int i = 0; i < graph.V(); i++) {
            StdOut.print(colors[i] + " ");
        }
        StdOut.println();
    }

    private static int maxDegree(Graph g) {
        int result = 0;
        for (int v = 0; v < g.V(); v++)
            if (g.degree(v) > result)
                result = g.degree(v);

        return result;
    }

    private static IntVar[] OrderByDegree(Graph g, IntVar[] vars) {
        Integer[] s = IntStream.range(0, graph.V()).boxed().toArray(Integer[]::new);
        Arrays.sort(s, (x, y) -> (graph.degree(y) - graph.degree(x)));
        IntVar[] result = new IntVar[vars.length];
        for (int i = 0; i < vars.length; i++) {
            result[i] = vars[s[i]];
        }
        return result;
    }

    private static void SolveChoco2() {
        Model model = new Model("Graph coloring");
        int maxX = maxDegree(graph) + 1;

        // Define variables
        IntVar[] vertices = model.intVarArray(graph.V(), 0, maxX, false);
        IntVar maxColor = model.intVar(1, maxX);
        model.max(maxColor, vertices).post();

        // Constraints from the graph
        for (int v = 0; v < graph.V(); v++) {
            for (int w: graph.adj(v)) {
                if (w > v)
                    model.arithm(vertices[v], "!=", vertices[w]).post();
            }
        }

        // Break symmetry with extra constraints: node with highest degree must get color 0, etc
        IntVar[] ordered = OrderByDegree(graph, vertices);
        for (int i = 0; i < graph.V(); i++) {
            IntVar v = ordered[i];
            model.arithm(v, "<=", i).post();
        }

        // Set objective and search strategy
        model.setObjective(Model.MINIMIZE, maxColor);
        org.chocosolver.solver.Solver solver = model.getSolver();
        solver.setSearch(Search.intVarSearch(
                new FirstFail(model),
                new IntDomainMin(),
                ordered
        ));

        // Solve and give feedback
        if (SUBMISSION)
            solver.limitTime("1h");
        else
            solver.limitTime("10m");
        while (solver.solve()) {
            for (int i = 0; i < graph.V(); i++) {
                colors[i] = vertices[i].getValue();
            }
            numColors = maxColor.getValue() + 1;
            if (!SUBMISSION)
                PrintSolution();
        }
        isOptimal = solver.isObjectiveOptimal();
        if (!SUBMISSION)
            solver.printStatistics();
    }


    private static void SolveGreedy() {
        numColors = 0;
        boolean[] assigned = new boolean[graph.V()];
        colors[0] = 0;
        assigned[0] = true;

        for (int v = 1; v < graph.V(); v++) {
            boolean adjacentColors[] = new boolean[graph.V()];
            for (int w : graph.adj(v)) {
                if (assigned[w])
                    adjacentColors[colors[w]] = true;
            }
            int c = 0;
            for (; adjacentColors[c]; c++);
            colors[v] = c;
            assigned[v] = true;
            if (c + 1 > numColors)
                numColors = c + 1;
        }
    }

}
