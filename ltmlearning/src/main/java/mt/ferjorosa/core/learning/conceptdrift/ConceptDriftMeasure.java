package mt.ferjorosa.core.learning.conceptdrift;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import mt.ferjorosa.core.models.LTM;

/**
 * This interface defines the Concept Drift measure used in the LTM Learning Engine for streaming data.
 */
public interface ConceptDriftMeasure {

    /**
     * Returns the fading factor being used.
     * @return the fading factor being used.
     */
    double getFadingFactor();

    /**
     * Sets the fading factor.
     * @param factor a {@code double} that represents the fading factor.
     */
    void setFadingFactor(double factor);

    /**
     * Returns current Concept Drift state for the learnt model.
     * @param updatedModel current model that is going to be compared to a one-batch model to check for concept drifts.
     * @param modelBatchScore is the updated model's score for current batch.
     * @param batch the batch of data that is going to be used to check it.
     * @return the concept drift state.
     */
    ConceptDriftStates checkConceptDrift(LTM updatedModel, double modelBatchScore, DataOnMemory<DataInstance> batch);

    ConceptDriftStates checkConceptDriftPH(LTM updatedModel, double modelBatchScore, DataOnMemory<DataInstance> batch, int currentIteration, int iterationLastDrift);

    void reset();

}
