package mt.ferjorosa.core.util.graph;

import mt.ferjorosa.core.util.pair.SymmetricPair;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class that represents a Directed Tree, used for representing connections between LTVariables
 * when a structural algorithm is being used to create a Latent Tree Model
 * (i.e @link mt.ferjorosa.core.learning.structural.ApproximateBIAlgorithm}).
 */
public class DirectedTree {

    /** The index of the root node of the tree. */
    private int rootIndex;

    /** Set of directed edges, represented as a Map. */
    private Map<Integer,Map<Integer,Double>> edges;

    /**
     * Creates a directed tree from an undirected graph.
     * @param graph the base graph being required.
     * @param rootIndex the index of the node that is going to be the root.
     */
    public DirectedTree(UndirectedGraph graph, int rootIndex){
        this.edges = new HashMap<>();
        this.rootIndex = rootIndex;
        createEdges(graph, rootIndex);
    }

    /**
     * Returns a Map containing the set of directed edges of the tree.
     * @return a Map containing the set of directed edges of the tree.
     */
    public Map<Integer, Integer> getEdges(){
        Map<Integer, Integer> onlyEdges = new HashMap<>();

        for(Integer parent: edges.keySet())
            for(Integer son: edges.get(parent).keySet())
                onlyEdges.put(parent, son);

        return onlyEdges;
    }

    /**
     * Returns a Map containing the set of edges with their associated weights.
     * @return a Map containing the set of edges with their associated weights
     */
    public Map<Integer,Map<Integer,Double>> getEdgesWithWeights(){
        return this.edges;
    }

    /**
     *
     * @param graph
     * @param index
     */
    private void createEdges(UndirectedGraph graph, int index){
        Boolean visited[] = new Boolean[graph.getNVertices()];
        for (int i = 0; i < visited.length; i++)
            visited[i] = false;

        // Recursively add the directed edges
        addEdgesRecursively(graph, visited, index);
    }

    /**
     * Private method that allows to add new edges recursively to the tree by traversing an undirected graph.
     * @param graph the undirected graph being traversed.
     * @param index the node's index.
     */
    private void addEdgesRecursively(UndirectedGraph graph, Boolean[] visited, int index){
        // Marks current node as visited
        visited[index] = true;

        for(Integer i :  graph.getAdjacentEdges()[index]){
            if (!visited[i]) {
                addEdge(index, i, graph.getEdges().get(new SymmetricPair<>(index, i)));
                addEdgesRecursively(graph, visited, i);
            }
        }
    }

    /**
     * Private method being used inside its recursive variant to add new edges.
     * @param parent the parent's node index.
     * @param son the index of the node receiving the edge.
     * @param weight the associated weight.
     */
    private void addEdge(int parent, int son, double weight){
        Map sonAndValue = new HashMap<Integer, Double>();
        sonAndValue.put(son, weight);
        edges.put(parent, sonAndValue);
    }

}
