package mt.ferjorosa.core.learning;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.datastream.DataStream;
import mt.ferjorosa.core.learning.conceptdrift.FadingLearner;
import mt.ferjorosa.core.learning.structural.StructuralLearning;
import mt.ferjorosa.core.models.LTM;

/**
 *
 */
public class StreamingLTMLearningEngine {

    /** */
    protected FadingLearner conceptDriftMeasure;

    /** */
    protected DataStream<DataInstance> dataStream;

    /** */
    protected StructuralLearning LTMLearningEngine;

    /** */
    protected LTM latentTreeModel;

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
     * @param ltmLearningEngine
     */
    public void setLTMLearningEngine(StructuralLearning ltmLearningEngine){
        this.LTMLearningEngine = ltmLearningEngine;
    }

    public void initLearning(DataOnMemory<DataInstance> batch){
        // En este caso el initLearning lo que hace es pasar un primer batch de instancias que son los que serviran
        // para poder aprender el LTM inicial y que sera posteriormente modificado segun se vayan produciendo
        // concept drifts.
        this.latentTreeModel = LTMLearningEngine.learnModel(batch);
    }

    /**
     *
     */
    public void runLearning() {
        // Con cada batch de instancias que le lleguen se comprueba que no exista un concept drift
        // mediante la comparación de la measure con un factor establecido, dependiendo de como se
        // supere dicho factor se  producir un CONCEPT_DRIFT o un CONCEPT_SHIFT


    }
}


