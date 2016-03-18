package mt.ferjorosa.core.learning;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.learning.parametric.ParameterLearningAlgorithm;
import eu.amidst.core.models.BayesianNetwork;
import mt.ferjorosa.core.models.LTM;
import mt.ferjorosa.core.models.LatentTreeModel;
import mt.ferjorosa.core.models.ltdag.LTDAG;

/**
 * Created by Fer on 15/03/2016.
 */
public class KnownStructureLTMLearn {

    // Le pasamos un LTDAG y nos aprende los parámetros mediante el algoritmo de aprendizaje
    // de parametros seleccionado

    /** Parameter learning algorithm used to fully learn the LTM */
    private ParameterLearningAlgorithm parameterLearningAlgorithm;

    public KnownStructureLTMLearn(ParameterLearningAlgorithm parameterLearningAlgorithm, LTDAG ltdag){
        this.parameterLearningAlgorithm = parameterLearningAlgorithm;
        parameterLearningAlgorithm.setDAG(ltdag.getDAG());
        this.parameterLearningAlgorithm.initLearning();
    }

    /**
     *
     * @param batch
     */
    public LTM learnModel(DataOnMemory<DataInstance> batch){
        double modelScore = parameterLearningAlgorithm.updateModel(batch);
        BayesianNetwork learntModel = parameterLearningAlgorithm.getLearntBayesianNetwork();
        return new LTM(learntModel, modelScore);
    }

}
