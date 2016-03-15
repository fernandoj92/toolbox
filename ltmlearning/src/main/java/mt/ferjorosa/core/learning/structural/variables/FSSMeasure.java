package mt.ferjorosa.core.learning.structural.variables;

import eu.amidst.core.datastream.Attribute;
import eu.amidst.core.datastream.Attributes;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Created by Fernando on 3/8/2016.
 */
public interface FSSMeasure {

    void setData(DataOnMemory<DataInstance> data);

    double computeBivariateScore(Attribute attributeX, Attribute attributeY);

    void computeAllPairScores(List<Attribute> attributes);

    Pair<Attribute, Attribute> getBestPair(List<Attribute> attributes);

    Attribute getClosestAttributeToSet(List<Attribute> activeSet, List<Attribute> outSet);
}
