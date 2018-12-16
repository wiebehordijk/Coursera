import edu.princeton.cs.algs4.Picture;

import java.awt.Color;

public class EnergyComputer {

    private final Picture picture;

    public EnergyComputer(Picture picture) {
        if (picture == null)
            throw new IllegalArgumentException("picture is null");
        this.picture = picture;
    }

    public double energy(int x, int y) {
        return computeEnergy(x, y);
    }

    private double computeEnergy(int x, int y) {              // energy of pixel at column x and row y
        checkBounds(x, picture.width());
        checkBounds(y, picture.height());

        if (x == 0 || x == picture.width()-1 || y == 0 || y == picture.height()-1) {
            return 1000.0;
        }
        else {
            Color xplus1 = picture.get(x + 1, y);
            Color xminus1 = picture.get(x - 1, y);
            Color yplus1 = picture.get(x, y + 1);
            Color yminus1 = picture.get(x, y - 1);
            return Math.sqrt(deltaSquared(xplus1, xminus1) + deltaSquared(yplus1, yminus1));
        }
    }

    private int deltaSquared(Color point1, Color point2) {
        int r = point1.getRed() - point2.getRed();
        int b = point1.getBlue() - point2.getBlue();
        int g = point1.getGreen() - point2.getGreen();
        return r * r + b * b + g * g;
    }

    private void checkBounds(int i, int size) {
        if (i < 0 || i >= size)
            throw new IllegalArgumentException(i + " out of bounds");
    }

}
