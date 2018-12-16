import edu.princeton.cs.algs4.Queue;

import java.util.Iterator;

public class BoggleTrieSET implements Iterable<String> {
    private static final int R = 26;        // Boggle letters
    private static final int OFFSET = 'A';

    private BoggleTrieSET.Node root;      // root of trie
    private int n;          // number of keys in trie

    // R-way trie node
    public static class Node {
        private BoggleTrieSET.Node[] next = new BoggleTrieSET.Node[R];
        private boolean isString;

        public boolean isString() {
            return isString;
        }
    }

    /**
     * Initializes an empty set of strings.
     */
    public BoggleTrieSET() {
    }

    /**
     * Does the set contain the given key?
     * @param key the key
     * @return {@code true} if the set contains {@code key} and
     *     {@code false} otherwise
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public boolean contains(String key) {
        if (key == null) throw new IllegalArgumentException("argument to contains() is null");
        BoggleTrieSET.Node x = get(root, key, 0);
        if (x == null) return false;
        return x.isString;
    }

//    public boolean isPrefix(String key) {
//        if (key == null) throw new IllegalArgumentException("argument is null");
//        BoggleTrieSET.Node x = get(root, key, 0);
//        return (x != null);
//    }

    public Node getRoot() {
        return root;
    }

    public Node getNext(Node x, char c) {
        if (x == null) return null;
        return x.next[c - OFFSET];
    }

    private BoggleTrieSET.Node get(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) return x;
        char c = key.charAt(d);
        return get(x.next[c - OFFSET], key, d+1);
    }


    /**
     * Returns all of the keys in the set that start with {@code prefix}.
     * @param prefix the prefix
     * @return all of the keys in the set that start with {@code prefix},
     *     as an iterable
     */
    private Iterable<String> keysWithPrefix(String prefix) {
        Queue<String> results = new Queue<String>();
        BoggleTrieSET.Node x = get(root, prefix, 0);
        collect(x, new StringBuilder(prefix), results);
        return results;
    }

    private void collect(BoggleTrieSET.Node x, StringBuilder prefix, Queue<String> results) {
        if (x == null) return;
        if (x.isString) results.enqueue(prefix.toString());
        for (char c = 0; c < R; c++) {
            if (c + OFFSET == 'Q') {
                prefix.append("QU");
                collect(x.next[c], prefix, results);
                prefix.deleteCharAt(prefix.length() - 1);
                prefix.deleteCharAt(prefix.length() - 1);
            }
            else {
                prefix.append((char) (c + OFFSET));
                collect(x.next[c], prefix, results);
                prefix.deleteCharAt(prefix.length() - 1);
            }
        }
    }

    @Override
    public Iterator<String> iterator() {
        return keysWithPrefix("").iterator();
    }


    /**
     * Adds the key to the set if it is not already present.
     * @param key the key to add
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public void add(String key) {
        if (key == null) throw new IllegalArgumentException("argument to add() is null");
        root = add(root, key, 0);
    }

    private BoggleTrieSET.Node add(BoggleTrieSET.Node x, String key, int d) {
        if (x == null) x = new BoggleTrieSET.Node();
        if (d == key.length()) {
            if (!x.isString) n++;
            x.isString = true;
        }
        else {
            char c = key.charAt(d);
            x.next[c - OFFSET] = add(x.next[c - OFFSET], key, d+1);
        }
        return x;
    }

    /**
     * Returns the number of strings in the set.
     * @return the number of strings in the set
     */
    public int size() {
        return n;
    }

    /**
     * Is the set empty?
     * @return {@code true} if the set is empty, and {@code false} otherwise
     */
    public boolean isEmpty() {
        return size() == 0;
    }
}
