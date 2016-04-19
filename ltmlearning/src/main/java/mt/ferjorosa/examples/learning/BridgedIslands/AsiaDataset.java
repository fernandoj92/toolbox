package mt.ferjorosa.examples.learning.BridgedIslands;

import eu.amidst.core.datastream.Attribute;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.BayesianNetworkWriter;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.learning.parametric.bayesian.ParallelSVB;
import eu.amidst.core.learning.parametric.bayesian.SVB;
import eu.amidst.huginlink.io.BNWriterToHugin;
import mt.ferjorosa.core.learning.LTMLearningEngine;
import mt.ferjorosa.core.learning.structural.ApproximateBIAlgorithm;
import mt.ferjorosa.core.learning.structural.ApproximateBIConfig;
import mt.ferjorosa.core.learning.structural.StructuralLearning;
import mt.ferjorosa.core.models.LTM;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fer on 04/04/2016.
 */
public class AsiaDataset {

    public static void main(String[] args) throws Exception {

        DataStream<DataInstance> data = DataStreamLoader.openFromFile("datasets/ferjorosaData/Asia_train.arff");

        StructuralLearning structuralLearningAlgorithm = new ApproximateBIAlgorithm(new ApproximateBIConfig());

        LTM learntModel = null;
        LTM zhangModel = null;

        long startTime = System.currentTimeMillis();
        for (DataOnMemory<DataInstance> batch : data.iterableOverBatches(100)){
            learntModel = structuralLearningAlgorithm.learnModel(batch);
        }
        long estimatedTime = System.currentTimeMillis() - startTime;

        DataStream<DataInstance> data2 = DataStreamLoader.openFromFile("datasets/ferjorosaData/Asia_train.arff");

        for (DataOnMemory<DataInstance> batch : data2.iterableOverBatches(100)){
            zhangModel = buildZhangLTM(batch);
        }

        System.out.println("elapsed time: "+estimatedTime);
        System.out.println("ABI score: "+ learntModel.getScore());
        System.out.println("Zhang's BI score: "+ zhangModel.getScore());

        //BayesianNetworkWriter.saveToFile(learntModel.getLearntBayesianNetwork(),"networks/asia_train.bn");
        //BNWriterToHugin.saveToHuginFile(learntModel.getLearntBayesianNetwork(),"networks/asia_train.net");

    }

    private static LTM buildZhangLTM(DataOnMemory<DataInstance> batch){
        List<Attribute> leftAttributes = new ArrayList<>();
        List<Attribute> rightAttributes = new ArrayList<>();


        // Defines the default parameter learning algorithm
        SVB streamingVariationalBayes = new SVB();
        streamingVariationalBayes.setWindowsSize(100);

        LTMLearningEngine ltmLearner = new LTMLearningEngine(streamingVariationalBayes);
        List<Attribute> allAttributes = batch.getAttributes().getFullListOfAttributes();

        leftAttributes.add(batch.getAttributes().getAttributeByName("vTuberculosis"));
        leftAttributes.add(batch.getAttributes().getAttributeByName("vSmoking"));
        leftAttributes.add(batch.getAttributes().getAttributeByName("vLungCancer"));
        leftAttributes.add(batch.getAttributes().getAttributeByName("vTbOrCa"));
        leftAttributes.add(batch.getAttributes().getAttributeByName("vXRay"));

        rightAttributes.add(batch.getAttributes().getAttributeByName("vBronchitis"));
        rightAttributes.add(batch.getAttributes().getAttributeByName("vDyspnea"));
        rightAttributes.add(batch.getAttributes().getAttributeByName("vVisitToAsia"));

        return ltmLearner.learn2dimensionalLTM(leftAttributes,rightAttributes,2,2,batch);
    }
}
