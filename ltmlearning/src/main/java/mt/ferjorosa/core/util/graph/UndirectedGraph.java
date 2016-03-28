package mt.ferjorosa.core.util.graph;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TODO: Crear un metodo que obtenga el MWST a partir de un grafo no dirigido completo con los pesos de union
 */
public class UndirectedGraph {
    /** */
    private int nVertices;

    /** */
    private Map<Pair<Integer,Integer>,Double> edges;

    /** */
    private Set<Integer> adjacentEdges[];

    public UndirectedGraph(int nVertices){

        this.nVertices = nVertices;

        this.edges = new LinkedHashMap<>();

        //Initialize the 'adjacent edges' list
        this.adjacentEdges = new HashSet[nVertices];
        for(int i=0; i<nVertices; ++i)
            this.adjacentEdges[i] = new HashSet<>();

    }

    public Map<Pair<Integer,Integer>,Double> getEdges(){
        return this.edges;
    }

    public int getNVertices(){
        return this.nVertices;
    }

    public Set<Integer>[] getAdjacentEdges(){
        return this.adjacentEdges;
    }

    public void addEdge(int v,int w, double weight) {
        adjacentEdges[v].add(w);
        adjacentEdges[w].add(v);
        edges.put(Pair.of(v, w), weight);
    }

    public void removeEdge(int v, int w){
        adjacentEdges[v].remove(w);
        adjacentEdges[w].remove(v);
        edges.remove(Pair.of(v, w));
    }

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

    private boolean recursiveIsCyclic(int v, Boolean visited[], int parent){
        // Mark the current node as visited
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