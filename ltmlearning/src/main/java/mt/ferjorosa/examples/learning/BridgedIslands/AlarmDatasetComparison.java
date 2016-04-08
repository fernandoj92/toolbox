package mt.ferjorosa.examples.learning.BridgedIslands;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.BayesianNetworkWriter;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.variables.Variables;
import eu.amidst.huginlink.io.BNWriterToHugin;
import mt.ferjorosa.core.learning.structural.ApproximateBIAlgorithm;
import mt.ferjorosa.core.learning.structural.StructuralLearning;
import mt.ferjorosa.core.models.LTM;
import mt.ferjorosa.core.models.ltdag.LTVariables;
import mt.ferjorosa.core.models.ltdag.LatentVariable;
import mt.ferjorosa.core.models.ltdag.ObservedVariable;

import java.util.Arrays;

/**
 * Created by Fer on 07/04/2016.
 */
public class AlarmDatasetComparison {

    public static void main(String[] args) throws Exception {

        DataStream<DataInstance> data = DataStreamLoader.openFromFile("datasets/ferjorosaData/Alarm_train.arff");

        StructuralLearning structuralLearningAlgorithm = new ApproximateBIAlgorithm();

        LTM learntModel = null;
        LTM zhangModel = null;

        for (DataOnMemory<DataInstance> batch : data.iterableOverBatches(1000)){
            learntModel = structuralLearningAlgorithm.learnModel(batch);
        }

        DataStream<DataInstance> data2 = DataStreamLoader.openFromFile("datasets/ferjorosaData/Alarm_train.arff");
        for (DataOnMemory<DataInstance> batch : data2.iterableOverBatches(1000)){
            zhangModel = buildZhangLTM(batch);
        }

    }

    private static LTM buildZhangLTM(DataOnMemory<DataInstance> batch){

        Variables variables = new Variables(batch.getAttributes());

        LTVariables ltVariables = new LTVariables(variables);

        String[] v338states = {"0","1","2"};
        LatentVariable variable338 = new LatentVariable(
                variables.newMultionomialVariable("variable338", Arrays.asList(v338states)),0);
        ObservedVariable vLVFailure = new ObservedVariable(variables.getVariableByName("vVentLung"));
        ObservedVariable vHistory = new ObservedVariable(variables.getVariableByName("vMinVol"));

        String[] v29states = {"0","1","2"};
        LatentVariable variable29 = new LatentVariable(
                variables.newMultionomialVariable("variable29", Arrays.asList(v29states)),0);
        ObservedVariable vLVEDVolume = new ObservedVariable(variables.getVariableByName("vLVEDVolume"));
        ObservedVariable vCVP = new ObservedVariable(variables.getVariableByName("vCVP"));
        ObservedVariable vPCWP = new ObservedVariable(variables.getVariableByName("vPCWP"));

        String[] v388states = {"0","1","2"};
        LatentVariable variable388 = new LatentVariable(
                variables.newMultionomialVariable("variable388", Arrays.asList(v388states)),0);
        ObservedVariable vHypovolemia = new ObservedVariable(variables.getVariableByName("vHypovolemia"));
        ObservedVariable vStrokeVolume = new ObservedVariable(variables.getVariableByName("vStrokeVolume"));

        String[] v386states = {"0","1","2"};
        LatentVariable variable386 = new LatentVariable(
                variables.newMultionomialVariable("variable386", Arrays.asList(v386states)),0);
        ObservedVariable vTPR = new ObservedVariable(variables.getVariableByName("vTPR"));
        ObservedVariable vCO = new ObservedVariable(variables.getVariableByName("vCO"));
        ObservedVariable vBP = new ObservedVariable(variables.getVariableByName("vBP"));
        ObservedVariable vAnaphylaxis = new ObservedVariable(variables.getVariableByName("vAnaphylaxis"));
        ObservedVariable vErrLowOutput = new ObservedVariable(variables.getVariableByName("vErrLowOutput"));

        String[] v392states = {"0","1","2"};
        LatentVariable variable392 = new LatentVariable(
                variables.newMultionomialVariable("variable392", Arrays.asList(v392states)),0);
        ObservedVariable vPulmEmbolus = new ObservedVariable(variables.getVariableByName("vPulmEmbolus"));
        ObservedVariable vShunt = new ObservedVariable(variables.getVariableByName("vShunt"));

        String[] v391states = {"0","1","2"};
        LatentVariable variable391 = new LatentVariable(
                variables.newMultionomialVariable("variable391", Arrays.asList(v391states)),0);
        ObservedVariable vErrCauter = new ObservedVariable(variables.getVariableByName("vErrCauter"));
        ObservedVariable vHREKG = new ObservedVariable(variables.getVariableByName("vHREKG"));
        ObservedVariable vHRSat = new ObservedVariable(variables.getVariableByName("vHRSat"));

        String[] v51states = {"0","1","2"};
        LatentVariable variable51 = new LatentVariable(
                variables.newMultionomialVariable("variable51", Arrays.asList(v51states)),0);
        ObservedVariable vInsuffAnesth = new ObservedVariable(variables.getVariableByName("vInsuffAnesth"));
        ObservedVariable vHR = new ObservedVariable(variables.getVariableByName("vHR"));
        ObservedVariable vHRBP = new ObservedVariable(variables.getVariableByName("vHRBP"));
        ObservedVariable vCatechol = new ObservedVariable(variables.getVariableByName("vCatechol"));

        String[] v73states = {"0","1","2"};
        LatentVariable variable73 = new LatentVariable(
                variables.newMultionomialVariable("variable73", Arrays.asList(v73states)),0);
        ObservedVariable vVentLung = new ObservedVariable(variables.getVariableByName("vVentLung"));
        ObservedVariable vMinVol = new ObservedVariable(variables.getVariableByName("vMinVol"));

        String[] v387states = {"0","1","2"};
        LatentVariable variable387 = new LatentVariable(
                variables.newMultionomialVariable("variable387", Arrays.asList(v387states)),0);
        ObservedVariable vArtCO2 = new ObservedVariable(variables.getVariableByName("vArtCO2"));
        ObservedVariable vExpCO2 = new ObservedVariable(variables.getVariableByName("vExpCO2"));

        String[] v389states = {"0","1","2"};
        LatentVariable variable389 = new LatentVariable(
                variables.newMultionomialVariable("variable389", Arrays.asList(v389states)),0);
        ObservedVariable vMinVolSet = new ObservedVariable(variables.getVariableByName("vMinVolSet"));
        ObservedVariable vVentMatch = new ObservedVariable(variables.getVariableByName("vVentMatch"));

        String[] v390states = {"0","1","2"};
        LatentVariable variable390 = new LatentVariable(
                variables.newMultionomialVariable("variable390", Arrays.asList(v390states)),0);
        ObservedVariable vIntubation = new ObservedVariable(variables.getVariableByName("vIntubation"));
        ObservedVariable vDisconnect = new ObservedVariable(variables.getVariableByName("vDisconnect"));
        ObservedVariable vVentTube = new ObservedVariable(variables.getVariableByName("vVentTube"));
        ObservedVariable vVentAlv = new ObservedVariable(variables.getVariableByName("vVentAlv"));
        ObservedVariable vPress = new ObservedVariable(variables.getVariableByName("vPress"));
        ObservedVariable vKinkedTube = new ObservedVariable(variables.getVariableByName("vKinkedTube"));

        String[] v46states = {"0","1","2"};
        LatentVariable variable46 = new LatentVariable(
                variables.newMultionomialVariable("variable46", Arrays.asList(v46states)),0);
        ObservedVariable vPVSat = new ObservedVariable(variables.getVariableByName("vPVSat"));
        ObservedVariable vSaO2 = new ObservedVariable(variables.getVariableByName("vSaO2"));
        ObservedVariable vFiO2 = new ObservedVariable(variables.getVariableByName("vFiO2"));

        return null;
    }
}
