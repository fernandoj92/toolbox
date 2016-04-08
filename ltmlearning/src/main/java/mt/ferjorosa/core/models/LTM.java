package mt.ferjorosa.core.models;

import eu.amidst.core.models.BayesianNetwork;
import mt.ferjorosa.core.models.ltdag.LTDAG;

/**
 * Latent Tree Model class that is returned by the Learning Engines. It is structure is fully represented by the
 * LTDAG class. It also contains a Bayesian network object, representing the fully learnt model and finally the
 * model score value (depends on the parameter learning algorithm used) for the set of data instances used to
 * learn it.
 *
 * This class purpose is to group all the LTM components while maintaining its consonance with the core
 * module and wrappers.
 */
public class LTM {

    /** Represents the LTM structure. */
    private LTDAG ltdag;

    /** Represents the learnt Bayesian network. */
    private BayesianNetwork learntModel;

    /** Represents the score of the learned model (depends on the parameter learning algorithm). */
    private double modelScore;

    /**
     * Creates an instance of the class by passing all its required components.
     * @param learntModel the fully learnt model.
     * @param modelScore the model's score.
     * @param ltdag the LTM structure.
     */
    public LTM(BayesianNetwork learntModel, double modelScore, LTDAG ltdag){
        this.learntModel = learntModel;
        this.modelScore = modelScore;
        this.ltdag = ltdag;
    }

    /**
     * Returns the LTM score for a specific set of data instances.
     * @return the model's score.
     */
    public double getScore(){
        return this.modelScore;
    }

    /**
     * Returns the fully learnt LTM, which is a Bayesian network.
     * @return the fully learnt model.
     */
    public BayesianNetwork getLearntBayesianNetwork(){
        return this.learntModel;
    }

    /**
     * Returns the LTM structure represented as a 'LTDAG' object.
     * @return the 'LTDAG' object.
     */
    public LTDAG getLtdag(){
        return this.ltdag;
    }

}
