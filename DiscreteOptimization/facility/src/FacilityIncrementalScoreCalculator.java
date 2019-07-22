import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.impl.score.director.incremental.AbstractIncrementalScoreCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FacilityIncrementalScoreCalculator extends AbstractIncrementalScoreCalculator<OptaProblem> {
    protected final transient Logger logger = LoggerFactory.getLogger(this.getClass());
    private double customerCost;
    private double facilityCost;
    private long unassignedDemand;
    private long missingCapacity;
    private boolean[] open;
    private OptaProblem problem;

    @Override
    public void resetWorkingSolution(OptaProblem problem) {
        this.problem = problem;

        customerCost = 0.0;
        facilityCost = 0.0;
        unassignedDemand = 0L;
        missingCapacity = 0L;
        open = new boolean[problem.getFacilities().size()];

        for (Customer c : problem.getCustomers()) {
            if (c.getFacility() != null) {
                Facility f = c.getFacility();
                open[f.getIndex()] = true;
            }
            customerCost += c.cost();
            unassignedDemand += c.getUnassignedDemand();
        }
        for (Facility f : problem.getFacilities()) {
            if (f.getCapacityLeft() < 0)
                missingCapacity -= f.getCapacityLeft();
            if (open[f.getIndex()])
                facilityCost += f.getSetupCost();
        }

//        checkMyself();
        logger.debug("Reset working solution done");
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
        customerCost -= c.cost();
        unassignedDemand -= c.getUnassignedDemand();
    }

    private void closeFacility(Facility f) {
        if (open[f.getIndex()] && f.getNumCustomers() <= 1) {
            open[f.getIndex()] = false;
            facilityCost -= f.getSetupCost();
        }
    }

    private void insertCustomer(Customer c) {
        if (c.getFacility() != null) {
            Facility f = c.getFacility();
            openFacility(f);
            if (f.getCapacityLeft() < 0)
                missingCapacity += Math.min(c.getDemand(), -f.getCapacityLeft());
        }
        customerCost += c.cost();
        unassignedDemand += c.getUnassignedDemand();
//        checkMyself();
    }

    private void openFacility(Facility f) {
        if (!open[f.getIndex()] && f.getNumCustomers() >= 1) {
            open[f.getIndex()] = true;
            facilityCost += f.getSetupCost();
        }
    }

    private void checkMyself() {
        FacilityEasyScoreCalculator easy = new FacilityEasyScoreCalculator();
        double realCost = easy.totalCost(problem);
        if (Math.abs(customerCost - realCost) > 0.1) {
            throw new IllegalStateException("Total cost: " + (customerCost ) + ", Real cost: " + realCost);
        }
    }

    private double computeFacilityCost() {
        double total = 0.0;
        for (Facility f : problem.getFacilities())
            if (open[f.getIndex()])
                total += f.getSetupCost();
        return total;
    }

    @Override
    public Score calculateScore() {
        return HardSoftLongScore.of(-unassignedDemand-missingCapacity, Math.round(-customerCost - facilityCost ));
    }
}
