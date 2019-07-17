import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class Solver {

    private static TspSpace space;
    private static Route currentRoute;
    private static TabuList<Route> tabu;
    private static int tabuHits = 0;
    private static final double TIME = 1000.0;
    private static int twoOptSteps = 0;
    private static int threeOptSteps = 0;
    private static int shuffles = 0;


    public static void main(String[] args) {
        String filename = args[0];
        In in = new In(filename);
        space = new TspSpace(in);

        solveGreedy();
        Route bestRoute = currentRoute;
        tabu = new TabuList<>(1000000);
        Stopwatch sw = new Stopwatch();
//        printSolution();
//        draw();

        while (sw.elapsedTime() < TIME) {
            while ((twoOptLocalSearchStep() > 0.001 || threeOptLocalSearchStep() > 0.001) && sw.elapsedTime() < TIME) {
                if (currentRoute.distance() < bestRoute.distance()) {
                    bestRoute = currentRoute;
//                    printRoute(currentRoute);
//                    tabu.add(currentRoute);
//                    StdOut.println(sw.elapsedTime());
                }
            }
            shuffle();
            shuffles++;
//            StdOut.println("Shuffled: " + sw.elapsedTime());
//            printRoute(currentRoute);
        }

        printRoute(bestRoute);
//        StdOut.println("Time: " + sw.elapsedTime());
//        StdOut.println("Two-opt steps: " + twoOptSteps);
//        StdOut.println("Three-opt steps: " + threeOptSteps);
//        StdOut.println("Shuffles: " + shuffles);
    }

    private static void printRoute(Route route) {
        StdOut.print(route.distance());
        StdOut.println(" 0");
        for (int i = 0; i < route.length(); i++) {
            StdOut.print(route.item(i));
            StdOut.print(" ");
        }
        StdOut.println();
    }

    private static void shuffle() {
        //noinspection unchecked
        List<Integer> list = new ArrayList();
        for (int i = 0; i < space.numPoints; i++) {
            list.add(i);
        }
        Collections.shuffle(list);

        currentRoute = new Route(list.stream().mapToInt(Integer::intValue).toArray(), space);
    }

    private static void solveGreedy() {
        boolean[] visited = new boolean[space.numPoints];
        int[] solution = new int[space.numPoints];
        solution[0] = 0;
        visited[0] = true;
        int next = 0;

        for (int i = 1; i < space.numPoints; i++) {
            double minDist = Double.MAX_VALUE;
            for (int j = 0; j < space.numPoints; j++) {
                if (!visited[j] && space.dist(solution[i - 1], j) < minDist) {
                    next = j;
                    minDist = space.dist(solution[i - 1], next);
                }
            }
            solution[i] = next;
            visited[next] = true;
        }

        currentRoute = new Route(solution, space);
    }


    private static boolean tabuLocalSearchStep() {
        double maxImprovement = -Double.MAX_VALUE;
        Route maxImprovedRoute = currentRoute;
        boolean foundNext = false;

        for (int i = 0; i < space.numPoints; i++) {
            for (int j = i + 1; j < space.numPoints; j++) {
                double improvement = currentRoute.distance() - currentRoute.distanceReversed(i, j);
                if (improvement > maxImprovement) {
                    Route next = currentRoute.reverse(i, j);
                    if (!tabu.contains(next)) {
                        maxImprovedRoute = next;
                        maxImprovement = improvement;
                        foundNext = true;
                    } else
                        tabuHits++;
                }
            }
        }

        currentRoute = maxImprovedRoute;
        tabu.add(maxImprovedRoute);
        return foundNext;
    }


    private static double twoOptLocalSearchStep() {
        double maxImprovement = 0.0;
        Route maxImprovedRoute = currentRoute;

        for (int i = 0; i < space.numPoints; i++) {
            for (int j = i + 1; j < space.numPoints; j++) {
                double improvement = currentRoute.distance() - currentRoute.distanceReversed(i, j);
                if (improvement > maxImprovement) {
                    maxImprovedRoute = currentRoute.reverse(i, j);
                    maxImprovement = improvement;
                }
            }
        }

        if (!maxImprovedRoute.equals(currentRoute))
            twoOptSteps++;
        currentRoute = maxImprovedRoute;
        return maxImprovement;
    }


    private static double threeOptLocalSearchStep() {
        for (int i = 0; i < space.numPoints; i++) {
            for (int j = i + 2; j < space.numPoints; j++) {
                for (int k = j + 2; k < space.numPoints + (i > 0 ? 1 : 0); k++) {
                    double improvement = currentRoute.improvementSwapped(i, j, k);
                    if (improvement > 0.0) {
                        currentRoute = currentRoute.swap(i, j, k);
                        threeOptSteps++;
                        return improvement;
                    }
                }
            }
        }

        return 0.0;
    }


    public static void draw() {
        StdDraw.setCanvasSize(1200, 1000);
        StdDraw.setXscale(space.getMinX() - 1.0, space.getMaxX() + 1.0);
        StdDraw.setYscale(space.getMinY() - 1.0, space.getMaxY() + 1.0);
        StdDraw.setPenRadius(0.005);
        StdDraw.setPenColor(Color.BLACK);

        for (int i = 0; i < currentRoute.length() - 1; i++) {
            drawRoute(i, i + 1);
        }
        drawRoute(currentRoute.length() - 1, 0);

        space.draw();

        StdDraw.show();
    }

    private static void drawRoute(int i1, int i2) {
        TspSpace.TspPoint p1 = space.get(currentRoute.item(i1));
        TspSpace.TspPoint p2 = space.get(currentRoute.item(i2));
        StdDraw.line(p1.x, p1.y, p2.x, p2.y);
    }


}
