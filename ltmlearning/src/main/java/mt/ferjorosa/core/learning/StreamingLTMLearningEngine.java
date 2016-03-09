package mt.ferjorosa.core.learning;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataStream;

/**
 * En este método al crear el LTM presuponemos ue las variable son condicionalmente independientes con respecto a su LV asociada
 *
 * Por tanto, el primer paso es crear los LCM (sibling clusters en el BI). Para ello utilizamos la información mútua entre las OVs y vamos creando LCMs.
 * Tras tener todos los LCMs creados, iteramos por ellos asignando la cardinalidad a las LVs de los disferentes LCMs. Para ello debemos estimar los parámetros primero.
 * Consideramos que la cardinalidad es binaria, calculamos los parametros y vemos el score de la estructura, despues aumentamos la cardinalidad en 1 y comparamos el score, de forma greedy.
 */
public class StreamingLTMLearningEngine {

    /**
     *
     * @param data a {@link DataStream} object.
     */
    public void setDataStream(DataStream<DataInstance> data) {

    }

    /**
     *
     */
    public void runLearning() {

    }
}


