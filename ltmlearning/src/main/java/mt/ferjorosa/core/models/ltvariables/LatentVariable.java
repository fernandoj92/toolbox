package mt.ferjorosa.core.models.ltvariables;

import eu.amidst.core.variables.DistributionTypeEnum;
import eu.amidst.core.variables.Variable;

/**
 * Represents a partition of the data, it is a hidden variable and can only be multinomial.
 * TODO: Implement an index comprobation
 */
public class LatentVariable implements LTVariable {

    /** Represents the Variable object, which contains its core functionality. */
    private Variable variable;

    /** The index of the LV to distinguish it from other LVs in case we need to combine LTMs. */
    private int index;

    /**
     * Creates an instance of the class by passing the required variable.
     * @param variable the wrapped variable.
     * @param index the required index to distinguish the LV.
     */
    public LatentVariable(Variable variable, int index){
        // Latent variables can only be multinomially distributed.
        if(variable.getDistributionTypeEnum() != DistributionTypeEnum.MULTINOMIAL)
            throw new IllegalArgumentException("Only Multinomial variables are allowed");

        this.variable = variable;
        this.index = index;
    }

    /**
     * Returns the Variable object.
     * @return the accessible variable.
     */
    public Variable getVariable(){
        return this.variable;
    }

    /**
     * Returns the index of the Latent Variable.
     * @return the LV's index.
     */
    public int getIndex(){
        return this.index;
    }



}
