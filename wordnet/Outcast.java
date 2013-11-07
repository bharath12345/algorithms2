package wordnet;

import stdlib.In;
import stdlib.StdOut;

/**
 * User: bharadwaj
 * Date: 05/11/13
 * Time: 10:47 PM
 */
public class Outcast {

    private final WordNet wordnet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        String outcast = null;
        int maxDist = 0;
        for(int i=0; i<nouns.length; i++) {
            int distance = 0;
            for(int j=0; j<nouns.length; j++) {
                if(i == j) {
                    continue;
                }

                distance += wordnet.distance(nouns[i], nouns[j]);
            }
            if(maxDist == 0 || distance > maxDist) {
                maxDist = distance;
                outcast = nouns[i];
            }
        }
        return outcast;
    }

    // for unit testing of this class (such as the one below)
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            String[] nouns = In.readStrings(args[t]);
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
