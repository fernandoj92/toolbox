package mt.ferjorosa.core.models.ltdag;

import eu.amidst.core.variables.DistributionTypeEnum;
import eu.amidst.core.variables.Variable;

/**
 * Represents a partition of the data, it is a hidden variable and can only be multinomial.
 */
public class LatentVariable implements LTVariable {

    /** Represents the Variable object, which contains its core functionality */
    private Variable variable;

    /** The index of the LV to distinguish it from other LVs in case we need to combine LTMs*/
    private int index;

    /**
     * Returns the Variable object
     * @return the accessible variable
     */
    public Variable getVariable(){
        return this.variable;
    }

    /**
     *
     * @return
     */
    public int getIndex(){
        return this.index;
    }

    public LatentVariable(Variable variable, int index){
        if(variable.getDistributionTypeEnum() != DistributionTypeEnum.MULTINOMIAL)
            throw new IllegalArgumentException("Only Multinomial variables are allowed");

        this.variable = variable;
    }

}
