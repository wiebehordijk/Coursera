import org.optaplanner.core.api.domain.autodiscover.AutoDiscoverMemberType;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;

import java.util.List;

@PlanningSolution(autoDiscoverMemberType = AutoDiscoverMemberType.GETTER)
public class OptaProblem {
    private List<Facility> facilities;
    private List<Customer> customers;
    private HardSoftLongScore score;

    public OptaProblem() {
    }

    public OptaProblem(List<Facility> facilities, List<Customer> customers) {
        this.facilities = facilities;
        this.customers = customers;
    }

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "facilityRange")
    public List<Facility> getFacilities() {
        return facilities;
    }

    @PlanningEntityCollectionProperty
    public List<Customer> getCustomers() {
        return customers;
    }

    @PlanningScore
    public HardSoftLongScore getScore() {
        return score;
    }

    public void setScore(HardSoftLongScore score) {
        this.score = score;
    }

    public String toString() {
        FacilityEasyScoreCalculator calculator = new FacilityEasyScoreCalculator();
        StringBuilder builder = new StringBuilder();
        if (score.getHardScore() < 0) {
            builder.append("No feasible solution found. Hardscore: " + score.getHardScore() + "\n");
        }
        builder.append(calculator.totalCost(this) + " 0\n");
        for (Customer c : customers) {
            if (c.getFacility() == null)
                builder.append("!X! ");
            else
                builder.append(c.getFacility().getIndex() + " ");
        }
        builder.append("\n");
        return builder.toString();
    }
}
