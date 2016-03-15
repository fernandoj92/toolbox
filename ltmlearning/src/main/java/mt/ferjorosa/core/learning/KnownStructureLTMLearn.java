package mt.ferjorosa.core.learning;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.learning.parametric.ParameterLearningAlgorithm;
import mt.ferjorosa.core.models.LatentTreeModel;
import mt.ferjorosa.core.models.ltdag.LTDAG;

/**
 * Created by Fer on 15/03/2016.
 */
public class KnownStructureLTMLearn {

    // Le pasamos un LTDAG y nos aprende los parámetros mediante el algoritmo de aprendizaje
    // de parametros seleccionado

    /** Parameter learning algorithm used to fully learn the LTM */
    private ParameterLearningAlgorithm parameterLearning;

    public KnownStructureLTMLearn(ParameterLearningAlgorithm parameterLearningAlgorithm){
        this.parameterLearning = parameterLearningAlgorithm;
    }

    /**
     *
     * @param batch
     * @param ltdag
     */
    public LatentTreeModel learnModel(DataOnMemory<DataInstance> batch, LTDAG ltdag){

    }
}
