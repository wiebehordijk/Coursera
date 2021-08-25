import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.AnchorShadowVariable;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;

@XStreamAlias("VrpLocation")
@PlanningEntity
public class Location {
    private double x, y;
    private int demand;

    private Location previous;
    private Location next;

    private Location() {}

    @InverseRelationShadowVariable(sourceVariableName = "previous")
    public Location getNext() {
        return next;
    }

    public void setNext(Location next) {
        this.next = next;
    }

    private Vehicle vehicle;

    @AnchorShadowVariable(sourceVariableName = "previous")
    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    @PlanningVariable(valueRangeProviderRefs = {"depotRange", "locationRange"}, graphType = PlanningVariableGraphType.CHAINED)
    public Location getPrevious() {
        return previous;
    }

    public void setPrevious(Location previous) {
        this.previous = previous;
    }

    public Location(double x, double y, int demand) {
        this.x = x;
        this.y = y;
        this.demand = demand;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getDemand() {
        return demand;
    }

    public double distanceTo(Location other) {
        double dx = other.x - this.x;
        double dy = other.y - this.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double getDistanceFromPrevious() {
        if (previous == null) {
            throw new IllegalStateException("This method must not be called when the previousStandstill ("
                    + previous + ") is not initialized yet.");
        }
        return distanceTo(previous);
    }


}
