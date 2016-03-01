package mt.ferjorosa;

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
 * Created by Fer on 01/03/2016.
 */
public class LCMTest2Multinomials {

    public static void main(String[] args) throws Exception {

        //We can open the data stream using the static class DataStreamLoader
        DataStream<DataInstance> data = DataStreamLoader.openFromFile("datasets/ferjorosaData/syntheticData2multinomial.arff");

        /**
         * 1. Once the data is loaded, we create a random variable for each of the attributes (i.e. data columns)
         * in our data.
         *
         * 2. {@link Variables} is the class for doing that. It takes a list of Attributes and internally creates
         * all the variables. We create the variables using Variables class to guarantee that each variable
         * has a different ID number and make it transparent for the user.
         *
         * 3. We can extract the Variable objects by using the method getVariableByName();
         */
        Variables variables = new Variables(data.getAttributes());

        Variable a = variables.getVariableByName("a");
        Variable e = variables.getVariableByName("e");


        /**
         * 1. We create the hidden variable. For doing that we make use of the method "newMultionomialVariable". When
         * a variable is created from an Attribute object, it contains all the information we need (e.g.
         * the name, the type, etc). But hidden variables does not have an associated attribute
         * and, for this reason, we use now this to provide this information.
         *
         * 2. Using the "newMultionomialVariable" method, we define a variable called HiddenVar, which is
         * not associated to any attribute and, then, it is a latent variable, its state space is a finite set with two elements, and its
         * distribution type is multinomial.
         *
         * 3. We finally create the hidden variable using the method "newVariable".
         */

        Variable hidden = variables.newMultionomialVariable("HiddenVar", Arrays.asList("TRUE", "FALSE"));

        /**
         * 1. Once we have defined your {@link Variables} object, including the latent variable,
         * the next step is to create a DAG structure over this set of variables.
         *
         * 2. To add parents to each variable, we first recover the ParentSet object by the method
         * getParentSet(Variable var) and then call the method addParent(Variable var).
         *
         * 3. We just put the hidden variable as parent of all the other variables. Following a naive-Bayes
         * like structure.
         */
        DAG dag = new DAG(variables);

        dag.getParentSet(a).addParent(hidden);
        dag.getParentSet(e).addParent(hidden);

/**
 * We print the graph to see if is properly created.
 */
        System.out.println(dag.toString());

        /**
         * 1. We now create the Bayesian network from the previous DAG.
         *
         * 2. The BN object is created from the DAG. It automatically looks at the distribution type
         * of each variable and their parents to initialize the Distributions objects that are stored
         * inside (i.e. Multinomial, Normal, CLG, etc). The parameters defining these distributions are
         * properly initialized.
         *
         * 3. The network is printed and we can have look at the kind of distributions stored in the BN object.
         */
        Random r = new Random(3);

        try {
            DiscreteLatentClusterModel lcm = new DiscreteLatentClusterModel(dag, r);
            //DiscreteLatentClusterModel lcm = new DiscreteLatentClusterModel(dag);
            System.out.println(lcm.toString());
            /**
             * 1. We iterate over the data set sample by sample.
             *
             * 2. For each sample or DataInstance object, we compute the log of the probability that the BN object
             * assigns to this observation.
             *
             * 3. We accumulate these log-probs and finally we print the log-prob of the data set.
             */
            double logProb = 0;
            for (DataInstance instance : data) {
                logProb += lcm.getLogProbabilityOf(instance);
            }
            System.out.println(logProb);
            //data.stream()
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

}
