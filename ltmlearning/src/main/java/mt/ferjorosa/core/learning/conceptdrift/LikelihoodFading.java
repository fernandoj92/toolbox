package mt.ferjorosa.core.learning.conceptdrift;

import eu.amidst.core.inference.InferenceAlgorithm;

/**
 * Created by Fernando on 14/03/2016.
 */
public class LikelihoodFading implements FadingLearner{

    /**
     *
     */
    private double fadingFactor;

    /**
     *
     */
    private InferenceAlgorithm inferenceEngine;

    public double getFadingFactor(){
        return fadingFactor;
    }

    @Override
    public void setFadingFactor(double factor) {
        this.fadingFactor = factor;
    }

    public LikelihoodFading(InferenceAlgorithm inferenceEngine){
        this.inferenceEngine = inferenceEngine;
    }

    // Aqui ejecutamos la inferencia y comprobamos la diferencia entre la log-likelihood del modelo actual
    // y la comparamos con la del nuevo LTM aprendido, en caso de que la del nuevo sea X superior a la del antiguo
    // se producira un CONCEPT_DRIFT o un CONCEPT_SHIFT
    public ConceptDriftStates checkConceptDrift(){
        if( 0 > fadingFactor)
            return ConceptDriftStates.CONCEPT_DRIFT;
        if( 1 > fadingFactor)
            return ConceptDriftStates.CONCEPT_SHIFT;
        else
            return ConceptDriftStates.NONE;
    }


}
