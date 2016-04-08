package mt.ferjorosa.core.learning;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.learning.parametric.ParameterLearningAlgorithm;
import mt.ferjorosa.core.learning.conceptdrift.ConceptDriftStates;
import mt.ferjorosa.core.learning.conceptdrift.FadingLearner;
import mt.ferjorosa.core.learning.structural.StructuralLearning;
import mt.ferjorosa.core.models.LTM;

/**
 * This class re
 *
 * TODO: Revisar el nombre del interfaz de Fading learner y tmb el nombre del atributo de dicho tipo de esta clase.
 * TODO: No me gusta el hecho de tener el engine de aprender los LTM combinado con el ABI
 */
public class StreamingLTMLearningEngine {

    /** */
    private FadingLearner conceptDriftMeasure;

    /** */
    private DataStream<DataInstance> dataStream;

    /** */
    private StructuralLearning structuralLearningAlgorithm;

    /** */
    private LTMLearningEngine ltmLearningEngine;

    /** */
    private LTM currentModel;

    /** */
    private int batchSize = 1000;

    /**
     *
     * @param conceptDriftMeasure
     */
    public void setConceptDriftMeasure(FadingLearner conceptDriftMeasure){
        this.conceptDriftMeasure = conceptDriftMeasure;
    }

    /**
     *
     * @param dataStream a {@link DataStream} object.
     */
    public void setDataStream(DataStream<DataInstance> dataStream) {
        this.dataStream = dataStream;
    }

    /**
     *
     * @param structuralLearningAlgorithm
     */
    public void setStructuralLearningAlgorithm(StructuralLearning structuralLearningAlgorithm){
        this.structuralLearningAlgorithm = structuralLearningAlgorithm;
    }

    /**
     *
     * @param batch
     */
    public void initLearning(DataOnMemory<DataInstance> batch){
        // En este caso el initLearning lo que hace es pasar un primer batch de instancias que son los que serviran
        // para poder aprender el LTM inicial y que sera posteriormente modificado segun se vayan produciendo
        // concept drifts.
        this.currentModel = structuralLearningAlgorithm.learnModel(batch);
    }

    /**
     *
     */
    public void runLearning() {
        // Con cada batch de instancias que le lleguen se comprueba que no exista un concept drift
        // mediante la comparación de la measure con un factor establecido, dependiendo de como se
        // supere dicho factor se  producir un CONCEPT_DRIFT o un CONCEPT_SHIFT
        for (DataOnMemory<DataInstance> batch : dataStream.iterableOverBatches(batchSize)){
            ConceptDriftStates state = conceptDriftMeasure.checkConceptDrift(currentModel);
            switch (state){

                case CONCEPT_SHIFT:
                    this.currentModel = structuralLearningAlgorithm.learnModel(batch);

                case CONCEPT_DRIFT:
                    this.currentModel = ltmLearningEngine.learnKnownStructureLTM(currentModel.getLtdag(),batch);

                default : break;
            }

        }

    }
}


