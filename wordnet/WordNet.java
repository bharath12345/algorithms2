package wordnet;

import algs4.Digraph;
import stdlib.In;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * User: bharadwaj
 * Date: 05/11/13
 * Time: 10:05 PM
 */
public class WordNet {

    private Map<Integer, Integer []> wordTree = new HashMap<Integer, Integer []>();

    private Map<String, Integer> synsetMap = new HashMap<String, Integer>(); // synset string (all nouns) to its id
    private Map<String, Integer> synsetNounMap = new HashMap<String, Integer>(); // synset induvidual nouns to its id
    private Map<Integer, String> synsetIdMap = new HashMap<Integer, String>(); // synset id to single synset string of all nouns

    private Digraph G;
    private SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) throws java.lang.IllegalArgumentException {
        In synsetsIn = new In(synsets);
        String synsetsLine;
        while(!synsetsIn.isEmpty()) {
            synsetsLine = synsetsIn.readLine();
            String [] synsetLineParts = synsetsLine.split(",");
            Integer synsetId = Integer.parseInt(synsetLineParts[0]);
            String [] nouns = synsetLineParts[1].split(" ");

            synsetMap.put(synsetLineParts[1], synsetId);
            synsetIdMap.put(synsetId, synsetLineParts[1]);
            for(String noun: nouns) {
                synsetNounMap.put(noun, synsetId);
            }
        }
        synsetsIn.close();

        G = new Digraph(synsetMap.size());

        In hypernymsIns = new In(hypernyms);
        String hypernymsLine;
        while(!hypernymsIns.isEmpty()) {
            hypernymsLine = hypernymsIns.readLine();
            String [] hypernymsParts = hypernymsLine.split(",");
            Integer synsetId = Integer.parseInt(hypernymsParts[0]);

            Integer [] hypernymsArray = new Integer[hypernymsParts.length - 1];
            for(int i=1; i<hypernymsParts.length-1; i++) {
                hypernymsArray[i-1] = Integer.parseInt(hypernymsParts[i]);
                G.addEdge(synsetId, hypernymsArray[i-1]);
            }
            wordTree.put(synsetId, hypernymsArray);
        }
        hypernymsIns.close();

        sap = new SAP(G);
    }

    // the set of nouns (no duplicates), returned as an Iterable
    public Iterable<String> nouns() {
        return new MyIterator();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        return synsetNounMap.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if(isNoun(nounA) == false || isNoun(nounB) == false) {
            throw new IllegalArgumentException("one of the nouns not part of wordnet = [" + nounA + ", " + nounB + "]");
        }

        int nounAsynsetId = synsetNounMap.get(nounA);
        int nounBsynsetId = synsetNounMap.get(nounB);

        return sap.length(nounAsynsetId, nounBsynsetId);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if(isNoun(nounA) == false || isNoun(nounB) == false) {
            throw new IllegalArgumentException("one of the nouns not part of wordnet = [" + nounA + ", " + nounB + "]");
        }

        int nounAsynsetId = synsetNounMap.get(nounA);
        int nounBsynsetId = synsetNounMap.get(nounB);

        int ancestorId = sap.ancestor(nounAsynsetId, nounBsynsetId);
        return synsetIdMap.get(ancestorId);
    }

    public class MyIterator implements Iterable<String>, Iterator<String> {

        String[] nounArray = synsetMap.keySet().toArray(new String[0]);
        int position = 0;

        @Override
        public boolean hasNext() {
            if(position == nounArray.length - 1) {
                return false;
            }
            return true;
        }

        @Override
        public String next() {
            return nounArray[position++];
        }

        @Override
        public void remove() {
        }

        @Override
        public Iterator<String> iterator() {
            return this;
        }
    }

    // for unit testing of this class
    public static void main(String[] args) {
        WordNet wordnet = new WordNet("./input/synsets.txt", "./input/hypernyms.txt");

        long counter = 0;
        for(String noun: wordnet.nouns()) {
            System.out.println(noun);
            counter++;
        }

        System.out.println("total num of nouns = " + counter);
        System.out.println("total num of nouns = " + counter);
    }
}
