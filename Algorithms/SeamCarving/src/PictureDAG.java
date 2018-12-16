import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PictureDAG implements Iterable<Integer> {
    private final Picture picture;
    private final boolean vertical;

    public PictureDAG(Picture picture, boolean vertical) {
        if (picture == null)
            throw new IllegalArgumentException("picture is null");

        this.picture = picture;
        this.vertical = vertical;
    }

    public int height() {
        return picture.height();
    }

    public int width() {
        return picture.width();
    }

    public int v() {
        return height() * width();
    }

    public int x(int p) {
        checkIndex(p);
        return p % width();
    }

    public int y(int p) {
        checkIndex(p);
        return p / width();
    }

    private void checkIndex(int v) {
        if (v < 0 || v > v())
            throw new IllegalArgumentException(v + " is not a valid index");
    }

    private int toIndex(int x, int y) {
        return y * width() + x;
    }

    public Iterable<Integer> adj(int v) {
        checkIndex(v);
        List<Integer> result = new ArrayList<>();

        if (vertical) {
            if (y(v) == height() - 1)
                return result;
            for (int i = Math.max(0, x(v)-1); i <= Math.min(width() - 1, x(v) + 1); i++)
                result.add(toIndex(i, y(v) + 1));
        }
        else {
            if (x(v) == width() - 1)
                return result;
            for (int i = Math.max(0, y(v) - 1); i <= Math.min(height() - 1, y(v) + 1); i++)
                result.add(toIndex(x(v) + 1, i));
        }
        return result;
    }

    public Iterable<Integer> roots() {
        List<Integer> result = new ArrayList<>();
        if (vertical) {
            for (int x = 0; x < width(); x++)
                result.add(toIndex(x, 0));
        }
        else {
            for (int y = 0; y < height(); y++)
                result.add(toIndex(0, y));
        }
        return result;
    }

    public Iterable<Integer> sinks() {
        List<Integer> result = new ArrayList<>();
        if (vertical) {
            for (int x = 0; x < width(); x++)
                result.add(toIndex(x, height() - 1));
        }
        else {
            for (int y = 0; y < height(); y++)
                result.add(toIndex(width() - 1, y));
        }
        return result;
    }

    public boolean isSink(int v) {
        checkIndex(v);
        if (vertical)
            return y(v) == height() - 1;
        else
            return x(v) == width() - 1;
    }

    private class VerticalIterator implements Iterator<Integer> {

        private int cursor = 0;

        @Override
        public boolean hasNext() {
            return (cursor < v() - 1);
        }

        @Override
        public Integer next() {
            return cursor++;
        }
    }

    private class HorizontalIterator implements Iterator<Integer> {

        private int cursor = 0;

        @Override
        public boolean hasNext() {
            return (cursor < v() - 1);
        }

        @Override
        public Integer next() {
            int x = x(cursor);
            int y = y(cursor);
            y++;
            if (y >= height()) {
                x++;
                y = 0;
            }
            cursor = toIndex(x, y);
            return cursor;
        }
    }

    @Override
    public Iterator<Integer> iterator() {
        if (vertical)
            return new VerticalIterator();
        else
            return new HorizontalIterator();
    }

    public int seamLength() {
        if (vertical)
            return height();
        else
            return width();
    }

    public static void main(String[] args) {
        Picture picture = new Picture(args[0]);
        PictureDAG dag = new PictureDAG(picture, true);
        Iterable<Integer> it = dag.adj(dag.toIndex(1, 2));
        StdOut.print("adj(1,2): ");
        for (int p: it)
            StdOut.print(dag.x(p) + "," + dag.y(p) + "  ");
    }
}
