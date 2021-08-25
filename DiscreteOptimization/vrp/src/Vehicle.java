import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.core.api.domain.entity.PlanningEntity;

@XStreamAlias("VrpVehicle")
public class Vehicle {
    private final int capacity;
    private final Location depot;
    private Location nextCustomer;

    public Vehicle(int capacity, Location depot) {
        this.capacity = capacity;
        this.depot = depot;
    }

    public int getCapacity() {
        return capacity;
    }

    public Location getDepot() {
        return depot;
    }

    public Location getNextCustomer() {
        return nextCustomer;
    }

    public void setNextCustomer(Location nextCustomer) {
        this.nextCustomer = nextCustomer;
    }
}
