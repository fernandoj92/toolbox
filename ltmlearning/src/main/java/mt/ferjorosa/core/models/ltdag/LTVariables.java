package mt.ferjorosa.core.models.ltdag;

import eu.amidst.core.variables.Variable;
import eu.amidst.core.variables.Variables;

import java.util.ArrayList;
import java.util.List;

/**
 * 'Variables' class equivalent for LTVariables
 */
public class LTVariables{

    private List<LatentVariable> latentVariables;

    private List<ObservedVariable> observedVariables;

    private Variables variables;

    public LTVariables(Variables variables){
        this.variables = variables;
        this.latentVariables = new ArrayList<>();
        this.observedVariables = new ArrayList<>();
    }

    /**
     * Returns the wrapped Variables object
     * @return the Variables object
     */
    public Variables getVariablesObject(){
        return this.variables;
    }

    /**
     * Returns a list of the latent variables
     * @return the latent variables
     */
    public List<LatentVariable> getLatentVariables(){
        return this.latentVariables;
    }

    /**
     * Returns a list of the observed variables
     * @return the observed variables
     */
    public List<ObservedVariable> getObservedVariables(){
        return this.observedVariables;
    }

    /**
     * Creates a new Observed Variable from a variable object, by wrapping it and adding the extra logic
     * @param variable
     */
    public ObservedVariable newObservedVariable(Variable variable){
        ObservedVariable observedVar = new ObservedVariable(variable);
        observedVariables.add(observedVar);
        return observedVar;
    }

    /**
     * Creates a new Latent Variable from a variable object, by wrapping it and adding the extra logic
     * @param variable
     */
    public LatentVariable newLatentVariable(Variable variable){
        LatentVariable latentVar = new LatentVariable(variable);
        latentVariables.add(latentVar);
        return latentVar;
    }

}
