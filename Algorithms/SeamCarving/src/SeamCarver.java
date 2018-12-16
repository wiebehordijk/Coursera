import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    private Picture picture;
    private EnergyComputer ec;
    private double[][] energy;

    public SeamCarver(Picture picture) {               // create a seam carver object based on the given picture
        if (picture == null)
            throw new IllegalArgumentException("Picture is null");

        this.picture = new Picture(picture);
        this.ec = new EnergyComputer(this.picture);
        energy = new double[width()][height()];
        for (int x = 0; x < width(); x++)
            for (int y = 0; y < height(); y++)
                energy[x][y] = ec.energy(x, y);
    }

    public Picture picture() {                         // current picture
        return new Picture(picture);
    }

    public int width() {                           // width of current picture
        return picture.width();
    }

    public int height() {                          // height of current picture
        return picture.height();
    }

    public double energy(int x, int y) {
        checkBounds(x, picture.width());
        checkBounds(y, picture.height());
        return energy[x][y];
    }

    private void checkBounds(int i, int size) {
        if (i < 0 || i >= size)
            throw new IllegalArgumentException(i + " out of bounds");
    }

    public int[] findHorizontalSeam() {              // sequence of indices for horizontal seam
        PictureDAG dag = new PictureDAG(picture, false);
        Seam seam = new Seam(dag, energy);
        int[] path = seam.path();
        for (int i = 0; i < width(); i++)
            path[i] = dag.y(path[i]);
        return path;
    }

    public int[] findVerticalSeam() {                // sequence of indices for vertical seam
        PictureDAG dag = new PictureDAG(picture, true);
        Seam seam = new Seam(dag, energy);
        int[] path = seam.path();
        for (int i = 0; i < height(); i++)
            path[i] = dag.x(path[i]);
        return path;
    }

    public void removeHorizontalSeam(int[] seam) {  // remove horizontal seam from current picture
        checkSeam(seam, width(), height());
        Picture newPicture = new Picture(width(), height() - 1);

        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height() - 1; y++) {
                if (y < seam[x])
                    newPicture.set(x, y, picture.get(x, y));
                else
                    newPicture.set(x, y, picture.get(x, y + 1));
            }
        }
        this.picture = newPicture;
        this.ec = new EnergyComputer(newPicture);

        for (int x = 0; x < width(); x++) {
            if (seam[x] > 0)
                energy[x][seam[x] - 1] = ec.energy(x, seam[x] - 1);
            if (seam[x] < height())
                energy[x][seam[x]] = ec.energy(x, seam[x]);
            for (int y = seam[x] + 1; y < height() - 1; y++)
                energy[x][y] = energy[x][y + 1];
        }
    }

    public void removeVerticalSeam(int[] seam) {    // remove vertical seam from current picture
        checkSeam(seam, height(), width());
        Picture newPicture = new Picture(width() - 1, height());

        for (int x = 0; x < width() - 1; x++) {
            for (int y = 0; y < height(); y++) {
                if (x < seam[y])
                    newPicture.set(x, y, picture.get(x, y));
                else
                    newPicture.set(x, y, picture.get(x + 1, y));
            }
        }
        this.picture = newPicture;
        this.ec = new EnergyComputer(newPicture);

        for (int y = 0; y < height(); y++) {
            if (seam[y] > 0)
                energy[seam[y] - 1][y] = ec.energy(seam[y] - 1, y);
            if (seam[y] < width())
                energy[seam[y]][y] = ec.energy(seam[y], y);
            for (int x = seam[y] + 1; x < width() - 1; x++)
                energy[x][y] = energy[x + 1][y];
        }
    }

    private void checkSeam(int[] seam, int seamLength, int seamWidth) {
        if (seam == null)
            throw new IllegalArgumentException("seam is null");
        if (seamWidth <= 1)
            throw new IllegalArgumentException("Cannot remove seam, not enough pixels left");
        if (seam.length != seamLength)
            throw new IllegalArgumentException("Length of seam is " + seam.length + " but should be " + seamLength);
        for (int i = 0; i < seamLength; i++) {
            checkBounds(seam[i], seamWidth);
            if (i > 0 && Math.abs(seam[i] - seam[i-1]) > 1)
                throw new IllegalArgumentException("seam[" + (i-1) + "] = " + seam[i-1] + ", seam[" + i + "] = " + seam[i]);
        }
    }
}