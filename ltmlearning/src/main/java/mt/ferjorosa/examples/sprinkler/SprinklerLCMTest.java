package mt.ferjorosa.sprinkler;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.inference.InferenceAlgorithm;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.models.BayesianNetwork;
import eu.amidst.core.models.DAG;
import eu.amidst.core.variables.Variable;
import eu.amidst.core.variables.Variables;
import eu.amidst.huginlink.inference.HuginInference;
import mt.ferjorosa.models.DiscreteLatentClusterModel;

import java.util.Arrays;
import java.util.Random;

/**
 * In this example we hide the Cloudy Variable, making it a latent variable
 * and we try to estimate its parameters with the EM algorithm
 */
public class SprinklerLCMTest {

    public static void main(String[] args) throws Exception {

        //We can open the data stream using the static class DataStreamLoader
        DataStream<DataInstance > data = DataStreamLoader.
                openFromFile("datasets/ferjorosaData/sprinklerDataHidden.arff");

        /**
         * Once the data is loaded, we create a random variable for each of the attributes (i.e. data columns)
         * in our data.
         *
         * We can extract the Variable objects by using the method getVariableByName();
         */
        Variables variables = new Variables(data.getAttributes());

        Variable sprinkler = variables.getVariableByName("sprinkler");
        Variable rain = variables.getVariableByName("rain");
        Variable wetGrass = variables.getVariableByName("wetGrass");

        /**
         * We create a hidden variable that will represent the "cloudy" variable, its state space is a finite set
         * with two elements, and its distribution type is multinomial.
         */
        Variable cloudy = variables.newMultionomialVariable("cloudy", Arrays.asList("TRUE", "FALSE"));

        DAG dag = new DAG(variables);

        dag.getParentSet(sprinkler).addParent(cloudy);
        dag.getParentSet(rain).addParent(cloudy);
        dag.getParentSet(wetGrass).addParent(sprinkler);
        dag.getParentSet(wetGrass).addParent(rain);

        System.out.println(dag.toString());

        /**
         * We now create the Bayesian network from the previous DAG.
         *
         * The distribution type and the parameters of these distributions are properly initialized.
         */

        Random r = new Random(0);

        try {
            // LCM with random parameters
            DiscreteLatentClusterModel lcm = new DiscreteLatentClusterModel(dag, r);
            System.out.println(lcm.toString());

            /**
             * Inference
             */

            // First we need to translate our LCM to a BN class to use the inference engines
            BayesianNetwork bnModel = lcm.toBayesianNetwork();

            // Then we create an instance of a inference algorithm. In this case we use the Hugin exact inference engine
            InferenceAlgorithm inferenceAlgorithm = new HuginInference();

            // We set the BN model
            inferenceAlgorithm.setModel(bnModel);

            // We recover the variables that interest us (in this case, all of them)
            Variable vCloudy = bnModel.getVariables().getVariableByName("cloudy");
            Variable vSprinkler = bnModel.getVariables().getVariableByName("sprinkler");
            Variable vRain = bnModel.getVariables().getVariableByName("rain");
            Variable vWetGrass = bnModel.getVariables().getVariableByName("wetGrass");

            // We assign the known evidence for our query
            /*Assignment assignment = new HashMapAssignment(2);
            assignment.setValue(vWetGrass,1);
            assignment.setValue(vRain,0);
            inferenceAlgorithm.setEvidence(assignment);*/

            //Then we run inference
            inferenceAlgorithm.runInference();

            //Then we query the posterior of
            //System.out.println("P(Sprinkler|W=1) = " + inferenceAlgorithm.getPosterior(vSprinkler));

            //Then we query the posterior of
            System.out.println("P(WetGrass) = " + inferenceAlgorithm.getPosterior(vWetGrass));

            // loglikelihood of the evidence
            //System.out.println("P(W=1) = " + Math.exp(inferenceAlgorithm.getLogProbabilityOfEvidence()));


        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
