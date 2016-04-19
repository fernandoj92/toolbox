package mt.ferjorosa.learning;

import eu.amidst.core.datastream.Attribute;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.learning.parametric.bayesian.SVB;
import eu.amidst.core.models.DAG;
import eu.amidst.core.utils.DAGGenerator;
import mt.ferjorosa.core.learning.LTMLearningEngine;
import mt.ferjorosa.core.models.LTM;
import mt.ferjorosa.core.util.graph.DirectedTree;
import mt.ferjorosa.core.util.graph.UndirectedGraph;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Testing the {@link LTMLearningEngine} class
 */
public class LTMLearningEngineTest {

    @Test
    public void testLearnUnidimensionalLTM(){

        // The ClassLoader adds a "/" at the beginning of the path that makes it throw an exception when loading
        String resourcePath = getClass().getResource("/Asia_train.arff").getPath().substring(1);
        DataStream<DataInstance> data  = DataStreamLoader.openFromFile(resourcePath);
        DataStream<DataInstance> data2  = DataStreamLoader.openFromFile(resourcePath);

        // Creates a naive bayes model using the core module
        SVB parameterLearningAlgorithm = new SVB();
        parameterLearningAlgorithm.setDAG(DAGGenerator.getHiddenNaiveBayesStructure(data.getAttributes(),"GlobalHidden", 2));
        parameterLearningAlgorithm.setWindowsSize(100);
        parameterLearningAlgorithm.setDataStream(data);
        parameterLearningAlgorithm.runLearning();

        // Creates the same structure (+ LTDAG wrappers) using the LTMLearningEngine
        SVB parameterLearningAlgorithm2 = new SVB();
        LTM ltm = null;
        LTMLearningEngine engine = new LTMLearningEngine(parameterLearningAlgorithm2);
        for(DataOnMemory<DataInstance> batch: data2.iterableOverBatches(100))
            ltm = engine.learnUnidimensionalLTM(data2.getAttributes().getFullListOfAttributes(), 2, 0, batch);

        /* Compares both structures. They should be the same (a naive bayes model) but with different names */

        Assert.assertEquals(
                parameterLearningAlgorithm.getLogMarginalProbability(),
                parameterLearningAlgorithm2.getLogMarginalProbability(),
                0);

        DAG dag1 = parameterLearningAlgorithm.getDAG();
        DAG dag2 = parameterLearningAlgorithm2.getDAG();

        Assert.assertEquals(dag1.getNumberOfLinks(), dag2.getNumberOfLinks());
        Assert.assertEquals(dag1.getVariables().getListOfVariables(), dag2.getVariables().getListOfVariables());
        Assert.assertEquals(dag1.getParentSets().size(), dag2.getParentSets().size());

        Assert.assertEquals(ltm.getScore(), parameterLearningAlgorithm.getLogMarginalProbability(), 0);
        Assert.assertEquals(ltm.getLtdag().getDAG().getParentSets().size(), data.getAttributes().getNumberOfAttributes() + 1);
        Assert.assertEquals(ltm.getLearntBayesianNetwork().getNumberOfVars(), data.getAttributes().getNumberOfAttributes() + 1);
        Assert.assertEquals(ltm.getLtdag().getLTVariables().size(), ltm.getLtdag().getDAG().getVariables().getNumberOfVars());
        Assert.assertEquals(ltm.getLtdag().getObservedVariables().size(), data.getAttributes().getNumberOfAttributes());
        Assert.assertEquals(ltm.getLtdag().getLatentVariables().size(), 1);
    }

    @Test
    public void testLearn2dimensionalLTM(){

        // The ClassLoader adds a "/" at the beginning of the path that makes it throw an exception when loading
        String resourcePath = getClass().getResource("/Asia_train.arff").getPath().substring(1);
        DataStream<DataInstance> data  = DataStreamLoader.openFromFile(resourcePath);

        List<Attribute> allAttributes = data.getAttributes().getFullListOfAttributes();
        List<Attribute> leftAttributes = new ArrayList<>();
        List<Attribute> rightAttributes = new ArrayList<>();

        /* Split the attributes in 2: 4 on the left and 4 on the right*/

        for(int i = 0; i < 4; i++)
            leftAttributes.add(allAttributes.get(i));

        for(int i = 4; i < allAttributes.size(); i++)
            rightAttributes.add(allAttributes.get(i));

        /* Creates a LTM with 2 LVs, 4 OVs under each of them */

        SVB parameterLearningAlgorithm = new SVB();
        LTM ltm = null;
        LTMLearningEngine engine = new LTMLearningEngine(parameterLearningAlgorithm);
        for(DataOnMemory<DataInstance> batch: data.iterableOverBatches(100))
            ltm = engine.learn2dimensionalLTM(leftAttributes, rightAttributes, 2, 2,batch);

        Assert.assertEquals(ltm.getScore(), parameterLearningAlgorithm.getLogMarginalProbability(), 0);
        Assert.assertEquals(ltm.getLtdag().getDAG(), parameterLearningAlgorithm.getDAG());
        Assert.assertEquals(ltm.getLtdag().getDAG().getParentSets().size(), data.getAttributes().getNumberOfAttributes() + 2);
        Assert.assertEquals(ltm.getLearntBayesianNetwork().getNumberOfVars(), data.getAttributes().getNumberOfAttributes() + 2);
        Assert.assertEquals(ltm.getLtdag().getLTVariables().size(), ltm.getLtdag().getDAG().getVariables().getNumberOfVars());
        Assert.assertEquals(ltm.getLtdag().getObservedVariables().size(), data.getAttributes().getNumberOfAttributes());
        Assert.assertEquals(ltm.getLtdag().getLatentVariables().size(), 2);
    }

    @Test
    public void testLearnFlatLTM(){

        // The ClassLoader adds a "/" at the beginning of the path that makes it throw an exception when loading
        String resourcePath = getClass().getResource("/Asia_train.arff").getPath().substring(1);
        DataStream<DataInstance> data  = DataStreamLoader.openFromFile(resourcePath);
        DataStream<DataInstance> data2  = DataStreamLoader.openFromFile(resourcePath);
        DataStream<DataInstance> data3  = DataStreamLoader.openFromFile(resourcePath);
        DataStream<DataInstance> data4  = DataStreamLoader.openFromFile(resourcePath);

        List<Attribute> allAttributes = data.getAttributes().getFullListOfAttributes();
        List<Attribute> leftAttributes = new ArrayList<>();
        List<Attribute> rightAttributes = new ArrayList<>();

        /* This test will only create a flat-LTM with a size of 2, so it should be almost identical to the one produced
           by the 2dimensional method.
        */

        for(int i = 0; i < 4; i++)
            leftAttributes.add(allAttributes.get(i));

        for(int i = 4; i < allAttributes.size(); i++)
            rightAttributes.add(allAttributes.get(i));

        SVB parameterLearningAlgorithm = new SVB();
        LTM leftLTM = null;
        LTM rightLTM = null;
        LTM twoDimensionalLTM = null;
        LTM flatLTM = null;
        LTMLearningEngine engine = new LTMLearningEngine(parameterLearningAlgorithm);

        /* Creates the DirectedTree representing the connections between the tree's LVs */

        UndirectedGraph graph = new UndirectedGraph(2);
        graph.addEdge(0,1,100);
        // The left tree is the root of the flat-LTM
        DirectedTree lvTree = new DirectedTree(graph, 0);

        for(DataOnMemory<DataInstance> batch: data.iterableOverBatches(100))
            leftLTM = engine.learnUnidimensionalLTM(leftAttributes,2,0,batch);

        for(DataOnMemory<DataInstance> batch: data2.iterableOverBatches(100))
            rightLTM = engine.learnUnidimensionalLTM(rightAttributes,2,1,batch);

        for(DataOnMemory<DataInstance> batch: data3.iterableOverBatches(100))
            twoDimensionalLTM = engine.learn2dimensionalLTM(leftAttributes, rightAttributes,2,2,batch);

        ArrayList<LTM> ltms = new ArrayList<>();
        ltms.add(leftLTM);
        ltms.add(rightLTM);

        for(DataOnMemory<DataInstance> batch: data4.iterableOverBatches(100))
            flatLTM = engine.learnFlatLTM(lvTree,ltms, batch);

        /* Assertions */

        Assert.assertEquals(flatLTM.getScore(), twoDimensionalLTM.getScore(), 0);
        Assert.assertEquals(flatLTM.getLtdag().getObservedVariables(), twoDimensionalLTM.getLtdag().getObservedVariables());
        Assert.assertEquals(flatLTM.getLtdag().getLTVariables().size(), twoDimensionalLTM.getLtdag().getLTVariables().size());
        Assert.assertEquals(flatLTM.getLtdag().getDAG().getParentSets().size(), twoDimensionalLTM.getLtdag().getDAG().getParentSets().size());
    }

}
