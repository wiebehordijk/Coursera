import edu.princeton.cs.algs4.StdOut;

import java.util.HashSet;

public class BoggleDFS {

    private final BoggleBoard board;
    private final BoggleTrieSET st;
    private boolean[][] visited;
    private final HashSet<String> found;

    public BoggleDFS(BoggleBoard board, BoggleTrieSET st, int row, int col, HashSet<String> found) {
        this.board = board;
        this.st = st;
        this.found = found;
        this.visited = new boolean[board.rows()][board.cols()];

        StringBuilder builder = new StringBuilder();
        char c = board.getLetter(row, col);
        if (c == 'Q')
            builder.append("QU");
        else
            builder.append(c);
        dfs(builder, row, col, st.getRoot(), c);
    }


    private void dfs(StringBuilder prefix, int row, int col, BoggleTrieSET.Node parent, char c) {

        BoggleTrieSET.Node node = st.getNext(parent, c);
        if (node == null)
            return;
        if (node.isString())
            found.add(prefix.toString());

        visited[row][col] = true;

        for (int i = Math.max(0, row-1); i <= Math.min(board.rows()-1, row+1); i++) {
            for (int j = Math.max(0, col-1); j <= Math.min(board.cols()-1, col+1); j++) {
                if (!visited[i][j]) {
                    char next = board.getLetter(i, j);
                    if (next == 'Q') {
                        prefix.append("QU");
                        dfs(prefix, i, j, node, next);
                        prefix.deleteCharAt(prefix.length() - 1);
                        prefix.deleteCharAt(prefix.length() - 1);
                    }
                    else {
                        prefix.append(next);
                        dfs(prefix, i, j, node, next);
                        prefix.deleteCharAt(prefix.length() - 1);
                    }
                }
            }
        }

        visited[row][col] = false;
    }


    public Iterable<String> found() {
        return found;
    }


    public static void main(String[] args) {
        BoggleBoard board = new BoggleBoard(new char[][] {{'A', 'B'}, {'C', 'D'}});
        BoggleTrieSET st = new BoggleTrieSET();
        st.add("ABC");
        st.add("ADB");
        st.add("A");
        st.add("AB");

        BoggleDFS dfs = new BoggleDFS(board, st, 0, 0, new HashSet<String>());
        for (String word: dfs.found()) {
            StdOut.println(word);
        }
    }
}
