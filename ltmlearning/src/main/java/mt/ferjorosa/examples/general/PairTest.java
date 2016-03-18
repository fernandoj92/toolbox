package mt.ferjorosa.examples.general;

import eu.amidst.core.datastream.Attribute;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.DataStreamLoader;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fer on 17/03/2016.
 */
public class PairTest {

    public static void main(String[] args) throws Exception {

        DataStream<DataInstance > data = DataStreamLoader.
                openFromFile("datasets/ferjorosaData/sprinklerDataHidden.arff");

        List<Attribute> attributes = data.getAttributes().getFullListOfAttributes();

        List<Attribute> attributes1 = new ArrayList<>();
        attributes1.add(attributes.get(0));

        List<Attribute> attributes2 = new ArrayList<>();
        attributes2.add(attributes.get(1));
        attributes2.add(attributes.get(2));

        Pair<List<Attribute>,List<Attribute>> pair1 = Pair.of(attributes1,attributes2);
        Pair<List<Attribute>,List<Attribute>> pair2 = Pair.of(attributes2,attributes1);

        if(pair1.equals(pair2))
            System.out.println("Toca currar");
        else
            System.out.println("Ayyyyyyyyy");
    }
}
