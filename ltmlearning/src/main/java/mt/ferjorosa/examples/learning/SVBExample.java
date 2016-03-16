package mt.ferjorosa.examples.learning;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.learning.parametric.bayesian.SVB;
import eu.amidst.core.models.BayesianNetwork;
import eu.amidst.core.utils.DAGGenerator;

/**
 * Esto es una copia del ejemplo proporcionado por el toolbox para comprobar que updateModel() da nullpointer exception si se intenta
 * realizar una iteración individual del aprendizaje, en teoria funciona bien cuando se ejecuta runLearning()
 */
public class SVBExample {


    public static void main(String[] args) throws Exception {

        //We can open the data stream using the static class DataStreamLoader
        DataStream<DataInstance> data = DataStreamLoader.openFromFile("datasets/WasteIncineratorSample.arff");

        //We create a SVB object
        SVB parameterLearningAlgorithm = new SVB();

        //We fix the DAG structure
        parameterLearningAlgorithm.setDAG(DAGGenerator.getHiddenNaiveBayesStructure(data.getAttributes(),"GlobalHidden", 2));

        //We fix the size of the window
        parameterLearningAlgorithm.setWindowsSize(100);

        //We can activate the output
        parameterLearningAlgorithm.setOutput(true);

        //We set the data which is going to be used for leaning the parameters
        //parameterLearningAlgorithm.setDataStream(data);

        //We perform the learning
        //parameterLearningAlgorithm.runLearning();

        parameterLearningAlgorithm.initLearning();

        for (DataOnMemory<DataInstance> batch : data.iterableOverBatches(100)){
            parameterLearningAlgorithm.updateModel(batch);
        }

        //And we get the model
        BayesianNetwork bnModel = parameterLearningAlgorithm.getLearntBayesianNetwork();

        //We print the model
        System.out.println(bnModel.toString());

    }

}
