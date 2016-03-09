package mt.ferjorosa.core.learning.structural;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.learning.parametric.ParameterLearningAlgorithm;
import mt.ferjorosa.core.learning.structural.variables.FSSMeasure;
import mt.ferjorosa.core.learning.structural.variables.MutualInformation;

/**
 * This is an approximation of the Bridged Islands algorithm proposed by Liu et.al in their article:
 *
 * Liu, T., Zhang, N. L., Poon, K. M., Liu, H., & Wang, Y. (2012, September). A novel ltm-based method for multi-partition clustering.
 * In 6th European Workshop on Probabilistic Graphical Models (pp. 203-210).
 *
 * This algorithm learns both the structure and the parameters of a Bayesian network. Its composed of 4 stages
 * 1 - Calculate the sibling clusters.
 * 2 - Generate a LCM (a LCM is a LTM with only one Latent variable)from each sibling cluster by assigning a Latent
 * variable on top of the chosen observed variables.
 * 3 - Learn each LCM's parameters, then repeatedly consider to increase the cardinality of the LV, stopping when the
 * used score decreases (BIC, BDe, AIC,etc., depending on the parameter Learning algorithm used).
 * 4 - Determine the connections among the latent variables so that they form a tree (LTM).
 * 5 - Refine the model.
 * 6* - Determine possible connections between the observed variables.
 *
 */
public class ApproximateBIAlgorithm implements LTMLearning {

    /**
     *
     */
    private FSSMeasure siblingClustersMeasure;

    /**
     *
     */
    private ParameterLearningAlgorithm parameterLearning;

    /**
     *
     * @param batch a {@link DataOnMemory} object.
     * @return
     */
    @Override
    public double updateModel(DataOnMemory<DataInstance> batch) {
        siblingClustersMeasure.setData(batch);
        // 1 - Calculate sibling clusters
        calculateSiblingClusters();
        // 2 - Generate LCMs
        generateLCMs();
        // 3 - Learn the LCM parameters and determine their LV's cardinality
        learnLCMs();
        // 4 - Generate the LTM
        generateLTM();

        return 0;
    }

    private void calculateSiblingClusters(){

    }

    private void generateLCMs(){

    }

    private void learnLCMs(){

    }

    private void generateLTM(){

    }

}
