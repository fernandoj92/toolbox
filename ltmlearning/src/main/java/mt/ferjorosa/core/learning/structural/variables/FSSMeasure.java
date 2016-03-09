package mt.ferjorosa.core.learning.structural.variables;

import eu.amidst.core.datastream.Attribute;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;

/**
 * Created by Fernando on 3/8/2016.
 */
public interface FSSMeasure {

    void setData(DataOnMemory<DataInstance> data);

    double computeBivariateScore(Attribute attributeX, Attribute attributeY);
}
