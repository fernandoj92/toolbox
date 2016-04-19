package mt.ferjorosa.models;

import eu.amidst.core.datastream.Attribute;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.learning.parametric.bayesian.SVB;
import mt.ferjorosa.core.learning.LTMLearningEngine;
import mt.ferjorosa.core.learning.structural.ApproximateBIAlgorithm;
import mt.ferjorosa.core.learning.structural.ApproximateBIConfig;
import mt.ferjorosa.core.learning.structural.StructuralLearning;
import mt.ferjorosa.core.models.LTM;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Testing the {@link LTM} class.
 */
public class LTMTest {

    @Test
    public void testUpdateModel(){

        // The ClassLoader adds a "/" at the beginning of the path that makes it throw an exception when loading
        String resourcePath = getClass().getResource("/Asia_train.arff").getPath().substring(1);
        DataStream<DataInstance> data  = DataStreamLoader.openFromFile(resourcePath);

        // Comparar que el score mejora al hacer update vs utilizar 2 aprendizajes independientes
        StructuralLearning structuralLearningAlgorithm = new ApproximateBIAlgorithm(new ApproximateBIConfig());

        LTM updatedModel = null;
        LTM zhangModel = null;
        double updatedModelScore = 0;
        double learntModelScore = 0;

        int it = 0;
        for (DataOnMemory<DataInstance> batch : data.iterableOverBatches(250)){
            zhangModel = buildZhangLTM(batch);
            learntModelScore += zhangModel.getScore();
            if(it == 0){
                updatedModel = buildZhangLTM(batch);
                updatedModelScore += updatedModel.getScore();
                it++;
            }else
                updatedModelScore += updatedModel.updateModel(batch);

            // Assertions

            Assert.assertEquals(updatedModelScore, learntModelScore, 0);
        }
    }

    private LTM buildZhangLTM(DataOnMemory<DataInstance> batch){
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
