import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.Objects;

public class MoveToFront {

    private static final int R = 256;
    private char[] sequence;

    public MoveToFront() {
        sequence = new char[R];
        for (char i = 0; i < R; i++) sequence[i] = i;
    }

    private char moveChar(char c) {
        for (char i = 0; i < R; i++) {
            if (sequence[i] == c) {
                move(c, i);
                return i;
            }
        }

        throw new IllegalStateException("Character " + c + " is not in the sequence");
    }

    private void move(char c, char i) {
        for (char j = i; j > 0; j--) {
            sequence[j] = sequence[j-1];
        }
        sequence[0] = c;
    }

    private char moveIndex(char i) {
        char c = sequence[i];
        move(c, i);
        return c;
    }

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        MoveToFront codec = new MoveToFront();
        while (!BinaryStdIn.isEmpty()) {
            BinaryStdOut.write(codec.moveChar(BinaryStdIn.readChar()));
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        MoveToFront codec = new MoveToFront();
        while (!BinaryStdIn.isEmpty()) {
            BinaryStdOut.write(codec.moveIndex(BinaryStdIn.readChar()));
        }
        BinaryStdOut.close();
    }

    // if args[0] is '-', apply move-to-front encoding
    // if args[0] is '+', apply move-to-front decoding
    public static void main(String[] args) {
        if (Objects.equals(args[0], "-")) encode();
        else if (Objects.equals(args[0], "+")) decode();
        else throw new IllegalArgumentException("args[0] must be '-' for encoding or '+' for decoding");
    }
}
