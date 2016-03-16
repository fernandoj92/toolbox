package mt.ferjorosa.examples.learning;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.learning.parametric.bayesian.SVB;
import eu.amidst.core.models.BayesianNetwork;
import eu.amidst.core.variables.Variable;
import eu.amidst.core.variables.Variables;
import mt.ferjorosa.core.learning.KnownStructureLTMLearn;
import mt.ferjorosa.core.models.ltdag.*;

import java.util.Arrays;

/**
 * Created by Fer on 15/03/2016.
 */
public class KnownLTMLearningExample {

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
                variables.newMultionomialVariable("latentCloudy", Arrays.asList("TRUE", "FALSE")));

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
        KnownStructureLTMLearn learner = new KnownStructureLTMLearn(parameterLearningAlgorithm, ltdag);

        for (DataOnMemory<DataInstance> batch : data.iterableOverBatches(100)){
            BayesianNetwork learntModel = learner.learnModel(batch);
            //We print the model
            System.out.println("-----------------------------------");
            System.out.println(learntModel.toString());
        }

        // En este ejemplo vamos a utilizar la estructura de la red de sprinkler y vamos a convertir la
        // variable cloudy en latente y luego vamos a aprender los parametros. Puede que sea necesario implementar
        // una proyeccion del dataSet, aunque espero que no porque seria estupido y podria llevar un tiempo extra de
        // procesamiento.

        //

    }
}
