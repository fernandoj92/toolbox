package mt.ferjorosa.examples.learning.BridgedIslands;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.learning.parametric.bayesian.SVB;
import mt.ferjorosa.core.learning.structural.ApproximateBIAlgorithm;
import mt.ferjorosa.core.learning.structural.ApproximateBIConfig;
import mt.ferjorosa.core.learning.structural.StructuralLearning;
import mt.ferjorosa.core.models.LTM;

/**
 * Created by Fer on 07/07/2016.
 */
public class CoilDataset {


    public static void main(String[] args) throws Exception {

        DataStream<DataInstance> data = DataStreamLoader.openFromFile("datasets/ferjorosaData/Coil-42-test.arff");

        //We create a ParallelSVB object
        //ParallelSVB parameterLearningAlgorithm = new ParallelSVB();
        SVB parameterLearningAlgorithm = new SVB();

        //We fix the number of cores we want to exploit
        //parameterLearningAlgorithm.setNCores(4);

        StructuralLearning structuralLearningAlgorithm = new ApproximateBIAlgorithm(new ApproximateBIConfig(), parameterLearningAlgorithm);

        LTM learntModel = null;

        long startTime = System.currentTimeMillis();
        for (DataOnMemory<DataInstance> batch : data.iterableOverBatches(4000)){
            learntModel = structuralLearningAlgorithm.learnModel(batch);
        }
        long estimatedTime = System.currentTimeMillis() - startTime;

        System.out.println(learntModel.getLearntBayesianNetwork().toString());

        System.out.println("elapsed time: "+estimatedTime);
        System.out.println("ABI score: "+ learntModel.getScore());

        //BayesianNetworkWriter.saveToFile(learntModel.getLearntBayesianNetwork(),"networks/alarm/alarm_train1.bn");
        //BNWriterToHugin.saveToHuginFile(learntModel.getLearntBayesianNetwork(),"networks/alarm/alarm_train1.net");

    }
}

