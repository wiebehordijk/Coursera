import edu.princeton.cs.algs4.StdOut;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;

import java.util.Arrays;

public class VrpSolver {

    public static void main(String[] args) {
        VrpSolution unsolved = VrpIO.ReadFile(args[0]);

        SolverFactory<VrpSolution> solverFactory = SolverFactory.createFromXmlResource(
                "vrpSolverConfig.xml");
        Solver<VrpSolution> solver = solverFactory.buildSolver();

        VrpSolution solved = solver.solve(unsolved);

        StdOut.println(solved);


    }
}
