package wordnet;

import algs4.Digraph;
import stdlib.In;
import stdlib.StdIn;
import stdlib.StdOut;

import java.util.*;

/**
 * User: bharadwaj
 * Date: 05/11/13
 * Time: 10:33 PM
 */
public class SAP {

    private final Digraph G;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        this.G = G;
    }

    private Map<Integer, Integer[]> getDistanceMap(int v, int distance, Map<Integer, Integer[]> dMap) {
        Set<Integer> vertices = new HashSet<Integer>();
        for(int vi: G.adj(v)) {
            dMap = getDistanceMap(vi, distance + 1, dMap);

            vertices.add(vi);
            dMap.put(distance, vertices.toArray(new Integer[vertices.size()]));
            //System.out.println("added vertex = " + vi + " at distance = " + distance);
        }
        return dMap;
    }

    public Integer[] findAncestor(int v, int w) {
        Integer [] ad = new Integer[2];

        // dMap has distance as the key and the list of vectors at that distance as the values
        Map<Integer, Integer[]> dMapV = getDistanceMap(v, 1, new HashMap<Integer, Integer[]>());
        Map<Integer, Integer[]> dMapW = getDistanceMap(w, 1, new HashMap<Integer, Integer[]>());

        for(int distanceV: dMapV.keySet()) {
            Integer [] verticesV = dMapV.get(distanceV);

            for(int distanceW: dMapW.keySet())  {
                Integer [] verticesW = dMapW.get(distanceW);

                for(int vv: verticesV) {
                    for(int vw: verticesW) {
                        if(vv == vw) {
                            ad[0] = vv;
                            ad[1] = distanceV + distanceW;
                            return ad;
                        }
                    }
                }
            }
        }
        ad[0] = -1;
        ad[1] = -1;
        return ad;
    }

    public Integer[] findShortestAncestorInSets(Iterable<Integer> v, Iterable<Integer> w) {
        Integer [] shortestPath = new Integer[2];
        shortestPath[0] = 0;
        shortestPath[1] = 0;

        for(Integer vi : v) {
            for(Integer wi: w) {
                Integer [] path = findAncestor(vi, wi);

                if(path[1] == -1) // no path found
                    continue;
                else if(shortestPath[1] == 0)  // first path found, assign it
                    shortestPath = path;
                else if(path[1] < shortestPath[1]) // more optimum path between a tuple, replace shortest
                    shortestPath = path;
            }
        }

        if(shortestPath[1] == 0) {
            shortestPath[0] = -1;
            shortestPath[1] = -1;
            return shortestPath;
        }
        return shortestPath;
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        System.out.println("computing length");
        return findAncestor(v, w)[1];
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        System.out.println("computing ancestor");
        return findAncestor(v, w)[0];
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        return findShortestAncestorInSets(v, w)[1];
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        return findShortestAncestorInSets(v, w)[0];
    }

    // for unit testing of this class (such as the one below)
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
