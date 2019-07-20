import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.impl.score.director.incremental.AbstractIncrementalScoreCalculator;

public class FacilityIncrementalScoreCalculator extends AbstractIncrementalScoreCalculator<OptaProblem> {
    private double totalCost;
    private long unassignedDemand;
    private long missingCapacity;
    private boolean[] open;

    @Override
    public void resetWorkingSolution(OptaProblem problem) {
        totalCost = 0.0;
        unassignedDemand = 0L;
        missingCapacity = 0L;
        open = new boolean[problem.getFacilities().size()];

        for (Customer c : problem.getCustomers()) {
            insertCustomer(c);
        }
    }

    @Override
    public void beforeEntityAdded(Object o) {

    }

    @Override
    public void afterEntityAdded(Object o) {
        Customer c = (Customer) o;
        insertCustomer(c);
    }

    @Override
    public void beforeVariableChanged(Object o, String s) {
        Customer c = (Customer) o;
        retractCustomer(c);
    }

    @Override
    public void afterVariableChanged(Object o, String s) {
        Customer c = (Customer) o;
        insertCustomer(c);
    }

    @Override
    public void beforeEntityRemoved(Object o) {
        Customer c = (Customer) o;
        retractCustomer(c);
    }

    @Override
    public void afterEntityRemoved(Object o) {

    }

    private void retractCustomer(Customer c) {
        if (c.getFacility() != null) {
            Facility f = c.getFacility();
            closeFacility(f);
            if (f.getCapacityLeft() < 0)
                missingCapacity -= Math.min(c.getDemand(), -f.getCapacityLeft());
        }
        totalCost -= c.cost();
        unassignedDemand -= c.getUnassignedDemand();
    }

    private void closeFacility(Facility f) {
        if (open[f.getIndex()] && f.getNumCustomers() <= 1) {
            totalCost -= f.getSetupCost();
            open[f.getIndex()] = false;
        }
    }

    private void insertCustomer(Customer c) {
        if (c.getFacility() != null) {
            Facility f = c.getFacility();
            openFacility(f);
            if (f.getCapacityLeft() < 0)
                missingCapacity += Math.min(c.getDemand(), -f.getCapacityLeft());
        }
        totalCost += c.cost();
        unassignedDemand += c.getUnassignedDemand();
    }

    private void openFacility(Facility f) {
        if (!open[f.getIndex()] && f.getNumCustomers() >= 1) {
            totalCost += f.getSetupCost();
            open[f.getIndex()] = true;
        }
    }

    @Override
    public Score calculateScore() {
        return HardSoftLongScore.of(-unassignedDemand-missingCapacity, Math.round(-totalCost));
    }
}
