import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class TabuList<T> implements Iterable<T> {

    private final HashSet<T> set;
    private final ArrayList<T> queue;
    private final int size;


    public TabuList(int mySize) {
        size = mySize;
        set = new HashSet<>(size);
        queue = new ArrayList<>(size);
    }

    public void add(T e) {
        if (queue.size() >= size)
            removeLast();

        set.add(e);
        queue.add(e);
    }

    private void removeLast() {
        T e = queue.remove(0);
        set.remove(e);
    }

    public boolean contains(T e) {
        return set.contains(e);
    }

    @Override
    public Iterator<T> iterator() {
        return queue.iterator();
    }


    public static void main(String[] args) {
        TabuList<Integer> tl = new TabuList<>(5);
        StdOut.println(tl.contains(3));
        tl.add(3);
        StdOut.println(tl.contains(3));
        tl.add(10);
        tl.add(11);
        tl.add(12);
        tl.add(13);
        tl.add(14);
        StdOut.println(tl.contains(3));

        for (int e : tl) {
            StdOut.print(e);
            StdOut.print(" ");
        }
        StdOut.println();
    }
}
