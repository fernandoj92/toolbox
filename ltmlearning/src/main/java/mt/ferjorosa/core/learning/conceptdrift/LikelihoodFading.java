package mt.ferjorosa.core.learning.conceptdrift;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.inference.InferenceAlgorithm;
import eu.amidst.core.learning.parametric.ParameterLearningAlgorithm;
import mt.ferjorosa.core.learning.LTMLearningEngine;
import mt.ferjorosa.core.models.LTM;

/**
 * This class compares the scores of current model, after learning a new batch of data, and another model with the same
 * structure but with parameters only influenced by this new batch, not by older data like the current model.
 *
 * The user can establish a value called "fading factor" that will be evaluated with the difference of score between models.
 */
public class LikelihoodFading implements ConceptDriftMeasure {

    /** The user established factor. The models score difference (the likelihood in this case) will be compared to this factor. */
    private double fadingFactor;

    /** The engine used to learn the model's parameters when a Concept Drift occurs. */
    private LTMLearningEngine ltmLearningEngine;

    /**
     * Creates an instance of this measure by passing the parameter learning algorithm used to learn
     * the one-batch model that is going to be compared with the updated model in the concept drift check.
     */
    public LikelihoodFading(ParameterLearningAlgorithm parameterLearningAlgorithm){
        this.ltmLearningEngine = new LTMLearningEngine(parameterLearningAlgorithm);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getFadingFactor(){
        return fadingFactor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFadingFactor(double factor) {
        this.fadingFactor = factor;
    }

    /**
     * Returns current Concept Drift state for the currently learnt model.
     * @param updatedModel current model that is going to be compared to a one-batch model to check for concept drifts.
     * @param modelBatchScore is the updated model's score for current batch.
     * @param batch the batch of data that is going to be used to check it.
     * @return the concept drift state.
     */
    @Override
    public ConceptDriftStates checkConceptDrift(LTM updatedModel, double modelBatchScore, DataOnMemory<DataInstance> batch){

        LTM oneBatchModel = ltmLearningEngine.learnKnownStructureLTM(updatedModel.getLtdag(),batch);
        System.out.println("\n MODELO ONE-BATCH:");
        System.out.println(oneBatchModel.getLearntBayesianNetwork().toString());
        System.out.println("\n SCORE MODELO ONE-BATCH: "+oneBatchModel.getScore());

        if((oneBatchModel.getScore() + modelBatchScore) > fadingFactor)
            return ConceptDriftStates.CONCEPT_DRIFT;
        else
            return ConceptDriftStates.NONE;
    }

    @Override
    public ConceptDriftStates checkConceptDriftPH(LTM updatedModel, double modelBatchScore, DataOnMemory<DataInstance> batch, int currentIteration, int iterationLastDrift) {
        return null;
    }

    @Override
    public void reset() {

    }
}
