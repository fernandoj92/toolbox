package mt.ferjorosa.examples.learning;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.learning.parametric.bayesian.SVB;
import eu.amidst.core.variables.Variable;
import eu.amidst.core.variables.Variables;
import mt.ferjorosa.core.learning.LTMLearningEngine;
import mt.ferjorosa.core.learning.structural.variables.MutualInformation;
import mt.ferjorosa.core.models.LTM;
import mt.ferjorosa.core.models.ltdag.LTDAG;
import mt.ferjorosa.core.models.ltdag.LTVariables;
import mt.ferjorosa.core.models.ltdag.LatentVariable;
import mt.ferjorosa.core.models.ltdag.ObservedVariable;

import java.util.Arrays;

/**
 * Created by Fernando on 3/21/2016.
 */
public class ChowLiuExample {

    public static void main(String[] args) throws Exception {

        DataStream<DataInstance > data = DataStreamLoader.
                openFromFile("datasets/ferjorosaData/sprinklerDataHidden.arff");

        /**
         * Create the LTM structure (LTDAG) that is going to be learned
         */

        Variables variables = new Variables(data.getAttributes());

        LTVariables ltVariables = new LTVariables(variables);

        Variable vsp = variables.getVariableByName("sprinkler");

        ObservedVariable sprinkler = ltVariables.newObservedVariable(vsp);
        ObservedVariable rain = ltVariables.newObservedVariable(variables.getVariableByName("rain"));
        ObservedVariable wetGrass = ltVariables.newObservedVariable(variables.getVariableByName("wetGrass"));
        LatentVariable ltCloudy = ltVariables.newLatentVariable(
                variables.newMultionomialVariable("latentCloudy", Arrays.asList("TRUE", "FALSE")), 0);

        LTDAG ltdag = new LTDAG(ltVariables);

        ltdag.addParent(sprinkler, ltCloudy);
        ltdag.addParent(rain, ltCloudy);
        ltdag.addParent(wetGrass, sprinkler);
        ltdag.addParent(wetGrass, rain);

        /**
         * After we have constructed the DAG that forms the Latent Tree Model structure we have to learn its parameters
         * using a parameter learning algorithm that is able to handle incomplete data, because we have one latent variable.
         *
         * In this case we will use the Streaming Variational Bayes algorithm.
         */

        //We create a SVB object
        SVB parameterLearningAlgorithm = new SVB();
        //We can activate the output
        parameterLearningAlgorithm.setOutput(true);
        // Normally we would need to call initLearning(), but the learner do it for us, so no need to call it 2 times
        LTMLearningEngine ltmLearner = new LTMLearningEngine(parameterLearningAlgorithm);

        for (DataOnMemory<DataInstance> batch : data.iterableOverBatches(100)){
            LTM learntModel = ltmLearner.learnKnownStructureLTM(batch, ltdag);
            LTM copyleanrtModel = ltmLearner.learnKnownStructureLTM(batch, ltdag);
            //We print the model
            System.out.println("-----------------------------------");
            System.out.println(learntModel.getLearntModel().toString());
            System.out.println("-----------------------------------");
            System.out.println(copyleanrtModel.getLearntModel().toString());

            System.out.println("===================================");

            // Ahora intentamos calcular la informacion mútua entre dos variables latentes

            MutualInformation MI = new MutualInformation();
            MI.setData(batch);


        }


    }
}
