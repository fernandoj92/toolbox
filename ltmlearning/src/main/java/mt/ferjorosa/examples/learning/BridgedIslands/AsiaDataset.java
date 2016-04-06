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
 * Created by Fer on 04/04/2016.
 */
public class AsiaDataset {

    public static void main(String[] args) throws Exception {

        DataStream<DataInstance> data = DataStreamLoader.openFromFile("datasets/ferjorosaData/Asia_train.arff");

        StructuralLearning structuralLearningAlgorithm = new ApproximateBIAlgorithm();

        LTM learntModel = null;

        for (DataOnMemory<DataInstance> batch : data.iterableOverBatches(100)){
            learntModel = structuralLearningAlgorithm.learnModel(batch);
        }

        BayesianNetworkWriter.saveToFile(learntModel.getLearntModel(),"networks/asia_train.bn");
        BNWriterToHugin.saveToHuginFile(learntModel.getLearntModel(),"networks/asia_train.net");

    }
}
