package mt.ferjorosa.examples.learning.BridgedIslands;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.BayesianNetworkWriter;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.learning.parametric.bayesian.ParallelSVB;
import eu.amidst.core.learning.parametric.bayesian.SVB;
import eu.amidst.core.models.BayesianNetwork;
import eu.amidst.core.utils.DAGGenerator;
import eu.amidst.core.variables.Variables;
import eu.amidst.huginlink.io.BNWriterToHugin;
import mt.ferjorosa.core.learning.LTMLearningEngine;
import mt.ferjorosa.core.learning.structural.ApproximateBIAlgorithm;
import mt.ferjorosa.core.learning.structural.ApproximateBIConfig;
import mt.ferjorosa.core.learning.structural.StructuralLearning;
import mt.ferjorosa.core.models.LTM;
import mt.ferjorosa.core.models.ltdag.LTDAG;
import mt.ferjorosa.core.models.ltdag.LTVariables;
import mt.ferjorosa.core.models.ltdag.LatentVariable;
import mt.ferjorosa.core.models.ltdag.ObservedVariable;

import java.util.Arrays;

/**
 * Created by Fer on 07/04/2016.
 */
public class AlarmDatasetComparison {

    public static void main(String[] args) throws Exception {

        StructuralLearning structuralLearningAlgorithm = new ApproximateBIAlgorithm(new ApproximateBIConfig());

        LTM learntModel = null;
        LTM zhangModel = null;
        LTM parallelLearntModel = null;

        // Learns the structure using the BI approximation I ve developed
        DataStream<DataInstance> data = DataStreamLoader.openFromFile("datasets/ferjorosaData/Alarm_train.arff");
        long oneCoreStartTime = System.currentTimeMillis();
        for (DataOnMemory<DataInstance> batch : data.iterableOverBatches(1000)){
            learntModel = structuralLearningAlgorithm.learnModel(batch);
        }
        long oneCoreEstimatedTime = System.currentTimeMillis() - oneCoreStartTime;

        // Learns the structure using the BI approximation I ve developed with Parallelization
        DataStream<DataInstance> data2 = DataStreamLoader.openFromFile("datasets/ferjorosaData/Alarm_train.arff");
        long multiCoreStartTime = System.currentTimeMillis();
        for (DataOnMemory<DataInstance> batch : data2.iterableOverBatches(1000)){
            parallelLearntModel = buildParallelLTM(batch);
        }
        long multiCoreEstimatedTime = System.currentTimeMillis() - multiCoreStartTime;

        // Learns the parameters of the structure learnt by Zhang's algorithm
        DataStream<DataInstance> data3 = DataStreamLoader.openFromFile("datasets/ferjorosaData/Alarm_train.arff");
        for (DataOnMemory<DataInstance> batch : data3.iterableOverBatches(1000)){
            zhangModel = buildZhangLTM(batch);
        }
        // Learns a Naïve Bayes structure
        DataStream<DataInstance> data4 = DataStreamLoader.openFromFile("datasets/ferjorosaData/Alarm_train.arff");
        LTM naiveBayesModel = buildNBmodel(data4);

        System.out.println("ABI score: "+ learntModel.getScore());
        System.out.println("Zhang BI score: "+ zhangModel.getScore());
        System.out.println("Parallel ABI score: "+ parallelLearntModel.getScore());
        System.out.println("Naive Bayes score:" + naiveBayesModel.getScore());

        System.out.println("------------------------------------");
        System.out.println("ABI learning time: "+ oneCoreEstimatedTime);
        System.out.println("Parallel ABI learning time: "+ multiCoreEstimatedTime);

    }

    private static LTM buildZhangLTM(DataOnMemory<DataInstance> batch){

        Variables variables = new Variables(batch.getAttributes());

        LTVariables ltVariables = new LTVariables(variables);

        String[] v338states = {"0","1"};
        LatentVariable variable338 = new LatentVariable(
                variables.newMultionomialVariable("variable338", Arrays.asList(v338states)),0);
        ObservedVariable vLVFailure = new ObservedVariable(variables.getVariableByName("vLVFailure"));
        ObservedVariable vHistory = new ObservedVariable(variables.getVariableByName("vHistory"));

        String[] v29states = {"0","1"};
        LatentVariable variable29 = new LatentVariable(
                variables.newMultionomialVariable("variable29", Arrays.asList(v29states)),1);
        ObservedVariable vLVEDVolume = new ObservedVariable(variables.getVariableByName("vLVEDVolume"));
        ObservedVariable vCVP = new ObservedVariable(variables.getVariableByName("vCVP"));
        ObservedVariable vPCWP = new ObservedVariable(variables.getVariableByName("vPCWP"));

        String[] v388states = {"0","1","2"};
        LatentVariable variable388 = new LatentVariable(
                variables.newMultionomialVariable("variable388", Arrays.asList(v388states)),2);
        ObservedVariable vHypovolemia = new ObservedVariable(variables.getVariableByName("vHypovolemia"));
        ObservedVariable vStrokeVolume = new ObservedVariable(variables.getVariableByName("vStrokeVolume"));

        String[] v386states = {"0","1","2","4","5","6","7"};
        LatentVariable variable386 = new LatentVariable(
                variables.newMultionomialVariable("variable386", Arrays.asList(v386states)),3);
        ObservedVariable vTPR = new ObservedVariable(variables.getVariableByName("vTPR"));
        ObservedVariable vCO = new ObservedVariable(variables.getVariableByName("vCO"));
        ObservedVariable vBP = new ObservedVariable(variables.getVariableByName("vBP"));
        ObservedVariable vAnaphylaxis = new ObservedVariable(variables.getVariableByName("vAnaphylaxis"));
        ObservedVariable vErrLowOutput = new ObservedVariable(variables.getVariableByName("vErrLowOutput"));

        String[] v392states = {"0","1","2"};
        LatentVariable variable392 = new LatentVariable(
                variables.newMultionomialVariable("variable392", Arrays.asList(v392states)),4);
        ObservedVariable vPulmEmbolus = new ObservedVariable(variables.getVariableByName("vPulmEmbolus"));
        ObservedVariable vShunt = new ObservedVariable(variables.getVariableByName("vShunt"));

        String[] v391states = {"0","1","2","3","4"};
        LatentVariable variable391 = new LatentVariable(
                variables.newMultionomialVariable("variable391", Arrays.asList(v391states)),5);
        ObservedVariable vErrCauter = new ObservedVariable(variables.getVariableByName("vErrCauter"));
        ObservedVariable vHREKG = new ObservedVariable(variables.getVariableByName("vHREKG"));
        ObservedVariable vHRSat = new ObservedVariable(variables.getVariableByName("vHRSat"));

        String[] v51states = {"0","1"};
        LatentVariable variable51 = new LatentVariable(
                variables.newMultionomialVariable("variable51", Arrays.asList(v51states)),6);
        ObservedVariable vInsuffAnesth = new ObservedVariable(variables.getVariableByName("vInsuffAnesth"));
        ObservedVariable vHR = new ObservedVariable(variables.getVariableByName("vHR"));
        ObservedVariable vHRBP = new ObservedVariable(variables.getVariableByName("vHRBP"));
        ObservedVariable vCatechol = new ObservedVariable(variables.getVariableByName("vCatechol"));

        String[] v73states = {"0","1"};
        LatentVariable variable73 = new LatentVariable(
                variables.newMultionomialVariable("variable73", Arrays.asList(v73states)),7);
        ObservedVariable vVentLung = new ObservedVariable(variables.getVariableByName("vVentLung"));
        ObservedVariable vMinVol = new ObservedVariable(variables.getVariableByName("vMinVol"));

        String[] v387states = {"0","1","2","3"};
        LatentVariable variable387 = new LatentVariable(
                variables.newMultionomialVariable("variable387", Arrays.asList(v387states)),8);
        ObservedVariable vArtCO2 = new ObservedVariable(variables.getVariableByName("vArtCO2"));
        ObservedVariable vExpCO2 = new ObservedVariable(variables.getVariableByName("vExpCO2"));

        String[] v389states = {"0","1","2"};
        LatentVariable variable389 = new LatentVariable(
                variables.newMultionomialVariable("variable389", Arrays.asList(v389states)),9);
        ObservedVariable vMinVolSet = new ObservedVariable(variables.getVariableByName("vMinVolSet"));
        ObservedVariable vVentMach = new ObservedVariable(variables.getVariableByName("vVentMach"));

        String[] v390states = {"0","1","2","3","4","5","6"};
        LatentVariable variable390 = new LatentVariable(
                variables.newMultionomialVariable("variable390", Arrays.asList(v390states)),10);
        ObservedVariable vIntubation = new ObservedVariable(variables.getVariableByName("vIntubation"));
        ObservedVariable vDisconnect = new ObservedVariable(variables.getVariableByName("vDisconnect"));
        ObservedVariable vVentTube = new ObservedVariable(variables.getVariableByName("vVentTube"));
        ObservedVariable vVentAlv = new ObservedVariable(variables.getVariableByName("vVentAlv"));
        ObservedVariable vPress = new ObservedVariable(variables.getVariableByName("vPress"));
        ObservedVariable vKinkedTube = new ObservedVariable(variables.getVariableByName("vKinkedTube"));

        String[] v46states = {"0","1"};
        LatentVariable variable46 = new LatentVariable(
                variables.newMultionomialVariable("variable46", Arrays.asList(v46states)),11);
        ObservedVariable vPVSat = new ObservedVariable(variables.getVariableByName("vPVSat"));
        ObservedVariable vSaO2 = new ObservedVariable(variables.getVariableByName("vSaO2"));
        ObservedVariable vFiO2 = new ObservedVariable(variables.getVariableByName("vFiO2"));

        LTDAG ltdag = new LTDAG(ltVariables);

        ltdag.addParent(vLVFailure, variable338);
        ltdag.addParent(vHistory, variable338);

        ltdag.addParent(vLVEDVolume, variable29);
        ltdag.addParent(vCVP, variable29);
        ltdag.addParent(vPCWP, variable29);

        ltdag.addParent(vHypovolemia, variable388);
        ltdag.addParent(vStrokeVolume, variable388);

        ltdag.addParent(vTPR, variable386);
        ltdag.addParent(vCO, variable386);
        ltdag.addParent(vBP, variable386);
        ltdag.addParent(vAnaphylaxis, variable386);
        ltdag.addParent(vErrLowOutput, variable386);

        ltdag.addParent(vPulmEmbolus, variable392);
        ltdag.addParent(vShunt, variable392);

        ltdag.addParent(vErrCauter, variable391);
        ltdag.addParent(vHREKG, variable391);
        ltdag.addParent(vHRSat, variable391);

        ltdag.addParent(vInsuffAnesth, variable51);
        ltdag.addParent(vHR, variable51);
        ltdag.addParent(vHRBP, variable51);
        ltdag.addParent(vCatechol, variable51);

        ltdag.addParent(vVentLung, variable73);
        ltdag.addParent(vMinVol, variable73);

        ltdag.addParent(vArtCO2, variable387);
        ltdag.addParent(vExpCO2, variable387);

        ltdag.addParent(vMinVolSet, variable389);
        ltdag.addParent(vVentMach, variable389);

        ltdag.addParent(vIntubation, variable390);
        ltdag.addParent(vDisconnect, variable390);
        ltdag.addParent(vVentTube, variable390);
        ltdag.addParent(vVentAlv, variable390);
        ltdag.addParent(vPress, variable390);
        ltdag.addParent(vKinkedTube, variable390);

        ltdag.addParent(vPVSat, variable46);
        ltdag.addParent(vFiO2, variable46);
        ltdag.addParent(vSaO2, variable46);

        // Latent Variable parents

        ltdag.addParent(variable338,variable29);
        ltdag.addParent(variable29,variable388);
        ltdag.addParent(variable388,variable386);
        ltdag.addParent(variable386,variable51);
        ltdag.addParent(variable392,variable391);
        ltdag.addParent(variable391,variable51);
        ltdag.addParent(variable73,variable390);
        ltdag.addParent(variable387,variable390);
        ltdag.addParent(variable389,variable390);
        ltdag.addParent(variable390,variable46);
        ltdag.addParent(variable51,variable46);

        //We create a SVB object
        SVB parameterLearningAlgorithm = new SVB();
        // Normally we would need to call initLearning(), but the learner does it for us, so no need to call it 2 times
        LTMLearningEngine learner = new LTMLearningEngine(parameterLearningAlgorithm);

        return learner.learnKnownStructureLTM(ltdag, batch);
    }

    private static LTM buildNBmodel(DataStream<DataInstance> data){
        //We create a SVB object
        SVB parameterLearningAlgorithm = new SVB();

        //We fix the DAG structure
        parameterLearningAlgorithm.setDAG(DAGGenerator.getHiddenNaiveBayesStructure(data.getAttributes(),"GlobalHidden", 2));

        //We fix the size of the window
        parameterLearningAlgorithm.setWindowsSize(100);

        //We can activate the output
        parameterLearningAlgorithm.setOutput(true);

        //We set the data which is going to be used for leaning the parameters
        parameterLearningAlgorithm.setDataStream(data);

        //We perform the learning
        parameterLearningAlgorithm.runLearning();

        return new LTM(parameterLearningAlgorithm.getLearntBayesianNetwork(),parameterLearningAlgorithm.getLogMarginalProbability(),null);
    }

    private static LTM buildParallelLTM(DataOnMemory<DataInstance> batch){

        //We create a ParallelSVB object
        ParallelSVB parameterLearningAlgorithm = new ParallelSVB();
        //We fix the number of cores we want to exploit
        parameterLearningAlgorithm.setNCores(4);

        StructuralLearning parallelStructuralLearning = new ApproximateBIAlgorithm(new ApproximateBIConfig(),parameterLearningAlgorithm);

        return parallelStructuralLearning.learnModel(batch);
    }
}
