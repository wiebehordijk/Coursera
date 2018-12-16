import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class WordNet {

    private Map<String, Set<Integer>> words;
    private Map<Integer, Set<String>> synsets;
    private Digraph hypernyms;
    private SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null)
            throw new IllegalArgumentException("synsets is null");
        if (hypernyms == null)
            throw new IllegalArgumentException("hypernyms is null");

        readSynsets(synsets);
        readHypernyms(hypernyms);

        DirectedCycle dc = new DirectedCycle(this.hypernyms);
        if (dc.hasCycle()) {
            throw new IllegalArgumentException("Hypernyms graph contains a cycle");
        }
    }

    private void readSynsets(String filename) {
        this.words = new HashMap<>();
        this.synsets = new HashMap<>();
        In in = new In(filename);

        for (int i = 0; in.hasNextLine(); i++) {
            Set<String> synset = new HashSet<>();
            String line = in.readLine();
            String[] parts = line.split(",");
            String[] wordparts = parts[1].split(" ");

            for (String word: wordparts) {
                synset.add(word);
                if (!this.words.containsKey(word))
                    this.words.put(word, new HashSet<Integer>());
                this.words.get(word).add(i);
            }

            synsets.put(i, synset);
        }
    }

    private void readHypernyms(String filename) {
        this.hypernyms = new Digraph(this.synsets.size());
        In in = new In(filename);
        while (in.hasNextLine()) {
            String line = in.readLine();
            String[] parts = line.split(",");
            int v = Integer.parseInt(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                int w = Integer.parseInt(parts[i]);
                this.hypernyms.addEdge(v, w);
            }
        }
        sap = new SAP(hypernyms);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return words.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null)
            throw new IllegalArgumentException("word is null");

        return words.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        Iterable<Integer> synsetA = getSynset(nounA);
        Iterable<Integer> synsetB = getSynset(nounB);

        return sap.length(synsetA, synsetB);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        Iterable<Integer> synsetA = getSynset(nounA);
        Iterable<Integer> synsetB = getSynset(nounB);

        int commonAncestor = sap.ancestor(synsetA, synsetB);
        return String.join(" ", synsets.get(commonAncestor));
    }

    private Iterable<Integer> getSynset(String noun) {
        if (noun == null)
            throw new IllegalArgumentException("noun is null");
        if (!words.containsKey(noun))
            throw new IllegalArgumentException(noun + " is not a WordNet word");

        return words.get(noun);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wn = new WordNet(args[0], args[1]);
        Iterator<String> it = wn.nouns().iterator();
        for (int i = 0; i < 20 && it.hasNext(); i++) {
            String w = it.next();
            Iterable<Integer> synsets = wn.words.get(w);
            StdOut.println(w + " - " + wn.words.get(w) + " : " + synsets);
        }

        StdOut.println();
        StdOut.println("Hypernyms: V = " + wn.hypernyms.V() + ", E = " + wn.hypernyms.E());
    }
}