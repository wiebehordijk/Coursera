import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.util.Map;
import java.util.Objects;

@PlanningEntity()
public class Customer {
    private int index;
    private int demand;
    private Map<Facility, Double> distance;
    private Facility facility;

    public int getDemand() {
        return demand;
    }

    @PlanningVariable(valueRangeProviderRefs = {"facilityRange"})
    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        if (this.facility != null)
            this.facility.removeCustomer(this);
        this.facility = facility;
        if (facility != null)
            facility.addCustomer(this);
    }

    public double cost() {
        if (facility == null)
            return 0.0;
        return distance.get(facility);
    }

    public Customer() {

    }

    public Customer(int index, int demand, Map<Facility, Double> distance) {
        this.index = index;
        this.demand = demand;
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return index == customer.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }
}
