import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.HashSet;

public class BoggleSolver {

    private final BoggleTrieSET st;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        st = new BoggleTrieSET();
        for (String word: dictionary) {
            if (word.length() >= 3) {
                boolean singleQ = false;
                for (int i = 0; i < word.length(); i++) {
                    if (word.charAt(i) == 'Q' && (i == word.length() - 1 || word.charAt(i + 1) != 'U'))
                        singleQ = true;
                }
                if (!singleQ)
                    st.add(removeUfromQU(word));
            }
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        HashSet<String> found = new HashSet<String>();

        for (int i = 0; i < board.rows(); i++) {
            for (int j = 0; j < board.cols(); j++) {
                new BoggleDFS(board, st, i, j, found);
            }
        }

        return found;
    }


    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (!st.contains(removeUfromQU(word)))
            return 0;

        int len = word.length();
        switch (len) {
            case 0: case 1: case 2: return 0;
            case 3: case 4: return 1;
            case 5: return 2;
            case 6: return 3;
            case 7: return 5;
            default: return 11;
        }
    }


    private static String removeUfromQU(String word) {
        return word.replaceAll("QU", "Q");
    }

//    private static String addUtoQ(String word) {
//        return word.replaceAll("Q", "QU");
//    }

    public static void main(String[] args) {
        In dictionary = new In(args[0]);
        BoggleSolver solver = new BoggleSolver(dictionary.readAllLines());

        BoggleBoard board = new BoggleBoard(args[1]);
        StdOut.println(board);

        Stopwatch sw = new Stopwatch();
//        for (int i = 0; i < 10000; i++) {
//            BoggleBoard board = new BoggleBoard();
            Iterable<String> found = solver.getAllValidWords(board);
            int score = 0;
            for (String word : found) {
                StdOut.println(word);
                score += solver.scoreOf(word);
            }
            StdOut.println("Score: " + score);
//        }
        StdOut.println(sw.elapsedTime());
    }
}
