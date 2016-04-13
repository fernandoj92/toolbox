package mt.ferjorosa.core.learning.conceptdrift;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.inference.InferenceAlgorithm;
import mt.ferjorosa.core.models.LTM;

/**
 * This class
 * TODO: el fading factor deberia ser la likelihood que se va a tomar de referencia
 */
public class LikelihoodFading implements ConceptDriftMeasure {

    /** The user established factor. The model's measure score (the likelihood in this case) will be compared to this factor. */
    private double fadingFactor;

    /** The inference engine being used to calculate the likelihood.*/
    private InferenceAlgorithm inferenceEngine;

    /**
     * Creates an instance of this measure by passing the inference engine used to calculate the likelihood of the model
     * for current batch of data.
     * @param inferenceEngine the inference engine used to calculate the likelihood of the data for current model.
     */
    public LikelihoodFading(InferenceAlgorithm inferenceEngine){
        this.inferenceEngine = inferenceEngine;
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

    // Aqui ejecutamos la inferencia y comprobamos la diferencia entre la log-likelihood del modelo actual
    // y la comparamos con la del nuevo LTM aprendido, en caso de que la del nuevo sea X superior a la del antiguo
    // se producira un CONCEPT_DRIFT o un CONCEPT_SHIFT
    /**
     * Returns current Concept Drift state for the currently learnt model.
     * @param model learnt model being checked
     * @param batch the batch of data that is going to be used to check it.
     * @return the concept drift state.
     */
    @Override
    public ConceptDriftStates checkConceptDrift(LTM model, DataOnMemory<DataInstance> batch){
        // Sets the model
        this.inferenceEngine.setModel(model.getLearntBayesianNetwork());

        // Sets the evidence

        // Runs the inference

        if( 0 > fadingFactor)
            return ConceptDriftStates.CONCEPT_DRIFT;
        if( 1 > fadingFactor)
            return ConceptDriftStates.CONCEPT_SHIFT;
        else
            return ConceptDriftStates.NONE;
    }


}
