package mt.ferjorosa.examples.learning.streaming;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.learning.parametric.bayesian.ParallelSVB;
import eu.amidst.core.learning.parametric.bayesian.SVB;
import mt.ferjorosa.core.learning.StreamingLTMLearningEngine;
import mt.ferjorosa.core.learning.conceptdrift.ConceptDriftMeasure;
import mt.ferjorosa.core.learning.conceptdrift.LikelihoodFading;
import mt.ferjorosa.core.learning.structural.ApproximateBIAlgorithm;
import mt.ferjorosa.core.learning.structural.ApproximateBIConfig;
import mt.ferjorosa.core.learning.structural.StructuralLearning;
import mt.ferjorosa.core.models.LTM;

/**
 * Created by Fernando on 6/25/2016.
 */
public class StreamAlarmDataset {

    public static void main(String[] args) throws Exception {

        DataStream<DataInstance> data = DataStreamLoader.openFromFile("datasets/ferjorosaData/Alarm_train.arff");

        //We create a ParallelSVB object
        SVB mainPLalgorihtm = new SVB();
        SVB driftPLalgorithm = new SVB();

        StructuralLearning structuralLearningAlgorithm = new ApproximateBIAlgorithm(new ApproximateBIConfig(), mainPLalgorihtm);

        ConceptDriftMeasure driftMeasure = new LikelihoodFading(driftPLalgorithm);
        StreamingLTMLearningEngine streamEngine = new StreamingLTMLearningEngine(driftMeasure,structuralLearningAlgorithm);

        boolean start = true;

        long startTime = System.currentTimeMillis();
        for (DataOnMemory<DataInstance> batch : data.iterableOverBatches(500)){
            if(start) {
                streamEngine.initLearning(batch);
                start = false;
            }else {
                streamEngine.updateModel(batch);
                System.out.println("\n MODELO NORMAL:");
                System.out.println(streamEngine.getCurrentModel().getLearntBayesianNetwork().toString());
                System.out.println("\n SCORE DEL MODELO NORMAL: " + streamEngine.getLastBatchScore());
            }
        }
        long estimatedTime = System.currentTimeMillis() - startTime;

        //System.out.println(streamEngine.getCurrentModel().getLearntBayesianNetwork().toString());

        System.out.println("elapsed time: "+estimatedTime);
        System.out.println("ABI score: "+ streamEngine.getCurrentModel().getScore());

        //BayesianNetworkWriter.saveToFile(learntModel.getLearntBayesianNetwork(),"networks/alarm/alarm_train1.bn");
        //BNWriterToHugin.saveToHuginFile(learntModel.getLearntBayesianNetwork(),"networks/alarm/alarm_train1.net");

    }
}
