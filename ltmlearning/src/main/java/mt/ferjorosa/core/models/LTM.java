package mt.ferjorosa.core.models;

import eu.amidst.core.models.BayesianNetwork;
import mt.ferjorosa.core.models.ltdag.LTDAG;

/**
 * Created by Fer on 18/03/2016.
 */
public class LTM {

    private LTDAG ltdag;

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

    public LTDAG getLtdag(){
        return this.ltdag;
    }

    public LTM(BayesianNetwork learntModel, double modelScore, LTDAG ltdag){
        this.learntModel = learntModel;
        this.modelScore = modelScore;
        this.ltdag = ltdag;
    }

}
