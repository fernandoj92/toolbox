package mt.ferjorosa.examples.general;

import eu.amidst.core.datastream.Attribute;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.DataStreamLoader;
import mt.ferjorosa.core.util.pair.SymmetricPair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        Attribute leftAttribute = attributes.get(0);
        Attribute leftRepetido = attributes.get(0);
        Attribute rightAttribute = attributes.get(1);

        Pair<Attribute, Attribute> leftPair = Pair.of(leftAttribute, rightAttribute);
        Pair<Attribute, Attribute> rightPair = Pair.of(rightAttribute, leftAttribute);

        Set<Pair<Attribute,Attribute>> pairSet = new HashSet<>();

        pairSet.add(leftPair);
        pairSet.add(rightPair);
/*
        if(leftPair.hashCode() == rightPair.hashCode())
            System.out.println("Los hashcodes coinciden");

        if(leftPair.equals(rightPair))
            System.out.println("Los equals coinciden");

        if(leftAttribute.equals(leftRepetido))
            System.out.println("Los equals de los atributos coinciden");

        if(leftAttribute.hashCode() == leftRepetido.hashCode())
            System.out.println("Los hashcodes de los atributos coinciden");

        if(pairSet.size() > 1)
            System.out.println("Mi no entender");

*/
        SymmetricPair<Attribute, Attribute> newLeftPair = new SymmetricPair<>(leftAttribute,rightAttribute);
        SymmetricPair<Attribute, Attribute> newRightPair = new SymmetricPair<>(rightAttribute,leftAttribute);

        if(newLeftPair.equals(newRightPair))
            System.out.println("Los equals de los SymmetricPairs coinciden");

        if(newLeftPair.hashCode() == newRightPair.hashCode())
            System.out.println("Los hashcodes de los SymmetricPairs coinciden");
    }
}
