package mt.ferjorosa.sprinkler;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.models.DAG;
import eu.amidst.core.variables.Variable;
import eu.amidst.core.variables.Variables;
import mt.ferjorosa.core.ltm.DiscreteLatentClusterModel;

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

        Random r = new Random(3);

        try {
            DiscreteLatentClusterModel lcm = new DiscreteLatentClusterModel(dag, r);
            //DiscreteLatentClusterModel lcm = new DiscreteLatentClusterModel(dag);
            System.out.println(lcm.toString());
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
