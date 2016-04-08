package mt.ferjorosa.core.learning.conceptdrift;

import eu.amidst.core.inference.InferenceAlgorithm;
import mt.ferjorosa.core.models.LTM;

/**
 * TODO: el fading factor deberia ser la likelihood que se va a tomar de referencia
 */
public class LikelihoodFading implements FadingLearner{

    /** */
    private double fadingFactor;

    /** */
    private InferenceAlgorithm inferenceEngine;

    /**
     *
     * @param inferenceEngine
     */
    public LikelihoodFading(InferenceAlgorithm inferenceEngine){
        this.inferenceEngine = inferenceEngine;
    }

    /**
     * {@inheritDoc}
     */
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

    // Aqui ejecutamos la inferencia y comprobamos la diferencia entre la log-likelihood del modelo actual
    // y la comparamos con la del nuevo LTM aprendido, en caso de que la del nuevo sea X superior a la del antiguo
    // se producira un CONCEPT_DRIFT o un CONCEPT_SHIFT
    /**
     * {@inheritDoc}
     */
    @Override
    public ConceptDriftStates checkConceptDrift(LTM learntModel){
        if( 0 > fadingFactor)
            return ConceptDriftStates.CONCEPT_DRIFT;
        if( 1 > fadingFactor)
            return ConceptDriftStates.CONCEPT_SHIFT;
        else
            return ConceptDriftStates.NONE;
    }


}
