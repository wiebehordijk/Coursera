import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VrpEasyScoreCalculator implements EasyScoreCalculator<VrpSolution> {
    @Override
    public Score calculateScore(VrpSolution solution) {
        Location depot = solution.getDepot();
        List<Location> locationList = solution.getLocations();
        List<Vehicle> vehicleList = solution.getVehicles();
        Map<Vehicle, Integer> vehicleDemandMap = new HashMap<>(vehicleList.size());
        for (Vehicle vehicle : vehicleList) {
            vehicleDemandMap.put(vehicle, 0);
        }
        long hardScore = 0L;
        long softScore = 0L;
        for (Location location : locationList) {
            Location previous = location.getPrevious();
            if (previous != null) {
                Vehicle vehicle = location.getVehicle();
                vehicleDemandMap.put(vehicle, vehicleDemandMap.get(vehicle) + location.getDemand());
                // Score constraint distanceToPreviousStandstill
                softScore -= Math.round(location.getDistanceFromPrevious());
                if (location.getNext() == null) {
                    // Score constraint distanceFromLastCustomerToDepot
                    softScore -= Math.round(location.distanceTo(depot));
                }
            }
            else {
                hardScore -= location.getDemand();
            }
        }
        for (Map.Entry<Vehicle, Integer> entry : vehicleDemandMap.entrySet()) {
            int capacity = entry.getKey().getCapacity();
            int demand = entry.getValue();
            if (demand > capacity) {
                // Score constraint vehicleCapacity
                hardScore -= (demand - capacity);
            }
        }
        // Score constraint arrivalAfterDueTimeAtDepot is a built-in hard constraint in VehicleRoutingImporter
        return HardSoftLongScore.of(hardScore, softScore);

    }
}
