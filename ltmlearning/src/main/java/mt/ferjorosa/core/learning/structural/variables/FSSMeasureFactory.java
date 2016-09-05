package mt.ferjorosa.core.learning.structural.variables;

import static mt.ferjorosa.core.learning.structural.variables.FSSMeasures.MUTUAL_INFORMATION;

/**
 * Created by fernando on 5/09/16.
 */
public class FSSMeasureFactory {

    public static FSSMeasure retrieveInstance(String fssMeasure){
        if(fssMeasure.equals(MUTUAL_INFORMATION.toString()))
            return new MutualInformation();

        // Unexpected error
        throw new IllegalArgumentException();
    }
}
