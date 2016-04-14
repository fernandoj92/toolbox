package mt.ferjorosa.util;

import mt.ferjorosa.core.util.graph.DirectedTree;
import mt.ferjorosa.core.util.graph.UndirectedGraph;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Testing the {@link DirectedTree} class.
 */
public class DirectedTreeTest {

    private UndirectedGraph undirectedTree;

    @Before
    public void createUndirectedGraph(){

        /* First creates an undirected graph */
        UndirectedGraph completeGraph = new UndirectedGraph(5);

        completeGraph.addEdge(0, 1, 5.5);
        completeGraph.addEdge(0, 2, 4.0);
        completeGraph.addEdge(0, 3, 6.0);
        completeGraph.addEdge(0, 4, 9.0);
        completeGraph.addEdge(1, 2, 2.0);
        completeGraph.addEdge(1, 3, 1.5);
        completeGraph.addEdge(1, 4, 8.5);
        completeGraph.addEdge(2, 3, 9.0);
        completeGraph.addEdge(2, 4, 5.5);
        completeGraph.addEdge(3, 4, 8.5);

        /* Obtains the MaxWST */
        undirectedTree = UndirectedGraph.obtainMaximumWeightSpanningTree(completeGraph);
    }

    @Test
    public void testConstructor(){

        DirectedTree directedTree = new DirectedTree(undirectedTree,0);

        /* Manually checks the tree edges */

        Map<Integer,Map<Integer,Double>> edges = new HashMap<>();

        Map edgesFrom_0 = new HashMap<Integer, Double>();
        edgesFrom_0.put(4, 9.0);
        Map edgesFrom_4 = new HashMap<Integer, Double>();
        edgesFrom_4.put(1, 8.5);
        edgesFrom_4.put(3, 8.5);
        Map edgesFrom_3 = new HashMap<Integer, Double>();
        edgesFrom_3.put(2, 9.0);

        edges.put(0, edgesFrom_0);
        edges.put(4, edgesFrom_4);
        edges.put(3, edgesFrom_3);

        Assert.assertEquals(directedTree.getEdgesWithWeights(), edges);
    }
}
