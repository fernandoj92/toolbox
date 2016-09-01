package mt.ferjorosa.core.util.exportBN.format.json;

import eu.amidst.core.models.BayesianNetwork;
import eu.amidst.core.models.DAG;
import eu.amidst.core.models.ParentSet;
import eu.amidst.core.variables.Variable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fernando on 27/08/2016.
 */
public class CytoscapeJsonFormatBN {

    private Elements elements;

    public CytoscapeJsonFormatBN(BayesianNetwork bayesianNetwork){

        DAG dag = bayesianNetwork.getDAG();

        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();

        for(Variable var : dag.getVariables().getListOfVariables()){
            nodes.add(new Node(var.getVarID() + ""));
        }

        for(ParentSet parent : dag.getParentSets()){
            for(Variable var : parent.getParents())
                edges.add(new Edge(parent.getMainVar().getVarID()+"",var.getVarID()+""));
        }

        this.elements = new Elements(nodes,edges);
    }

    /** ------------------------------------------------------------------------------------------------------------- */

    private class Elements {

        private List<Node> nodes;
        private List<Edge> edges;

        public Elements(List<Node> nodes, List<Edge> edges){
            this.nodes = nodes;
            this.edges = edges;
        }
    }

    private class Node{

        private NodeData data;

        public Node(String id){
            this.data = new NodeData(id);
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

        public Edge(String source, String target){
            this.data = new EdgeData(source,target);
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