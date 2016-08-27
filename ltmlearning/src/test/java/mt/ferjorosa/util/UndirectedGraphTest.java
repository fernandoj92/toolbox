package mt.ferjorosa.util;


import mt.ferjorosa.core.util.graph.UndirectedGraph;
import org.junit.Assert;
import org.junit.Test;

/**
 * Testing the {@link UndirectedGraph} class.
 */
public class UndirectedGraphTest {

    @Test
    public void testConstructor(){

        int nVertices = 6;
        UndirectedGraph graph = new UndirectedGraph(nVertices);

        /* Checks the number of vertices is the same as in its creation */

        Assert.assertEquals(graph.getNVertices(), nVertices);
        Assert.assertEquals(graph.getAdjacentEdges().length, nVertices);
    }

    @Test
    public void testIsCyclic(){

        /* Creates an undirected graph with 4 vertices */

        UndirectedGraph graph = new UndirectedGraph(4);

        /* Adds 3 new edges that form a cycle (the weights don't affect) and therefore should return true to isCyclic() */

        graph.addEdge(0, 1, 5.50);
        graph.addEdge(1, 2, 4.60);
        graph.addEdge(0, 2, 3.70);

        Assert.assertTrue(graph.isCyclic());

        /* Removes one edge and therefore it doesn't have a cycle anymore */

        graph.removeEdge(0,1);

        Assert.assertFalse(graph.isCyclic());
    }

    @Test
    public void testMaximumSpanningTree(){

        /* First creates a complete undirected graph */
        UndirectedGraph completeGraph = new UndirectedGraph(4);

        completeGraph.addEdge(0, 1, 5.5);
        completeGraph.addEdge(0, 2, 4.0);
        completeGraph.addEdge(0, 3, 6.0);
        completeGraph.addEdge(1, 2, 7.0);
        completeGraph.addEdge(1, 3, 3.5);
        completeGraph.addEdge(2, 3, 1.5);

        /* Obtains the MWST from the complete graph */

        UndirectedGraph obtainedMWST = UndirectedGraph.obtainMaximumWeightSpanningTree(completeGraph);

        /* Manually creates the MWST to compare it with the obtained one */

        UndirectedGraph manualMWST = new UndirectedGraph(4);

        manualMWST.addEdge(1, 2, 7.0);
        manualMWST.addEdge(0, 3, 6.0);
        manualMWST.addEdge(0, 1, 5.5);

        Assert.assertEquals(manualMWST, obtainedMWST);
        Assert.assertEquals(manualMWST.hashCode(), obtainedMWST.hashCode());
    }
    @Test
    public void testMinimumSpanningTree(){

        /* First creates a complete undirected graph */
        UndirectedGraph completeGraph = new UndirectedGraph(4);

        completeGraph.addEdge(0, 1, 5.5);
        completeGraph.addEdge(0, 2, 4.0);
        completeGraph.addEdge(0, 3, 6.0);
        completeGraph.addEdge(1, 2, 7.0);
        completeGraph.addEdge(1, 3, 3.5);
        completeGraph.addEdge(2, 3, 1.5);

        /* Obtains the MinWST from the complete graph */

        UndirectedGraph obtainedMWST = UndirectedGraph.obtainMinimumWeightSpanningTree(completeGraph);

        /* Manually creates the MinWST to compare it with the obtained one */

        UndirectedGraph manualMWST = new UndirectedGraph(4);

        manualMWST.addEdge(2, 3, 1.5);
        manualMWST.addEdge(1, 3, 3.5);
        manualMWST.addEdge(0, 2, 4.0);

        Assert.assertEquals(manualMWST, obtainedMWST);
        Assert.assertEquals(manualMWST.hashCode(), obtainedMWST.hashCode());
    }
}
