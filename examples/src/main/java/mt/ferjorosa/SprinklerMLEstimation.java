package mt.ferjorosa;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.learning.parametric.ParallelMaximumLikelihood;
import eu.amidst.core.learning.parametric.ParameterLearningAlgorithm;
import eu.amidst.core.models.BayesianNetwork;
import eu.amidst.core.models.DAG;
import eu.amidst.core.variables.Variable;
import eu.amidst.core.variables.Variables;


public class SprinklerMLEstimation {

    public static void main(String[] args) throws Exception {

        //We can open the data stream using the static class DataStreamLoader
        DataStream<DataInstance> data = DataStreamLoader.openFromFile("datasets/ferjorosaData/sprinklerData300.arff");

        //We create a ParameterLearningAlgorithm object with the MaximumLikelihood builder
        ParameterLearningAlgorithm parameterLearningAlgorithm = new ParallelMaximumLikelihood();

        //We fix the DAG structure
        parameterLearningAlgorithm.setDAG(getSprinklerStructure(data));

        //We should invoke this method before processing any data
        parameterLearningAlgorithm.initLearning();

        //Then we show how we can perform parameter learnig by a sequential updating of data batches.
        for (DataOnMemory<DataInstance> batch : data.iterableOverBatches(100)){
            parameterLearningAlgorithm.updateModel(batch);
        }

        //And we get the model
        BayesianNetwork bnModel = parameterLearningAlgorithm.getLearntBayesianNetwork();

        //We print the model
        System.out.println(bnModel.toString());
    }

    private static DAG getSprinklerStructure(DataStream<DataInstance> dataStream){

        Variables variables = new Variables(dataStream.getAttributes());

        //Pre-defined structure
        Variable cloudy = variables.getVariableByName("cloudy");
        Variable sprinkler = variables.getVariableByName("sprinkler");
        Variable rain = variables.getVariableByName("rain");
        Variable wetGrass = variables.getVariableByName("wetGrass");

        /**
         * 1. Once you have defined your {@link Variables} object, the next step is to create
         * a DAG structure over this set of variables.
         *
         * 2. To add parents to each variable, we first recover the ParentSet object by the method
         * getParentSet(Variable var) and then call the method addParent().
         */

        DAG dag = new DAG(variables);

        dag.getParentSet(sprinkler).addParent(cloudy);
        dag.getParentSet(rain).addParent(cloudy);
        dag.getParentSet(wetGrass).addParent(sprinkler);
        dag.getParentSet(wetGrass).addParent(rain);

        /**
         * 1. We first check if the graph contains cycles.
         *
         * 2. We print out the created DAG. We can check that everything is as expected.
         */
        if (dag.containCycles()) {
            try {
            } catch (Exception ex) {
                throw new IllegalArgumentException(ex);
            }
        }

        System.out.println(dag.toString());

        return dag;
    }
}
