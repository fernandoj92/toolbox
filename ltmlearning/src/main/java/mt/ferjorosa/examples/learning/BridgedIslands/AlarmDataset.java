package mt.ferjorosa.examples.learning.BridgedIslands;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.BayesianNetworkWriter;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.huginlink.io.BNWriterToHugin;
import mt.ferjorosa.core.learning.structural.ApproximateBIAlgorithm;
import mt.ferjorosa.core.learning.structural.StructuralLearning;
import mt.ferjorosa.core.models.LTM;

/**
 * Created by Fer on 07/04/2016.
 */
public class AlarmDataset {

    public static void main(String[] args) throws Exception {

        DataStream<DataInstance> data = DataStreamLoader.openFromFile("datasets/ferjorosaData/Alarm_train.arff");

        StructuralLearning structuralLearningAlgorithm = new ApproximateBIAlgorithm();

        LTM learntModel = null;

        long startTime = System.currentTimeMillis();
        for (DataOnMemory<DataInstance> batch : data.iterableOverBatches(1000)){
            learntModel = structuralLearningAlgorithm.learnModel(batch);
        }
        long estimatedTime = System.currentTimeMillis() - startTime;

        System.out.println(learntModel.getLearntModel().toString());

        System.out.println("elapsed time: "+estimatedTime);

        BayesianNetworkWriter.saveToFile(learntModel.getLearntModel(),"networks/alarm/alarm_train1.bn");
        BNWriterToHugin.saveToHuginFile(learntModel.getLearntModel(),"networks/alarm/alarm_train1.net");

    }
}
