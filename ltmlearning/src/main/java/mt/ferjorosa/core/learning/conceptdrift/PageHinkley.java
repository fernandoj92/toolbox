package mt.ferjorosa.core.learning.conceptdrift;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.learning.parametric.ParameterLearningAlgorithm;
import mt.ferjorosa.core.learning.LTMLearningEngine;
import mt.ferjorosa.core.models.LTM;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Fernando on 7/7/2016.
 */
public class PageHinkley implements ConceptDriftMeasure{

    /** The user established factor. The gamma value for PH */
    private double fadingFactor;

    /** The delta value corresponding the tolerance parameter related to the magnitude of allowed changes */
    private double delta;

    /** The user-specified gamma value */
    private double gamma;

    /** The engine used to learn the model's parameters when a Concept Drift occurs. */
    private LTMLearningEngine ltmLearningEngine;

    private ArrayList<Double> scoreDiffsSinceLastCD = new ArrayList<>();

    private ArrayList<Double> CumSinceLastCD = new ArrayList<>();

    private ArrayList<Double> PHValues = new ArrayList<>();

    private double minCumSinceLastCD = 0;


    @Override
    public double getFadingFactor() {
        return 0;
    }

    @Override
    public void setFadingFactor(double factor) {

    }

    public PageHinkley(double gamma, double delta, ParameterLearningAlgorithm parameterLearningAlgorithm){
        this.ltmLearningEngine = new LTMLearningEngine(parameterLearningAlgorithm);
        this.gamma = gamma;
        this.delta = delta;
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

    @Override
    public ConceptDriftStates checkConceptDriftPH(LTM updatedModel, double modelBatchScore, DataOnMemory<DataInstance> batch, int currentIteration, int iterationLastDrift) {

        LTM oneBatchModel = ltmLearningEngine.learnKnownStructureLTM(updatedModel.getLtdag(),batch);
        //System.out.println("\n MODELO ONE-BATCH:");
        //System.out.println(oneBatchModel.getLearntBayesianNetwork().toString());
        //System.out.println("\n SCORE MODELO ONE-BATCH: "+oneBatchModel.getScore());

        double currentScoreDiff = oneBatchModel.getScore() - modelBatchScore;
        scoreDiffsSinceLastCD.add(currentScoreDiff);
        double Cum = 0;
        for(Double score: scoreDiffsSinceLastCD){
            Cum += score / scoreDiffsSinceLastCD.size();
        }
        CumSinceLastCD.add(Cum);
        // Now we calculate the minimum Cum value
        minCumSinceLastCD = Collections.min(CumSinceLastCD);
        // Finally we calculate the PH value
        double currentPH = Cum - minCumSinceLastCD;
        // Store the PH value for test purposes
        PHValues.add(currentPH);
        System.out.println("PH ("+currentIteration+"): "+currentPH);
        if(currentPH > gamma) {
            System.out.println(currentIteration + ": DRIFT");
            return ConceptDriftStates.CONCEPT_DRIFT;
        }
        return ConceptDriftStates.NONE;
    }

    @Override
    public void reset(){
        scoreDiffsSinceLastCD = new ArrayList<>();
        CumSinceLastCD = new ArrayList<>();
        minCumSinceLastCD = 0;
    }
}
