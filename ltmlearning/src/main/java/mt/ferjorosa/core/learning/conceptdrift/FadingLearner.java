package mt.ferjorosa.core.learning.conceptdrift;

import mt.ferjorosa.core.models.LTM;

/**
 * This interface defines the Fading Learner.
 */
public interface FadingLearner {

    /**
     * Sets the fading factor.
     * @param factor a {@code double} that represents the fading factor.
     */
    void setFadingFactor(double factor);

    /**
     * Returns current Concept Drift state.
     * @param learntModel
     * @return the concept drift state.
     */
    ConceptDriftStates checkConceptDrift(LTM learntModel);
}
