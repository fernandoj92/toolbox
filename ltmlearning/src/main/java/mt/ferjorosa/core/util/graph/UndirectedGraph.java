package mt.ferjorosa.core.util.graph;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class that represents an Undirected Graph, used for representing connections between LTVariables
 * when a structural algorithm is being used to create a Latent Tree Model
 * (i.e @link mt.ferjorosa.core.learning.structural.ApproximateBIAlgorithm}).
 */
public class UndirectedGraph {

    /** Number of vertices of the graph. */
    private int nVertices;

    /** Set of undirected edges, represented as a Map. */
    private Map<Pair<Integer,Integer>,Double> edges;

    /** Set of adjacent nodes of a node. */
    private Set<Integer> adjacentEdges[];

    /**
     * Creates an empty undirected graph with a specific number of nodes.
     * @param nVertices number of nodes that is going to have the graph.
     */
    public UndirectedGraph(int nVertices){

        this.nVertices = nVertices;

        this.edges = new LinkedHashMap<>();

        //Initialize the 'adjacent edges' list
        this.adjacentEdges = new HashSet[nVertices];
        for(int i=0; i<nVertices; ++i)
            this.adjacentEdges[i] = new HashSet<>();

    }

    /**
     * Calculates the Maximum Weight Spanning Tree
     * Note: the passed graph should be complete.
     * @param graph the base graph used to calculate the Maximum Weight Spanning Tree.
     * @return the Maximum Weight Spanning Tree.
     */
    public static UndirectedGraph obtainMaximumWeightSpanningTree(UndirectedGraph graph){

        // First we sort the edges by its weight, from big to small
        Map<Pair<Integer,Integer>, Double> sortedEdges = graph.getEdges().entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        UndirectedGraph mwst = new UndirectedGraph(graph.getNVertices());
        int nEdges = 0;

        // Iterate through the edges and add them one by one to the tree and checking that there are no cycles
        for(Map.Entry entry: sortedEdges.entrySet()){
            Pair<Integer,Integer> key = (Pair<Integer,Integer>) entry.getKey();
            Double value = (Double) entry.getValue();

            mwst.addEdge(key.getLeft(),key.getRight(),value);
            nEdges++;
            if(mwst.isCyclic()) {
                mwst.removeEdge(key.getLeft(), key.getRight());
                nEdges--;
            }
            // If the number of edges in the tree is equal to nVertices - 1, there is no need to add more edges
            if(nEdges == (graph.getNVertices() - 1))
                return mwst;
        }

        return mwst;
    }

    /**
     * Returns the graph's set of edges with their associated weights.
     * @return the graph's set of edges with their associated weights.
     */
    public Map<Pair<Integer,Integer>,Double> getEdges(){
        return this.edges;
    }

    /**
     * Returns the number of nodes contained in the graph.
     * @return the number of nodes contained in the graph.
     */
    public int getNVertices(){
        return this.nVertices;
    }

    /**
     * Returns a set of arrays containing the indexes of each node's adjacent companions.
     * @return a set of arrays containing the indexes of each node's adjacent companions.
     */
    public Set<Integer>[] getAdjacentEdges(){
        return this.adjacentEdges;
    }

    /**
     * Creates a new edge between two nodes.
     * @param parent left side of the edge.
     * @param son right side of the edge.
     * @param weight the weight of the edge.
     */
    public void addEdge(int parent,int son, double weight) {
        if(parent < 0 || son < 0 || parent >= nVertices || son > nVertices)
            throw new IllegalArgumentException("Illegal edge parameters.");

        adjacentEdges[parent].add(son);
        adjacentEdges[son].add(parent);
        edges.put(Pair.of(parent, son), weight);
    }

    /**
     * Removes an edge.
     * @param parent the parent node index.
     * @param son the son node index.
     */
    public void removeEdge(int parent, int son){
        if(parent < 0 || son < 0 || parent >= nVertices || son > nVertices)
            throw new IllegalArgumentException("Illegal edge parameters.");

        adjacentEdges[parent].remove(son);
        adjacentEdges[son].remove(parent);
        edges.remove(Pair.of(parent, son));
    }

    /**
     * Checks if there is a cycle in the graph.
     * @return a boolean indicating if this graph contains cycles (true) or not (false).
     */
    public boolean isCyclic(){

        Boolean visited[] = new Boolean[nVertices];
        for (int i = 0; i < nVertices; i++)
            visited[i] = false;

        // Recursively check if exists a cycle in each DFS Tree
        for (int vertix = 0; vertix < nVertices; vertix++)
            if (!visited[vertix])
                if (recursiveIsCyclic(vertix, visited, -1))
                    return true;

        return false;
    }

    /**
     * Private method used inside its non-recursive companion method.
     * @param v de node's index.
     * @param visited an array containing a list of boolean values, where its index coincide with the node's index.
     * @param parent the parent node index.
     * @return a boolean value representing if an specific part of the graph contains a cycle.
     */
    private boolean recursiveIsCyclic(int v, Boolean visited[], int parent){
        // Marks current node as visited
        visited[v] = true;

        for(Integer i : adjacentEdges[v]){
            // If an adjacent is not visited, then recur for that
            // adjacent
            if (!visited[i])
            {
                if (recursiveIsCyclic(i, visited, v))
                    return true;
            }

            // If an adjacent is visited and not parent of current
            // vertex, then there is a cycle.
            else if (i != parent)
                return true;
        }
        return false;
    }

}