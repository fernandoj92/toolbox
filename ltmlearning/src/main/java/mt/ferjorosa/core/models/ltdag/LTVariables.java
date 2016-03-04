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
    public void newObservedVariable(Variable variable){
        latentVariables.add(new LatentVariable(variable));
    }

    /**
     *
     * @param variable
     */
    public void newLatentVariable(Variable variable){
        observedVariables.add(new ObservedVariable(variable));
    }

}
