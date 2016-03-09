package mt.ferjorosa.examples.variables;

import eu.amidst.core.datastream.Attributes;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.DataStreamLoader;
import mt.ferjorosa.core.learning.structural.variables.MutualInformation;

/**
 * Created by Fernando on 3/7/2016.
 */
public class MutualInformationExample {

    private static DataStream<DataInstance> data;

    public static void main(String[] args) throws Exception {

        //We can open the data stream using the static class DataStreamLoader
        data = DataStreamLoader.openFromFile("datasets/ferjorosaData/sprinklerData300.arff");

        Attributes attributes = data.getAttributes();

        double MI = 0;
        for (DataOnMemory<DataInstance> batch : data.iterableOverBatches(300)) {
            MutualInformation mutualInformation = new MutualInformation(batch);
            MI = mutualInformation.computeBivariateScore(attributes.getAttributeByName("wetGrass"), attributes.getAttributeByName("cloudy"));
        }

        // Lo interesante para el algoritmo BI es calcular la probabilidaddes de todos los atributos, de tal forma que nos ahorremos apsadas innecesarias por el dataset.
        System.out.println(MI);

    }

    private static double computeMI(int[] a, int avals, int[] t, int tvals) {
        double numinst = a.length;
        double oneovernuminst = 1/numinst;
        double sum = 0;

        // longs are required here because of big multiples in calculation
        long[][] crosscounts = new long[avals][tvals];
        long[] tcounts = new long[tvals];
        long[] acounts = new long[avals];
        // Compute counts for the two variables
        for (int i=0;i<a.length;i++) {
            int av = a[i];
            int tv = t[i];
            acounts[av]++;
            tcounts[tv]++;
            crosscounts[av][tv]++;
        }

        for (int tv=0;tv<tvals;tv++) {
            for (int av=0;av<avals;av++) {
                if (crosscounts[av][tv] != 0) {
                    // Main fraction: (n|x,y|)/(|x||y|)
                    double sumtmp = (numinst*crosscounts[av][tv])/(acounts[av]*tcounts[tv]);
                    // Log bit (|x,y|/n) and update product
                    sum += oneovernuminst*crosscounts[av][tv]*Math.log(sumtmp)*2;
                }
            }

        }

        return sum;
    }

}
