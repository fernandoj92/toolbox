package mt.ferjorosa.models;

import eu.amidst.core.models.BayesianNetwork;
import eu.amidst.core.models.DAG;

import java.io.Serializable;
import java.util.Random;

/**
 * Tree-structured graphical model with latent variables. Leaf nodes are observed variables while
 * internal nodes can be either observed or latent.
 *
 * In an LTM, each Latent Variable represent a partition of the data.
 *
 * This class will wrap some functionalities from the BayesianNetwork class and add its own methods. This way
 * it will not interfere with the developed code
 */
public class LatentClusterModel implements Serializable{

    /** Represents the serial version ID for serializing the object. */
    private static final long serialVersionUID = 7107523324501381856L;

    /** Represents the Directed Acyclic Graph ({@link DAG}) defining the LCM graphical structure. */
    private DAG dag;

    /** Represents the equivalent Bayesian Network object that will be used for inference*/
    private BayesianNetwork equivalentBN;

    public LatentClusterModel(DAG dag) throws IllegalArgumentException{
        // Check if the DAG structure follows the LCM structure
        if(isLCM(dag)) {
            this.dag = dag;
            equivalentBN = new BayesianNetwork(dag);
        }else
            throw new IllegalArgumentException("DAG must follow the LCM structure");
    }

    public LatentClusterModel(DAG dag, Random randomInitialization) throws IllegalArgumentException{
        // Check if the DAG structure follows the LCM structure
        if(isLCM(dag)) {
            this.dag = dag;
            equivalentBN = new BayesianNetwork(dag);
            equivalentBN.randomInitialization(randomInitialization);
        }else
            throw new IllegalArgumentException("DAG must follow the LCM structure");
    }

    private boolean isLCM(DAG dag){
        //Solo puede existir una variable oculta y dicha variable debe de ser el padre de todas las demás
        return true;
    }
}
