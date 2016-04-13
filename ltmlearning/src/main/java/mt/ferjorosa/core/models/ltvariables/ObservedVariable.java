package mt.ferjorosa.core.models.ltvariables;

import eu.amidst.core.datastream.Attribute;
import eu.amidst.core.variables.DistributionTypeEnum;
import eu.amidst.core.variables.Variable;

/**
 * Normally observed variable, its data is complete and it will be considered an attribute when doing clustering
 * TODO: Develop methods that allow continuous variables, in an hybrid or complete way
 */
public class ObservedVariable implements LTVariable {

    /** Represents the Variable object, which contains its core functionality */
    private Variable variable;

    /**
     * Creates an instance of the class by passing the required variable.
     * @param variable the wrapped variable.
     */
    public ObservedVariable(Variable variable){
        // For the moment multinomial variables only.
        if(variable.getDistributionTypeEnum() != DistributionTypeEnum.MULTINOMIAL)
            throw new IllegalArgumentException("Only Multinomial variables are allowed");

        this.variable = variable;
    }

    /**
     * Returns the Variable object
     * @return the accessible variable
     */
    public Variable getVariable(){
        return this.variable;
    }

    /**
     * Returns the {@link Attribute} associated with this Variable.
     * @return the attribute associated with this Variable.
     */
    public Attribute getAttribute(){
        return this.variable.getAttribute();
    }
}
