import edu.princeton.cs.algs4.StdDraw;

public class Coprime {

    public static boolean[][] makeTable(int n) {
        boolean[][] result = new boolean[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                boolean coprime = isCoprime(i, j);
                result[i][j] = coprime;
                result[j][i] = coprime;
            }
        }

        return result;
    }


    private static boolean isCoprime(int a, int b) {
        return gcd(a, b) == 1;
    }


    // https://en.wikipedia.org/wiki/Euclidean_algorithm
    private static int gcd(int a, int b) {
        int t;
        while (b != 0) {
            t = b;
            b = a % b;
            a = t;
        }
        return a;
    }


    private static void drawBoolTable(boolean[][] table) {
        StdDraw.setScale(0, table.length);
        StdDraw.setPenRadius(0.01);

        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table.length; j++) {
                if (table[i][j])
                    StdDraw.point(i, table.length - j);
            }
        }
    }


    public static void main(String[] args) {
        int n = 100;
        boolean[][] table = makeTable(n);

        drawBoolTable(table);
    }
}
