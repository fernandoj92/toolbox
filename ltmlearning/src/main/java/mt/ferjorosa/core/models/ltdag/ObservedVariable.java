package mt.ferjorosa.core.models.ltdag;

import eu.amidst.core.variables.DistributionTypeEnum;
import eu.amidst.core.variables.Variable;

/**
 * Normally observed variable, its data is complete and it will be considered an attribute when doing clustering
 * TODO: Develop methods that allow continuous variables, in an hybrid or complete way
 */
public class ObservedVariable implements LTVariable {

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

    /**
     * In the very beggining we only accept multinomial variables
     * @param variable
     */
    public ObservedVariable(Variable variable){
        if(variable.getDistributionTypeEnum() != DistributionTypeEnum.MULTINOMIAL)
            throw new IllegalArgumentException("Only Multinomial variables are allowed");
    }
}
