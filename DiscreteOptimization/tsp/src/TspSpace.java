import edu.princeton.cs.algs4.*;

import java.awt.*;

public class TspSpace {

    public class TspPoint {
        public final double x, y;

        public TspPoint(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    public final int numPoints;
    private final boolean precalc;

    public double getMinX() {
        return minX;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxY() {
        return maxY;
    }

    private double minX = 0.0, maxX = 0.0, minY = 0.0, maxY = 0.0;
    private TspPoint[] points;
    private double[][] distance;

    public TspSpace(In inputFile) {
        numPoints = inputFile.readInt();
        inputFile.readLine();
        points = new TspPoint[numPoints];
        for (int i = 0; i < numPoints; i++) {
            double x = inputFile.readDouble();
            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
            double y = inputFile.readDouble();
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;

            points[i] = new TspPoint(x, y);
            inputFile.readLine();
        }

        precalc = (numPoints < 1000);
        if (precalc) {
            distance = new double[numPoints][numPoints];
            for (int i = 0; i < numPoints; i++) {
                for (int j = i; j < numPoints; j++) {
                    double dist = calcdist(i, j);
                    distance[i][j] = dist;
                    distance[j][i] = dist;
                }
            }
        }
    }

    private double calcdist(int i, int j) {
        return Math.sqrt(Math.pow(Math.abs(points[i].x - points[j].x), 2) +
                Math.pow(Math.abs(points[i].y - points[j].y), 2));
    }

    public TspPoint get(int i) {
        return points[i];
    }

    public double dist(int i, int j) {
        if (precalc)
            return distance[i][j];
        else
            return calcdist(i, j);
    }


    public void drawToScale() {
        StdDraw.setCanvasSize(1200, 1000);
        StdDraw.setXscale(minX - 1.0, maxX + 1.0);
        StdDraw.setYscale(minY - 1.0, maxY + 1.0);
        StdDraw.setPenRadius(0.02);
        StdDraw.setPenColor(Color.BLACK);
        for (int i = 0; i < numPoints; i++) {
            StdDraw.point(points[i].x, points[i].y);
        }
        StdDraw.show();
    }

    public void draw() {
        StdDraw.setPenRadius(0.02);
        for (int i = 0; i < numPoints; i++) {
            StdDraw.point(points[i].x, points[i].y);
        }

    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        TspSpace space = new TspSpace(in);
        StdOut.println(space.numPoints);
        StdOut.println(space.dist(0, 1));
        StdOut.println(space.dist(1, 0));
        space.drawToScale();
    }
}
