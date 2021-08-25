import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.persistence.xstream.api.score.buildin.hardsoftlong.HardSoftLongScoreXStreamConverter;

import java.util.List;

@PlanningSolution
@XStreamAlias("VrpVehicleRoutingSolution")
public class VrpSolution {
    private Location depot;
    private List<Location> locations;
    private List<Vehicle> vehicles;

    @XStreamConverter(HardSoftLongScoreXStreamConverter.class)
    protected HardSoftLongScore score;

    @ValueRangeProvider(id = "depotRange")
    @ProblemFactCollectionProperty
    public Location[] getDepots() {
        return new Location[] {depot};
    }

    public Location getDepot() {
        return depot;
    }

    public void setDepot(Location depot) {
        this.depot = depot;
    }

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "locationRange")
    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    @PlanningScore
    public HardSoftLongScore getScore() {
        return score;
    }

    public void setScore(HardSoftLongScore score) {
        this.score = score;
    }
}
