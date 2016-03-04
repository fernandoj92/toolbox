package mt.ferjorosa.core.models;

import eu.amidst.core.models.BayesianNetwork;
import eu.amidst.core.models.DAG;
import mt.ferjorosa.core.models.ltdag.LTDAG;

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
public class LatentTreeModel implements Serializable{

    /** Represents the serial version ID for serializing the object. */
    private static final long serialVersionUID = 7107523324501381856L;

    /** Represents the Directed Acyclic Graph ({@link DAG}) defining the LCM graphical structure. */
    private LTDAG ltdag;

    /** Represents the equivalent Bayesian Network object that will be used for inference*/
    private BayesianNetwork equivalentBN;

    public LatentTreeModel(LTDAG dag) throws IllegalArgumentException{
            this.ltdag = dag;
            equivalentBN = new BayesianNetwork(ltdag.getDAG());
    }

    public LatentTreeModel(LTDAG dag, Random randomInitialization) throws IllegalArgumentException{
            this.ltdag = dag;
            equivalentBN = new BayesianNetwork(ltdag.getDAG());
            equivalentBN.randomInitialization(randomInitialization);
    }

}
