import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Facility {
    private final int index;
    private final int capacity;
    private final double setupCost;
    private int capacityLeft;
    private Set<Customer> customers = new HashSet<>();

    public void addCustomer(Customer customer) {
        if (customers.contains(customer))
            throw new IllegalArgumentException("Customer is already a customer at this facility");
        customers.add(customer);
        capacityLeft -= customer.getDemand();
    }

    public void removeCustomer(Customer customer) {
        if (!customers.contains(customer))
            throw new IllegalArgumentException("Customer is not a customer at this facility");
        customers.remove(customer);
        capacityLeft += customer.getDemand();
    }

    public int getCapacityLeft() {
        return capacityLeft;
    }

    public Facility(int index, int capacity, double setupCost) {
        this.index = index;
        this.capacity = capacity;
        this.capacityLeft = capacity;
        this.setupCost = setupCost;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Facility facility = (Facility) o;
        return index == facility.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }

    public double getSetupCost() {
        return setupCost;
    }

    public double cost() {
        if (customers.isEmpty())
            return 0.0;
        else
            return setupCost;
    }
}
