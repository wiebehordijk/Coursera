import edu.princeton.cs.algs4.Draw;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.awt.*;
import java.util.Arrays;

public class Solver {

    private static TspSpace space;
    private static int[] route;
    private static double totalDistance;

    public static void main(String[] args) {
        String filename = args[0];
        In in = new In(filename);
        space = new TspSpace(in);
        route = new int[space.numPoints];
        totalDistance = 0.0;

        solveGreedy();

        StdOut.print(totalDistance);
        StdOut.println(" 0");
        for (int i = 0; i < route.length; i++) {
            StdOut.print(route[i]);
            StdOut.print(" ");
        }
        StdOut.println();
        draw();
    }

    private static void solveGreedy() {
        boolean[] visited = new boolean[space.numPoints];
        route[0] = 0;
        visited[0] = true;
        int next = 0;

        for (int i = 1; i < space.numPoints; i++) {
            double minDist = Double.MAX_VALUE;
            for (int j = 0; j < space.numPoints; j++) {
                if (!visited[j] && space.dist(route[i-1], j) < minDist) {
                    next = j;
                    minDist = space.dist(route[i-1], next);
                }
            }
            route[i] = next;
            totalDistance += space.dist(route[i-1], next);
            visited[next] = true;
        }

        totalDistance += space.dist(next, 0);
    }


    public static void draw() {
        StdDraw.setCanvasSize(1200, 1000);
        StdDraw.setXscale(space.getMinX() - 1.0, space.getMaxX() + 1.0);
        StdDraw.setYscale(space.getMinY() - 1.0, space.getMaxY() + 1.0);
        StdDraw.setPenRadius(0.005);
        StdDraw.setPenColor(Color.BLACK);

        for (int i = 0; i < route.length - 1; i++) {
            drawRoute(i, i + 1);
        }
        drawRoute(route.length - 1, 0);

        space.draw();

        StdDraw.show();
    }

    private static void drawRoute(int i1, int i2) {
        TspSpace.TspPoint p1 = space.get(route[i1]);
        TspSpace.TspPoint p2 = space.get(route[i2]);
        StdDraw.line(p1.x, p1.y, p2.x, p2.y);
    }


}
