package mt.ferjorosa.core.util.exportBN.format.json;

import eu.amidst.core.models.BayesianNetwork;
import eu.amidst.core.models.DAG;

import java.util.List;

/**
 * Created by Fernando on 27/08/2016.
 */
public class CytoscapeJsonFormatBN {

    private Element elements;

    public CytoscapeJsonFormatBN(BayesianNetwork bayesianNetwork){

        DAG dag = bayesianNetwork.getDAG();


    }

    /** ------------------------------------------------------------------------------------------------------------- */

    private class Element{

        private List<Node> nodes;
        private List<Edge> edges;

        public Element(List<Node> nodes, List<Edge> edges){
            this.nodes = nodes;
            this.edges = edges;
        }
    }

    private class Node{

        private NodeData data;

        public Node(NodeData data){
            this.data = data;
        }

        private class NodeData{
            private String id;

            public NodeData(String id){
                this.id = id;
            }
        }
    }

    private class Edge{

        private EdgeData data;

        public Edge(EdgeData data){
            this.data = data;
        }

        private class EdgeData{

            private String source;
            private String target;

            public EdgeData(String source, String target){
                this.source = source;
                this.target = target;
            }
        }
    }

}