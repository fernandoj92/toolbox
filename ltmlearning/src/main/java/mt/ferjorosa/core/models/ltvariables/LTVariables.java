package mt.ferjorosa.core.models.ltvariables;

import eu.amidst.core.datastream.Attribute;
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

    /** Initial list of variables. */
    private List<Variable> variables;

    /** 'Variables' object, needed for constructing the 'DAG' object. */
    private Variables variablesObj;

    /**
     * Creates an instance of the class by passing a 'Variables' object.
     * @param variablesObj the required 'Variables' object.
     */
    public LTVariables(Variables variablesObj){
        this.variablesObj = variablesObj;
        this.variables = new ArrayList<>(variablesObj.getListOfVariables());
        this.latentVariables = new ArrayList<>();
        this.observedVariables = new ArrayList<>();
    }

    /**
     * Returns the wrapped Variables object.
     * @return the Variables object.
     */
    public Variables getVariablesObject(){
        return this.variablesObj;
    }

    /**
     * Returns the list of {@link Attribute} associated with the LTDAG's observed variables.
     * @return the LTDAG's list of {@link Attribute}.
     */
    public List<Attribute> getAttributes(){
        List<Attribute> attributes = new ArrayList<>();
        for(ObservedVariable observedVariable : this.observedVariables)
            attributes.add(observedVariable.getAttribute());
        return attributes;
    }

    /**
     * Returns the number of LTVariables contained (both observed and latent variables)
     * @return the number of LTVariables contained (both observed and latent variables)
     */
    public int size(){
        return observedVariables.size() + latentVariables.size();
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
        if(!variables.contains(variable))
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
        if(!variables.contains(variable))
            throw new IllegalArgumentException("Variable doesn't belong to the LTVariables object");

        LatentVariable latentVar = new LatentVariable(variable, index);
        latentVariables.add(latentVar);
        return latentVar;
    }

}
