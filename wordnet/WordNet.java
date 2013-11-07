package wordnet;

import algs4.Digraph;
import stdlib.In;
import stdlib.StdIn;
import stdlib.StdOut;

import java.util.*;

/**
 * User: bharadwaj
 * Date: 05/11/13
 * Time: 10:05 PM
 */
public class WordNet {

    private final Map<Integer, Integer[]> wordTree = new HashMap<Integer, Integer[]>();

    private final Map<String, Set<Integer>> synsetNounMap = new HashMap<String, Set<Integer>>(); // synset induvidual nouns to its id
    private final Map<Integer, String> synsetIdMap = new HashMap<Integer, String>(); // synset id to single synset string of all nouns

    private final Digraph G;
    private final SAP sap;

    // A single synset like 'miracle' can have multiple IDs. 'miracle' has 54682 and 54683 (and maybe more!)

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) throws java.lang.IllegalArgumentException {
        In synsetsIn = new In(synsets);
        String synsetsLine;
        int counter = 0;
        while (!synsetsIn.isEmpty()) {
            synsetsLine = synsetsIn.readLine();
            //System.out.println("counter = " + counter++ + " line = " + synsetsLine);

            String[] synsetLineParts = synsetsLine.split(",");
            Integer synsetId = Integer.parseInt(synsetLineParts[0]);
            //System.out.println("synsetId = " + synsetId);

            //synsetMap.put(synsetLineParts[1], synsetId);
            synsetIdMap.put(synsetId, synsetLineParts[1]);

            String[] nouns = synsetLineParts[1].split(" ");
            for (String noun : nouns) {
                if (synsetNounMap.containsKey(noun)) {
                    Set idSet = synsetNounMap.get(noun);
                    idSet.add(synsetId);
                } else {
                    Set idSet = new HashSet<Integer>();
                    idSet.add(synsetId);
                    synsetNounMap.put(noun, idSet);
                }
            }
        }
        synsetsIn.close();

        G = new Digraph(synsetIdMap.size());
        System.out.println("# of vertices on the graph, synsetMap = " + synsetNounMap.size());
        System.out.println("# of vertices on the graph, synsetIdMap = " + synsetIdMap.size());

        In hypernymsIns = new In(hypernyms);
        String hypernymsLine;
        counter = 0;
        while (!hypernymsIns.isEmpty()) {
            hypernymsLine = hypernymsIns.readLine();
            String[] hypernymsParts = hypernymsLine.split(",");
            Integer synsetId = Integer.parseInt(hypernymsParts[0]);

            Integer[] hypernymsArray = new Integer[hypernymsParts.length - 1];
            if (hypernymsParts.length > 2) {
                for (int i = 1; i < hypernymsParts.length - 1; i++) {
                    hypernymsArray[i - 1] = Integer.parseInt(hypernymsParts[i]);
                    G.addEdge(synsetId, hypernymsArray[i - 1]);
                    //System.out.println("adding edge: " + synsetId + " => " + hypernymsArray[i - 1]);
                    counter++;
                }
            } else if (hypernymsParts.length == 2) {
                hypernymsArray[0] = Integer.parseInt(hypernymsParts[1]);
                G.addEdge(synsetId, hypernymsArray[0]);
                counter++;
            } else {
                System.out.println("flawed hypernym line = " + hypernymsLine);
            }

            wordTree.put(synsetId, hypernymsArray);
        }
        hypernymsIns.close();
        System.out.println("number of edges added = " + counter);

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
        if (isNoun(nounA) == false || isNoun(nounB) == false) {
            throw new IllegalArgumentException("one of the nouns not part of wordnet = [" + nounA + ", " + nounB + "]");
        }

        Set<Integer> nounAsynsetId = synsetNounMap.get(nounA);
        Set<Integer> nounBsynsetId = synsetNounMap.get(nounB);

        return sap.length(nounAsynsetId, nounBsynsetId);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (isNoun(nounA) == false || isNoun(nounB) == false) {
            throw new IllegalArgumentException("one of the nouns not part of wordnet = [" + nounA + ", " + nounB + "]");
        }

        Set<Integer> nounAsynsetId = synsetNounMap.get(nounA);
        Set<Integer> nounBsynsetId = synsetNounMap.get(nounB);

        int ancestorId = sap.ancestor(nounAsynsetId, nounBsynsetId);
        return synsetIdMap.get(ancestorId);
    }

    public class MyIterator implements Iterable<String>, Iterator<String> {

        String[] nounArray = synsetNounMap.keySet().toArray(new String[0]);
        int position = 0;

        @Override
        public boolean hasNext() {
            if (position == nounArray.length - 1) {
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
        //for(String noun: wordnet.nouns()) {
        //    System.out.println(noun);
        //    counter++;
        //}
        System.out.println("total num of nouns = " + counter);

        while (!StdIn.isEmpty()) {
            String nounA = StdIn.readString();
            String nounB = StdIn.readString();

            System.out.println("is A noun = " + wordnet.isNoun(nounA));
            System.out.println("is B noun = " + wordnet.isNoun(nounB));

            Set<Integer> idA = wordnet.synsetNounMap.get(nounA);
            Set<Integer> idB = wordnet.synsetNounMap.get(nounB);

            StringBuilder sb = new StringBuilder("");
            for (Integer id : idA) {
                sb.append(id);
                sb.append(", ");
            }
            System.out.println("noun A ids = " + sb.toString());

            sb = new StringBuilder("");
            for (Integer id : idB) {
                sb.append(id);
                sb.append(", ");
            }
            System.out.println("noun B ids = " + sb.toString());


            int length = wordnet.distance(nounA, nounB);
            String ancestor = wordnet.sap(nounA, nounB);
            StdOut.printf("length = %d, ancestor = %s\n", length, ancestor);
        }
    }
}
