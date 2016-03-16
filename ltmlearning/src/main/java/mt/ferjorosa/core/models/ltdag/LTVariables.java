package mt.ferjorosa.core.models.ltdag;

import eu.amidst.core.variables.Variable;
import eu.amidst.core.variables.Variables;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fer on 04/03/2016.
 */
public class LTVariables{

    private List<LTVariable> latentVariables;

    private List<LTVariable> observedVariables;

    private Variables variables;

    public LTVariables(Variables variables){
        this.variables = variables;
        this.latentVariables = new ArrayList<>();
        this.observedVariables = new ArrayList<>();
    }

    /**
     *
     * @return
     */
    public Variables getVariablesObject(){
        return this.variables;
    }

    /**
     *
     * @return
     */
    public List<LTVariable> getLatentVariables(){
        return this.latentVariables;
    }

    /**
     *
     * @return
     */
    public List<LTVariable> getObservedVariables(){
        return this.observedVariables;
    }

    /**
     *
     * @param variable
     */
    public ObservedVariable newObservedVariable(Variable variable){
        ObservedVariable observedVar = new ObservedVariable(variable);
        observedVariables.add(observedVar);
        return observedVar;
    }

    /**
     *
     * @param variable
     */
    public LatentVariable newLatentVariable(Variable variable){
        LatentVariable latentVar = new LatentVariable(variable);
        latentVariables.add(latentVar);
        return latentVar;
    }

}
