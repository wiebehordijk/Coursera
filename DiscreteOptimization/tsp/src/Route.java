import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.Iterator;

public class Route implements Iterable<Integer> {

    private final int[] route;
    private final TspSpace space;
    private final double distance;
    private final int hashcode;

    public Route(int[] theRoute, TspSpace theSpace) {
        route = theRoute.clone();
        space = theSpace;
        distance = computeLength();
        hashcode = Arrays.hashCode(route);
    }

    private Route(int[] theRoute, TspSpace theSpace, double theDistance) {
        route = theRoute.clone();
        space = theSpace;
        distance = theDistance;
        hashcode = Arrays.hashCode(route);
    }

    private double computeLength() {
        double total = 0.0;
        for (int i = 0; i < route.length - 1; i++) {
            total += space.dist(route[i], route[i + 1]);
        }
        total += space.dist(route[route.length - 1], route[0]);
        return total;
    }

    public int length() {
        return route.length;
    }

    public double distance() {
        return distance;
    }

    public int item(int i) {
        return route[i];
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route integers = (Route) o;
        return Arrays.equals(route, integers.route) &&
                space.equals(integers.space);
    }

    @Override
    public int hashCode() {
        return hashcode;
    }

    public Route reverse(int a, int b) {
        if (a < 0 || a >= route.length || b < 0 || b >= route.length || a >= b)
            throw new IllegalArgumentException();

        int[] swapped = route.clone();
        for (int i = b, j = a; i >= a; i--, j++) {
            swapped[j] = route[i];
        }

        return new Route(swapped, space, distanceReversed(a, b));
    }


    public double distanceReversed(int a, int b) {
        int previous = (a == 0 ? route.length - 1 : a - 1);
        int next = (b == route.length - 1 ? 0 : b + 1);

        if (a == 0 && b == route.length - 1)
            return distance;
        else
            return distance
                    - space.dist(route[previous], route[a])
                    - space.dist(route[b], route[next])
                    + space.dist(route[previous], route[b])
                    + space.dist(route[a], route[next]);
    }


    public Route swap(int a, int b, int c) {
        if (a < 0 || a >= route.length
                || b < 0 || b >= route.length || a >= b
                || c < 0 || c > route.length || b >= c)
            throw new IllegalArgumentException();

        int[] swapped = route.clone();
        System.arraycopy(route, a, swapped, a + c - b, b - a);
        System.arraycopy(route, b, swapped, a, c - b);

        return new Route(swapped, space, distance - improvementSwapped(a, b, c));
    }


    public double improvementSwapped(int i, int j, int k) {
        int a = route[i == 0 ? route.length - 1 : i - 1];
        int b = route[i];
        int c = route[j - 1];
        int d = route[j];
        int e = route[k - 1];
        int f = route[k % route.length];

        return space.dist(a, b) + space.dist(c, d) + space.dist(e, f)
                - space.dist(a, d) - space.dist(b, e) - space.dist(c, f);
    }


    @Override
    public String toString() {
        return Arrays.toString(route);
    }


    @Override
    public Iterator<Integer> iterator() {
        return new RouteIterator(this);
    }

    public class RouteIterator implements Iterator<Integer> {
        private int pos = 0;
        private final Route route;

        public RouteIterator(Route theRoute) {
            route = theRoute;
        }

        @Override
        public boolean hasNext() {
            return (pos < route.length() - 1);
        }

        @Override
        public Integer next() {
            int item = route.route[pos];
            pos++;
            return item;
        }
    }


    public static void main(String[] args) {
        In in = new In(args[0]);
        TspSpace space = new TspSpace(in);
        int[] points = new int[space.numPoints];
        for (int i = 0; i < space.numPoints; i++) {
            points[i] = i;
        }
        Route route = new Route(points, space);
        StdOut.println("Improvement swap 0, 2, 4: " + route.improvementSwapped(0, 2, 4));
        StdOut.println(route.swap(0, 2, 4));
        StdOut.println("Improvement swap 1, 2, " + space.numPoints + ": " + route.improvementSwapped(1, 2, space.numPoints));
        StdOut.println(route.swap(1, 2, space.numPoints));
    }
}
