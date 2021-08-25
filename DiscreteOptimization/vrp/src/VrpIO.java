import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.List;

public class VrpIO {

    public static VrpSolution ReadFile(String filename) {
        In in = new In(filename);
        int numCustomers = in.readInt();
        int numVehicles = in.readInt();
        int capacity = in.readInt();
        in.readLine();

        VrpSolution solution = new VrpSolution();
        Location depot = readLocation(in);
        solution.setDepot(depot);

        List<Location> locations = new ArrayList<>();
        for (int i = 1; i < numCustomers; i++) {
            locations.add(readLocation(in));
        }
        solution.setLocations(locations);

        List<Vehicle> vehicles = new ArrayList<>();
        for (int i = 0; i < numVehicles; i++) {
            vehicles.add(new Vehicle(capacity, depot));
        }
        solution.setVehicles(vehicles);

        return solution;
    }


    private static Location readLocation(In in) {
        int demand = in.readInt();
        double x = in.readDouble();
        double y = in.readDouble();
        in.readLine();
        return new Location(x, y, demand);
    }

}
