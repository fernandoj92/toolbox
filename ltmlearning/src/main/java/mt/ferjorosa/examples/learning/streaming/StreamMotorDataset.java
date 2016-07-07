package mt.ferjorosa.examples.learning.streaming;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.learning.parametric.bayesian.SVB;
import mt.ferjorosa.core.learning.StreamingLTMLearningEngine;
import mt.ferjorosa.core.learning.conceptdrift.ConceptDriftMeasure;
import mt.ferjorosa.core.learning.conceptdrift.LikelihoodFading;
import mt.ferjorosa.core.learning.conceptdrift.PageHinkley;
import mt.ferjorosa.core.learning.structural.ApproximateBIAlgorithm;
import mt.ferjorosa.core.learning.structural.ApproximateBIConfig;
import mt.ferjorosa.core.learning.structural.StructuralLearning;

/**
 * Created by Fernando on 7/7/2016.
 */
public class StreamMotorDataset {

    public static void main(String[] args) throws Exception {

        DataStream<DataInstance> data = DataStreamLoader.openFromFile("datasets/ferjorosaData/dataset_motor.arff");

        //We create a ParallelSVB object
        SVB mainPLalgorihtm = new SVB();
        SVB driftPLalgorithm = new SVB();

        StructuralLearning structuralLearningAlgorithm = new ApproximateBIAlgorithm(new ApproximateBIConfig(), mainPLalgorihtm);

        ConceptDriftMeasure driftMeasure = new PageHinkley(100, 0,driftPLalgorithm);
        StreamingLTMLearningEngine streamEngine = new StreamingLTMLearningEngine(driftMeasure,structuralLearningAlgorithm);

        boolean start = true;

        long startTime = System.currentTimeMillis();
        for (DataOnMemory<DataInstance> batch : data.iterableOverBatches(1000)){
            if(start) {
                streamEngine.initLearning(batch);
                start = false;
                //System.out.println("\n MODELO INICIAL:");
               // System.out.println(streamEngine.getCurrentModel().getLearntBayesianNetwork().toString());
               // System.out.println("\n SCORE DEL MODELO INICIAL: " + streamEngine.getLastBatchScore());
            }else {
                streamEngine.updateModel(batch);
                //System.out.println("\n MODELO NORMAL:");
                //System.out.println(streamEngine.getCurrentModel().getLearntBayesianNetwork().toString());
                //System.out.println("\n SCORE DEL MODELO NORMAL: " + streamEngine.getLastBatchScore());
            }
        }
/*
        streamEngine.setDataStream(data);
        streamEngine.setBatchSize(1000);
        streamEngine.initLearning(data.iterableOverBatches(1000).);
        streamEngine.runLearning();
*/
        long estimatedTime = System.currentTimeMillis() - startTime;

        //System.out.println(streamEngine.getCurrentModel().getLearntBayesianNetwork().toString());

        System.out.println("elapsed time: "+estimatedTime);
        //System.out.println("ABI score: "+ streamEngine.getCurrentModel().getScore());

        //BayesianNetworkWriter.saveToFile(learntModel.getLearntBayesianNetwork(),"networks/alarm/alarm_train1.bn");
        //BNWriterToHugin.saveToHuginFile(learntModel.getLearntBayesianNetwork(),"networks/alarm/alarm_train1.net");

    }
}
