package mt.ferjorosa.core.learning.conceptdrift;

/**
 * Created by Fernando on 14/03/2016.
 */
public interface FadingLearner {

    void setFadingFactor(double factor);

    ConceptDriftStates checkConceptDrift();
}
