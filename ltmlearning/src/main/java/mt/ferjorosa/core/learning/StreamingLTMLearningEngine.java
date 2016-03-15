package mt.ferjorosa.core.learning;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.datastream.DataStream;
import mt.ferjorosa.core.learning.conceptdrift.FadingLearner;
import mt.ferjorosa.core.learning.structural.LTMLearning;
import mt.ferjorosa.core.models.LatentTreeModel;

/**
 * En este método al crear el LTM presuponemos ue las variable son condicionalmente independientes con respecto a su LV asociada
 *
 * Por tanto, el primer paso es crear los LCM (sibling clusters en el BI). Para ello utilizamos la información mútua entre las OVs y vamos creando LCMs.
 * Tras tener todos los LCMs creados, iteramos por ellos asignando la cardinalidad a las LVs de los disferentes LCMs. Para ello debemos estimar los parámetros primero.
 * Consideramos que la cardinalidad es binaria, calculamos los parametros y vemos el score de la estructura, despues aumentamos la cardinalidad en 1 y comparamos el score, de forma greedy.
 */
public class StreamingLTMLearningEngine {

    protected FadingLearner conceptDriftMeasure;
    protected DataStream<DataInstance> dataStream;
    protected LTMLearning LTMLearningEngine;
    protected LatentTreeModel LTM;

    /**
     *
     * @param conceptDriftMeasure
     */
    public void setConceptDriftMeasure(FadingLearner conceptDriftMeasure){
        this.conceptDriftMeasure = conceptDriftMeasure;
    }

    /**
     *
     * @param dataStream_ a {@link DataStream} object.
     */
    public void setDataStream(DataStream<DataInstance> dataStream_) {
        this.dataStream = dataStream_;
    }

    /**
     *
     * @param ltmLearningEngine
     */
    public void setLTMLearningEngine(LTMLearning ltmLearningEngine){
        this.LTMLearningEngine = ltmLearningEngine;
    }

    public void initLearning(DataOnMemory<DataInstance> batch){
        // En este caso el initLearning lo que hace es pasar un primer batch de instancias que son los que serviran
        // para poder aprender el LTM inicial y que sera posteriormente modificado segun se vayan produciendo
        // concept drifts.


    }

    /**
     *
     */
    public void runLearning() {
        // Con cada batch de instancias que le lleguen se comprueba que no exista un concept drift
        // mediante la comparación de la measure con un factor establecido, dependiendo de como se
        // supere dicho factor se  producir< un CONCEPT_DRIFT o un CONCEPT_SHIFT


    }
}


