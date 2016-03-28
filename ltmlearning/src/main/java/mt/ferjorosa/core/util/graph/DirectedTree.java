package mt.ferjorosa.core.util.graph;

import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fernando on 28/03/2016.
 */
public class DirectedTree {

    /** */
    private int rootIndex;
    /** */
    private Map<Integer,Map<Integer,Double>> edges;

    public DirectedTree(UndirectedGraph graph, int rootIndex){
        this.edges = new HashMap<>();
        addEdgesRecursively(graph, rootIndex);
    }

    private void addEdgesRecursively(UndirectedGraph graph, int index){
        for(Integer i :  graph.getAdjacentEdges()[index]){
            addEdge(index, i, graph.getEdges().get(Pair.of(index,i)));
            addEdgesRecursively(graph, i);
        }
    }

    private void addEdge(int parent, int son, double value){
        Map sonAndValue = new HashMap<Integer, Double>();
        sonAndValue.put(son, value);
        edges.put(parent, sonAndValue);
    }



}
