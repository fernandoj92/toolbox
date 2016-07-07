package mt.ferjorosa.core.learning.conceptdrift;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import mt.ferjorosa.core.learning.LTMLearningEngine;
import mt.ferjorosa.core.models.LTM;

/**
 * Created by Fernando on 7/7/2016.
 */
public class PageHinkley implements ConceptDriftMeasure{

    /** The user established factor. The gamma value for PH */
    private double fadingFactor;

    /** The delta value corresponding the tolerance parameter related to the magnitude of allowed changes */
    private double delta;

    /** The engine used to learn the model's parameters when a Concept Drift occurs. */
    private LTMLearningEngine ltmLearningEngine;


    @Override
    public double getFadingFactor() {
        return 0;
    }

    @Override
    public void setFadingFactor(double factor) {

    }

    @Override
    public ConceptDriftStates checkConceptDrift(LTM updatedModel, double modelBatchScore, DataOnMemory<DataInstance> batch) {

        LTM oneBatchModel = ltmLearningEngine.learnKnownStructureLTM(updatedModel.getLtdag(),batch);
        System.out.println("\n MODELO ONE-BATCH:");
        System.out.println(oneBatchModel.getLearntBayesianNetwork().toString());
        System.out.println("\n SCORE MODELO ONE-BATCH: "+oneBatchModel.getScore());

        if((oneBatchModel.getScore() + modelBatchScore) > fadingFactor)
            return ConceptDriftStates.CONCEPT_DRIFT;
        else
            return ConceptDriftStates.NONE;
    }
}
