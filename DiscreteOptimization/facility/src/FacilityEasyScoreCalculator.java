import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;

import java.util.HashSet;
import java.util.Set;

public class FacilityEasyScoreCalculator implements EasyScoreCalculator<OptaProblem> {
    @Override
    public Score calculateScore(OptaProblem optaProblem) {
        long hardScore = 0;
        for (Facility f : optaProblem.getFacilities()) {
            if (f.getCapacityLeft() < 0)
                hardScore += f.getCapacityLeft();
        }
        for (Customer c : optaProblem.getCustomers()) {
            hardScore -= c.getUnassignedDemand();
        }

        long softScore = 0L;
        softScore = Math.round(-totalCost(optaProblem));

        return HardSoftLongScore.of(hardScore, softScore);
    }

    public double totalCost(OptaProblem optaProblem) {
        double totalCost = 0.0;
        boolean[] openFacilities = new boolean[optaProblem.getFacilities().size()];
        for (Customer c : optaProblem.getCustomers()) {
            totalCost += c.cost();
//            if (c.getFacility() != null && !openFacilities[c.getFacility().getIndex()]) {
//                openFacilities[c.getFacility().getIndex()] = true;
//                totalCost += c.getFacility().getSetupCost();
//            }
        }
        for (Facility f : optaProblem.getFacilities()) {
            totalCost += f.cost();
        }
        return totalCost;
    }
}
