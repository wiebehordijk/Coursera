import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet wn;

    public Outcast(WordNet wordnet) {
        if (wordnet == null)
            throw new IllegalArgumentException("wordnet is null");

        wn = wordnet;
    }

    public String outcast(String[] nouns)   // given an array of WordNet nouns, return an outcast
    {
        if (nouns == null || nouns.length == 0)
            throw new IllegalArgumentException("nouns is empty");

        int[] sums = new int[nouns.length];
        for (int i = 0; i < nouns.length; i++) {
            for (int j = i + 1; j < nouns.length; j++) {
                int dist = wn.distance(nouns[i], nouns[j]);
                sums[i] += dist;
                sums[j] += dist;
            }
        }

        int outcast = 0;
        for (int i = 1; i < nouns.length; i++)
            if (sums[i] > sums[outcast])
                outcast = i;

        return nouns[outcast];
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}