import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;
import edu.princeton.cs.algs4.StdOut;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

public class FacilitySolver {

    private final static boolean SUBMISSION = false;

    int numFacilities;
    int numCustomers;

    double[] setupCost;
    int[] capacity;
    int[] demand;
    double[][] dist;

    int[] currentSolution;
    int[] capacityLeft;
    double[] customerCost;

    private FacilitySolver(String filename) {
        readFile(filename);
    }

    private void solveChoco() {
        Model model = new Model("FacilityLocation");

        // Max cost bound
        final int MAX_TOTALCOST = 100000000;
        final int MAX_COST = 1000000;
        final int MAX_CAPACITY = 100000;
        int maxCustCost = 0;
        int maxSumCustCost = 0;

        // Convert distances to an integer array per customer
        int[][] custDist = new int[numCustomers][numFacilities];
        for (int i = 0; i < numCustomers; i++) {
            for (int j = 0; j < numFacilities; j++) {
                custDist[i][j] = (int) Math.round(dist[j][i]);
                if (custDist[i][j] > maxCustCost)
                    maxCustCost = custDist[i][j];
                maxSumCustCost += custDist[i][j];
            }
        }

        // Define variables
        IntVar[] custFac = model.intVarArray(numCustomers, 0, numFacilities, false);
        IntVar[] numCustPerFac = model.intVarArray(numFacilities, 0, numCustomers, true);
        IntVar[] capUsed = model.intVarArray(numFacilities, 0, MAX_CAPACITY, true);
        BoolVar[] facOpen = model.boolVarArray(numFacilities);
        IntVar[] facilityCost = model.intVarArray(numFacilities, 0, maxCustCost, true);
        IntVar[] customerCost = model.intVarArray(numCustomers, 0, maxCustCost, true);

        // The customers are each assigned to a facility and the capacity used at each facility is the sum of
        // the customers demand
        model.binPacking(custFac, demand, capUsed, 0).post();

        for (int i = 0; i < numFacilities; i++) {
            // A facility is open if at least one customer uses it
            model.among(numCustPerFac[i], custFac, new int[] {i}).post();
            facOpen[i] = model.intGeView(numCustPerFac[i], 1);
            // The cost of a facility is zero if closed, otherwise the specified cost
            model.times(facOpen[i], (int)Math.round(setupCost[i]), facilityCost[i]).post();
            // The capacity used (total demand of assigned customers) may not exceed the facility's capacity
            model.arithm(capUsed[i], "<=", capacity[i]).post();
        }

        // The cost per customer is the distance between the customer and the facility
        for (int i = 0; i < numCustomers; i++) {
            model.element(customerCost[i], custDist[i], custFac[i]).post();
        }

        // The total cost is the sum of costs per facility and per customer
        IntVar sumFacilities = model.intVar(0, MAX_TOTALCOST);
        model.sum(facilityCost, "=", sumFacilities).post();
        IntVar sumCustomers = model.intVar(0, maxSumCustCost);
        model.sum(customerCost, "=", sumCustomers).post();
        IntVar totalCost = model.intVar(0, MAX_TOTALCOST);
        model.arithm(sumFacilities, "+", sumCustomers, "=", totalCost).post();

        // Set the objective
        model.setObjective(Model.MINIMIZE, totalCost);

        // Find solutions
        org.chocosolver.solver.Solver solver = model.getSolver();

        // Solve and give feedback
        if (SUBMISSION)
            solver.limitTime("1h");
        else
            solver.limitTime("30s");
        while (solver.solve()) {
            StdOut.printf("Total: %d; Facilities: %d; Customers: %d\n", totalCost.getValue(), sumFacilities.getValue(), sumCustomers.getValue());
            for (int i = 0; i < numCustomers; i++) {
                StdOut.print(custFac[i].getValue());
                StdOut.print(" ");
            }
            StdOut.println();
            for (int i = 0; i < numCustomers; i++) {
                StdOut.print(customerCost[i].getValue());
                StdOut.print(" ");
            }
            StdOut.println();
            for (int i = 0; i < numFacilities; i++) {
                StdOut.print(facilityCost[i].getValue());
                StdOut.print(" ");
            }
            StdOut.println();
        }
    }

    private void solveGreedy() {
        capacityLeft = capacity.clone();
        customerCost = new double[numCustomers];
        Arrays.fill(customerCost, 0.0);

        for (int i = 0; i < numCustomers; i++) {
            int closestFacility = 0;
            for (int j = 0; j < numFacilities; j++) {
                if (capacityLeft[j] >= demand[i] && dist[j][i] < customerCost[i]) {

                }
            }
        }
    }

    private void solveOpta() {
        Facility[] facilities = new Facility[numFacilities];
        for (int i = 0; i < numFacilities; i++) {
            facilities[i] = new Facility(i, capacity[i], setupCost[i]);
        }
        Customer[] customers = new Customer[numCustomers];
        for (int i = 0; i < numCustomers; i++) {
            Map<Facility, Double> distances = new HashMap<>();
            for (int j = 0; j < numFacilities; j++) {
                distances.put(facilities[j], dist[j][i]);
            }
            customers[i] = new Customer(i, demand[i], distances);
        }
        OptaProblem unsolved = new OptaProblem(Arrays.asList(facilities), Arrays.asList(customers));

        SolverFactory<OptaProblem> solverFactory = SolverFactory.createFromXmlResource(
                "optaSolverConfig.xml");
        Solver<OptaProblem> solver = solverFactory.buildSolver();

        OptaProblem solved = solver.solve(unsolved);

        StdOut.println(solved);
    }

    private void readFile(String filename) {
        In in = new In(filename);
        numFacilities = in.readInt();
        numCustomers = in.readInt();
        in.readLine();

        setupCost = new double[numFacilities];
        capacity = new int[numFacilities];
        demand = new int[numCustomers];

        double[][] facilityLocations = new double[numFacilities][2];
        double[][] customerLocations = new double[numCustomers][2];

        for (int i = 0; i < numFacilities; i++) {
            setupCost[i] = in.readDouble();
            capacity[i] = in.readInt();
            facilityLocations[i][0] = in.readDouble();
            facilityLocations[i][1] = in.readDouble();
            in.readLine();
        }

        for (int i = 0; i < numCustomers; i++) {
            demand[i] = in.readInt();
            customerLocations[i][0] = in.readDouble();
            customerLocations[i][1] = in.readDouble();
            in.readLine();
        }

        dist = new double[numFacilities][numCustomers];
        for (int i = 0; i < numFacilities; i++) {
            for (int j = 0; j < numCustomers; j++) {
                double distX = facilityLocations[i][0] - customerLocations[j][0];
                double distY = facilityLocations[i][1] - customerLocations[j][1];
                dist[i][j] = Math.sqrt( distX * distX + distY * distY );
            }
        }
    }


    private void writeMinizincDataFile(String filename) {
        Out out = new Out(filename);
        out.println("numFacilities=" + numFacilities + ";");
        out.println("numCustomers=" + numCustomers + ";");

        out.print("capacity=[");
        for (int i = 0; i < numFacilities; i++) {
            out.print(capacity[i]);
            if (i < numFacilities - 1)
                out.print(", ");
        }
        out.println("];");

        out.print("setupCost=[");
        for (int i = 0; i < numFacilities; i++) {
            out.print(setupCost[i]);
            if (i < numFacilities - 1)
                out.print(", ");
        }
        out.println("];");

        out.print("demand=[");
        for (int i = 0; i < numCustomers; i++) {
            out.print(demand[i]);
            if (i < numCustomers - 1)
                out.print(", ");
        }
        out.println("];");

        out.print("distance=[");
        for (int i = 0; i < numFacilities; i++) {
            out.print("|");
            for (int j = 0; j < numCustomers; j++) {
                out.print(dist[i][j]);
                if (i < numFacilities - 1 || j < numCustomers - 1)
                    out.print(", ");
            }
            out.println();
        }
        out.println(" |];");

        out.close();
    }


    public static void main(String[] args) throws FileNotFoundException {
        System.setErr(new PrintStream("errorlog.txt"));
        FacilitySolver solver = new FacilitySolver(args[0]);
        solver.solveOpta();
//        String outputFilename = args[0] + ".dzn";
//        solver.writeMinizincDataFile(outputFilename);
//        solver.solveChoco();
/*
        for (int i = 0; i < solver.numFacilities; i++) {
            StdOut.printf("%5d; %5d\n", solver.setupCost[i], solver.capacity[i]);
        }
        for (int i = 0; i < solver.numCustomers; i++) {
            StdOut.printf("%5d\n", solver.demand[i]);
        }
        for (int i = 0; i < solver.numFacilities; i++) {
            for (int j = 0; j < solver.numCustomers; j++) {
                StdOut.printf("%5.2f; ", solver.dist[i][j]);
            }
            StdOut.println();
        }
*/
    }
}
