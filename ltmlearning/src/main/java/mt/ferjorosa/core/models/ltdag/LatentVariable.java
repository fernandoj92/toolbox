package mt.ferjorosa.core.models.ltdag;

import eu.amidst.core.variables.DistributionTypeEnum;
import eu.amidst.core.variables.Variable;

/**
 * Represent a partition of the data, can only be multinomial
 */
public class LatentVariable implements LTVariable {

    /**
     * Represents the Variable object, which contains its core functionality
     */
    private Variable variable;

    /**
     * Returns the Variable object
     * @return the accessible variable
     */
    public Variable getVariable(){
        return this.variable;
    }


    public LatentVariable(Variable variable){
        if(variable.getDistributionTypeEnum() != DistributionTypeEnum.MULTINOMIAL)
            throw new IllegalArgumentException("Only Multinomial variables are allowed");
    }

}
