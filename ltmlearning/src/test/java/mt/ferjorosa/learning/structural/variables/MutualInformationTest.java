package mt.ferjorosa.learning.structural.variables;

import eu.amidst.core.datastream.*;
import eu.amidst.core.io.DataStreamLoader;
import mt.ferjorosa.core.learning.structural.variables.MutualInformation;
import mt.ferjorosa.core.util.pair.SymmetricPair;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Testing the {@link MutualInformation} class
 */
public class MutualInformationTest {

    @Test
    public void testComputeBivariateScore(){

        // The ClassLoader adds a "/" at the beginning of the path that makes it throw an exception when loading
        String resourcePath = getClass().getResource("/sprinklerData300.arff").getPath().substring(1);
        DataStream<DataInstance> data  = DataStreamLoader.openFromFile(resourcePath);

        Attributes attributes = data.getAttributes();

        double calculatedMI = 0;
        double realMI = 0.0203;

        MutualInformation mutualInformation = new MutualInformation();

        for (DataOnMemory<DataInstance> batch : data.iterableOverBatches(300)) {
            mutualInformation.setData(batch);
            calculatedMI = mutualInformation.computeBivariateScore(attributes.getAttributeByName("wetGrass"), attributes.getAttributeByName("cloudy"));
        }

        Assert.assertEquals(calculatedMI, realMI, 0.001);
    }

    @Test
    public void testComputeAllPairScores(){

        // The ClassLoader adds a "/" at the beginning of the path that makes it throw an exception when loading
        String resourcePath = getClass().getResource("/sprinklerData300.arff").getPath().substring(1);
        DataStream<DataInstance> data  = DataStreamLoader.openFromFile(resourcePath);

        Attributes attributes = data.getAttributes();

        MutualInformation mutualInformation = new MutualInformation();

        /* Computes all the MI values */

        for (DataOnMemory<DataInstance> batch : data.iterableOverBatches(300)) {
            mutualInformation.setData(batch);
            mutualInformation.computeAllPairScores(data.getAttributes().getFullListOfAttributes());
        }
        Map<SymmetricPair<Attribute,Attribute>,Double> allPairScores = mutualInformation.getAllPairScores();

        /* Manually creates te data structure containing all the Pair Scores */

        Attribute cloudy = data.getAttributes().getAttributeByName("cloudy");
        Attribute sprinkler = data.getAttributes().getAttributeByName("sprinkler");
        Attribute rain = data.getAttributes().getAttributeByName("rain");
        Attribute wetGrass = data.getAttributes().getAttributeByName("wetGrass");


        Map<SymmetricPair<Attribute,Attribute>,Double> manualPairScores = new HashMap<>();

        manualPairScores.put(
                new SymmetricPair<>(cloudy, sprinkler),
                mutualInformation.computeBivariateScore(cloudy, sprinkler));

        manualPairScores.put(
                new SymmetricPair<>(cloudy, rain),
                mutualInformation.computeBivariateScore(cloudy, rain));

        manualPairScores.put(
                new SymmetricPair<>(cloudy, wetGrass),
                mutualInformation.computeBivariateScore(cloudy, wetGrass));

        manualPairScores.put(
                new SymmetricPair<>(sprinkler, rain),
                mutualInformation.computeBivariateScore(sprinkler, rain));

        manualPairScores.put(
                new SymmetricPair<>(sprinkler, wetGrass),
                mutualInformation.computeBivariateScore(sprinkler, wetGrass));

        manualPairScores.put(
                new SymmetricPair<>(rain, wetGrass),
                mutualInformation.computeBivariateScore(rain, wetGrass));

        /* Compares both data structures */

        Assert.assertEquals(manualPairScores, allPairScores);
    }

    @Test
    public void testGetBestPair(){

        // The ClassLoader adds a "/" at the beginning of the path that makes it throw an exception when loading
        String resourcePath = getClass().getResource("/sprinklerData300.arff").getPath().substring(1);
        DataStream<DataInstance> data  = DataStreamLoader.openFromFile(resourcePath);

        Attributes attributes = data.getAttributes();

        MutualInformation mutualInformation = new MutualInformation();

        /* Computes all the MI values */

        for (DataOnMemory<DataInstance> batch : data.iterableOverBatches(300)) {
            mutualInformation.setData(batch);
            mutualInformation.computeAllPairScores(data.getAttributes().getFullListOfAttributes());
        }

        SymmetricPair<Attribute, Attribute> bestPair = mutualInformation.getBestPair(data.getAttributes().getFullListOfAttributes());

        /* Compares the real best pair to the returned one */

        Attribute cloudy = data.getAttributes().getAttributeByName("rain");
        Attribute sprinkler = data.getAttributes().getAttributeByName("wetGrass");
        SymmetricPair<Attribute, Attribute> realBestPair = new SymmetricPair<>(cloudy, sprinkler);

        Assert.assertEquals(realBestPair, bestPair);

        /* Passes a list containing different attributes than the dataSet */

        DataStream<DataInstance> otherData  = DataStreamLoader.openFromFile(
                getClass().getResource("/Asia_train.arff").getPath().substring(1));

        try{
            mutualInformation.getBestPair(otherData.getAttributes().getFullListOfAttributes());
            Assert.fail("Should throw an IllegalArgumentException, there are absent attributes");
        }catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        } catch (Exception e){
            Assert.assertTrue(false);
        }

    }

    @Test
    public void testGetClosestAttributeToSet(){

        // The ClassLoader adds a "/" at the beginning of the path that makes it throw an exception when loading
        String resourcePath = getClass().getResource("/sprinklerData300.arff").getPath().substring(1);
        DataStream<DataInstance> data  = DataStreamLoader.openFromFile(resourcePath);

        Attributes attributes = data.getAttributes();

        MutualInformation mutualInformation = new MutualInformation();

        /* Computes all the MI values */

        for (DataOnMemory<DataInstance> batch : data.iterableOverBatches(300)) {
            mutualInformation.setData(batch);
            mutualInformation.computeAllPairScores(data.getAttributes().getFullListOfAttributes());
        }

        /* Compares the real closest attribute to the returned one */

        Attribute cloudy = data.getAttributes().getAttributeByName("cloudy");
        Attribute sprinkler = data.getAttributes().getAttributeByName("sprinkler");
        Attribute rain = data.getAttributes().getAttributeByName("rain");
        Attribute wetGrass = data.getAttributes().getAttributeByName("wetGrass");

        List<Attribute> firstSet = new ArrayList<>();
        firstSet.add(cloudy);
        firstSet.add(wetGrass);
        List<Attribute> secondSet = new ArrayList<>();
        secondSet.add(sprinkler);
        secondSet.add(rain);

        Attribute realClosestAttribute = rain;
        Attribute closestAttribute  = mutualInformation.getClosestAttributeToSet(firstSet, secondSet);

        Assert.assertEquals(realClosestAttribute, closestAttribute);

        /* Passes a list containing different attributes than the dataSet */

        DataStream<DataInstance> otherData  = DataStreamLoader.openFromFile(
                getClass().getResource("/Asia_train.arff").getPath().substring(1));

        // one way
        try{
            mutualInformation.getClosestAttributeToSet(
                    data.getAttributes().getFullListOfAttributes(),
                    otherData.getAttributes().getFullListOfAttributes());
            Assert.fail("Should throw an IllegalArgumentException, there are absent attributes");
        }catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        } catch (Exception e){
            Assert.assertTrue(false);
        }
        // the other
        try{
            mutualInformation.getClosestAttributeToSet(
                    otherData.getAttributes().getFullListOfAttributes(),
                    data.getAttributes().getFullListOfAttributes());
            Assert.fail("Should throw an IllegalArgumentException, there are absent attributes");
        }catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        } catch (Exception e){
            Assert.assertTrue(false);
        }

    }

}
