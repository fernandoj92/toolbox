package mt.ferjorosa.core.models;

import eu.amidst.core.models.BayesianNetwork;

/**
 * Created by Fer on 18/03/2016.
 */
public class LTM {

    /** Represents the learnt Bayesian Network*/
    private BayesianNetwork learntModel;

    /** Represents the score of the learned model*/
    private double modelScore;

    /**
     *
     * @return
     */
    public double getScore(){
        return this.modelScore;
    }

    /**
     *
     * @return
     */
    public BayesianNetwork getLearntModel(){
        return this.learntModel;
    }

    public LTM(BayesianNetwork learntModel, double modelScore){
        this.learntModel = learntModel;
        this.modelScore = modelScore;
    }

}
