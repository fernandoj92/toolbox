package mt.ferjorosa.core.models;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.learning.parametric.ParameterLearningAlgorithm;
import eu.amidst.core.models.BayesianNetwork;

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

    /** The parameter learning algorihtm instance that stores the model and its score. */
    private ParameterLearningAlgorithm parameterLearningAlgorithm;

    /** The score returned by the model for the last batch of data */
    private double lastBatchScore;

    /** When calling updateModel() it is possible to update*/
    private double updatedScore;

    /**
     * Creates an instance of the class by passing all its required components.
     * @param ltdag the LTM structure.
     * @param parameterLearningAlgorithm stores the fully learnt model and its score. It will also allow us to update them
     *                                   with new batches.
     */
    public LTM(LTDAG ltdag, ParameterLearningAlgorithm parameterLearningAlgorithm){
        this.parameterLearningAlgorithm = parameterLearningAlgorithm;
        this.parameterLearningAlgorithm.setDAG(ltdag.getDAG());
        this.ltdag = ltdag;
        this.updatedScore = parameterLearningAlgorithm.getLogMarginalProbability();
    }

    /**
     * Returns the LTM score for a series of data batches (updates).
     * @return the model's score.
     */
    public double getScore(){
        return this.updatedScore;
    }

    /**
     * Returns the LTM score of the last batch used for its learning.
     * @return the last batch's score.
     */
    public double getLastBatchScore(){
        return this.lastBatchScore;
    }

    /**
     * Returns the fully learnt LTM, which is a Bayesian network.
     * @return the fully learnt model.
     */
    public BayesianNetwork getLearntBayesianNetwork(){
        return this.parameterLearningAlgorithm.getLearntBayesianNetwork();
    }

    /**
     * Returns the LTM structure represented as a 'LTDAG' object.
     * @return the 'LTDAG' object.
     */
    public LTDAG getLtdag(){
        return this.ltdag;
    }

    /**
     * Updates current model by learning its parameters with a new batch. Depending on the parameter algorithm being
     * used, it would take into consideration previous data or not (For example,
     * {@link eu.amidst.core.learning.parametric.bayesian.SVB} does it).
     *
     * This method should not call initLearning() because that would reset previous knowledge, so its advisable to only
     * use this method after an initial learning of the parameters (for example generating the LTM with LTMLearningEngine or the
     * Approximate Bridged Islands Algorithm).
     *
     * @param batch a {@link DataOnMemory} object that is going to be used to learn the model.
     */
    public double updateModel(DataOnMemory<DataInstance> batch){

        // Updates the LTM parameters and updates its score
        double sum = 0;
        for (DataOnMemory<DataInstance> windowSizeBatch : batch.iterableOverBatches(100)){
            sum += parameterLearningAlgorithm.updateModel(windowSizeBatch);
        }

        // Adds the batch score to the model's score
        this.updatedScore += sum;

        // Stores the batch score
        this.lastBatchScore = sum;

        // Returns the score for the passed batch
        return sum;
    }
}
