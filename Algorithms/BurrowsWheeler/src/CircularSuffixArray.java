import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class CircularSuffixArray {
    private final Suffix[] suffixes;

    /**
     * Initializes a circular suffix array for the given {@code text} string.
     * @param text the input string
     */
    public CircularSuffixArray(String text) {
        if (text == null) throw new IllegalArgumentException("text is null");

        int length = text.length();
        this.suffixes = new Suffix[length];
        String texttext = text + text;
        for (int i = 0; i < length; i++)
            suffixes[i] = new Suffix(texttext, i, length);
        Arrays.sort(suffixes);
    }

    private static class Suffix implements Comparable<Suffix> {
        private final String text;
        private final int index;
        private final int length;

        private Suffix(String text, int index, int length) {
            this.text = text;
            this.index = index;
            this.length = length;
        }
        private char charAt(int i) {
            return text.charAt(index + i);
        }

        public int compareTo(Suffix that) {
            if (this == that) return 0;  // optimization
            for (int i = 0; i < length; i++) {
                if (this.charAt(i) < that.charAt(i)) return -1;
                if (this.charAt(i) > that.charAt(i)) return +1;
            }
            return 0;
        }

        public String toString() {
            return text.substring(index, index + length);
        }
    }

    /**
     * Returns the length of the input string.
     * @return the length of the input string
     */
    public int length() {
        return suffixes.length;
    }

    /**
     * Returns the index into the original string of the <em>i</em>th smallest suffix.
     * That is, {@code text.substring(sa.index(i))} is the <em>i</em>th smallest suffix.
     * @param i an integer between 0 and <em>n</em>-1
     * @return the index into the original string of the <em>i</em>th smallest suffix
     * @throws java.lang.IllegalArgumentException unless {@code 0 <= i < n}
     */
    public int index(int i) {
        if (i < 0 || i >= suffixes.length) throw new IllegalArgumentException();
        return suffixes[i].index;
    }

    /**
     * Unit tests the {@code CircularSuffixArray} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        String s = StdIn.readAll().replaceAll("\\s+", " ").trim();
        CircularSuffixArray suffix = new CircularSuffixArray(s);
        StdOut.println("Length: " + suffix.length());

        for (int i = 0; i < s.length(); i++) {
            int index = suffix.index(i);
            String ith = "\"" + suffix.suffixes[i] + "\"";
            StdOut.printf("%3d %3d %s\n", i, index, ith);
        }
    }

}