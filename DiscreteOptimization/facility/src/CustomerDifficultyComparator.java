import java.util.Comparator;

public class CustomerDifficultyComparator implements Comparator<Customer> {
    @Override
    public int compare(Customer o1, Customer o2) {
        return o1.getDemand() - o2.getDemand();
    }
}
