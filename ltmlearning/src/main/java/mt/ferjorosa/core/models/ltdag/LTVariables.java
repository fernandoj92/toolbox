package mt.ferjorosa.core.models.ltdag;

import eu.amidst.core.variables.Variable;
import eu.amidst.core.variables.Variables;

import java.util.ArrayList;
import java.util.List;

/**
 * 'Variables' class equivalent for LTVariables. Wraps its functionality and adds another layer needed for the
 * 'LTDAG' class, to separate classes.
 */
public class LTVariables{

    /** List of latent variables. */
    private List<LatentVariable> latentVariables;

    /** List of observed variables. */
    private List<ObservedVariable> observedVariables;

    /** 'Variables' object, needed for constructing the 'DAG' object. */
    private Variables variables;

    /**
     * Creates an instance of the class by passing a 'Variables' object.
     * @param variables the required 'Variables' object.
     */
    public LTVariables(Variables variables){
        this.variables = variables;
        this.latentVariables = new ArrayList<>();
        this.observedVariables = new ArrayList<>();
    }

    /**
     * Returns the wrapped Variables object.
     * @return the Variables object.
     */
    public Variables getVariablesObject(){
        return this.variables;
    }

    /**
     * Returns a list of the latent variables.
     * @return the latent variables.
     */
    public List<LatentVariable> getLatentVariables(){
        return this.latentVariables;
    }

    /**
     * Returns a list of the observed variables.
     * @return the observed variables.
     */
    public List<ObservedVariable> getObservedVariables(){
        return this.observedVariables;
    }

    /**
     * Creates a new Observed Variable from a variable object, by wrapping it and adding the extra logic.
     * @param variable 'Variable' object that is going to be wrapped as an observed variable.
     * @return The newly created observed variable.
     */
    public ObservedVariable newObservedVariable(Variable variable){
        if(!variables.getListOfVariables().contains(variable))
            throw new IllegalArgumentException("Variable doesn't belong to the LTVariables object");

        ObservedVariable observedVar = new ObservedVariable(variable);
        observedVariables.add(observedVar);
        return observedVar;
    }

    /**
     * Creates a new Latent Variable from a variable object, by wrapping it and adding the extra logic.
     * @param variable 'Variable' object that is going to be wrapped as a latent variable.
     * @param index An index to distinguish the latent variables.
     * @return The newly created latent variable.
     */
    public LatentVariable newLatentVariable(Variable variable, int index){
        if(!variables.getListOfVariables().contains(variable))
            throw new IllegalArgumentException("Variable doesn't belong to the LTVariables object");

        LatentVariable latentVar = new LatentVariable(variable, index);
        latentVariables.add(latentVar);
        return latentVar;
    }

}
