import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {

    // apply Burrows-Wheeler transform, reading from standard input and writing to standard output
    public static void transform() {
        String input = BinaryStdIn.readString();
        CircularSuffixArray suffix = new CircularSuffixArray(input);

        // Output index where original string ends up in the sorted circular suffix array
        for (int i = 0; i < suffix.length(); i++) {
            if (suffix.index(i) == 0) {
                BinaryStdOut.write(i);
                break;
            }
        }

        // Output last column of sorted circular suffix array
        for (int i = 0; i < suffix.length(); i++)
            if (suffix.index(i) == 0)
                BinaryStdOut.write(input.charAt(suffix.length() - 1));
            else
                BinaryStdOut.write(input.charAt(suffix.index(i) - 1));

        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform, reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        String input = BinaryStdIn.readString();
        int length = input.length();

        // CountSort
        final int R = 256;
        int[] count = new int[R+1];
        for (int i = 0; i < length; i++) {
            count[input.charAt(i)+1]++;
        }
        for (int i = 1; i <= R; i++) {
            count[i] += count[i-1];
        }
        char[] sorted = new char[length];
        int[] next = new int[length];
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            int index = count[c]++;
            sorted[index] = c;
            next[index] = i;
        }

        // Get original string from sorted (first) column and next array
        int index = first;
        for (int i = 0; i < length; i++) {
            BinaryStdOut.write(sorted[index]);
            index = next[index];
        }
        BinaryStdOut.close();
    }

    // if args[0] is '-', apply Burrows-Wheeler transform
    // if args[0] is '+', apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-"))
            transform();
        else if (args[0].equals("+"))
            inverseTransform();
        else
            throw new IllegalArgumentException("args[0] must be - for transform or + for inverse transform");
    }
}
