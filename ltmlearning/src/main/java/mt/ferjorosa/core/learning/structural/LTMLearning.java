package mt.ferjorosa.core.learning.structural;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;

/**
 * Created by Fernando on 3/8/2016.
 */
public interface LTMLearning {

    /**
     * Updates the model using a given {@link DataOnMemory} object.
     * @param batch a {@link DataOnMemory} object.
     * @return the log-probability of the data instances of the
     * batch. Or Double.NaN if this log-probability can not be estimated.
     */
    double updateModel(DataOnMemory<DataInstance> batch);

}
