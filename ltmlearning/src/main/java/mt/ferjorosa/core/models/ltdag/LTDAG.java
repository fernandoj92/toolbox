package mt.ferjorosa.core.models.ltdag;

import eu.amidst.core.models.DAG;

import java.util.List;

/**
 * DAG class wrapper that represents the Latent Tree Model (LTM) structure. LTMs have some characteristics that make
 * suitable to create a specific class instead of simply reusing the DAG one:
 *
 * - There are two types of variables: Observed and Latent (hidden).
 * - Observed variables can hav other observed variables or latent variables as parents.
 * - Latent variables cannot have observed variables as parents.
 */
public class LTDAG {

    /** Represents the set of variables. */
    private LTVariables ltVariables;

    /** The internal DAG, which contains the core functionality */
    private DAG dag;

    public LTDAG(LTVariables ltVariables){
        this.ltVariables = ltVariables;
        this.dag = new DAG(ltVariables.getVariablesObject());
    }

    public LTVariables getLTVariables(){
        return this.ltVariables;
    }

    public List<ObservedVariable> getObservedVariables(){
        return this.getLTVariables().getObservedVariables();
    }

    public List<LatentVariable> getLatentVariables(){
        return this.getLTVariables().getLatentVariables();
    }

    /**
     * Returns the internal DAG, which contains the core functionality of the DAG
     * @return the internal DAG
     */
    public DAG getDAG(){
        return dag;
    }

    /**
     * Adds a new parent to a variable if the LTM restriction is satisfied
     * @param variable the variable
     * @param parent the parent variable that will be added
     */
    public void addParent(LTVariable variable, LTVariable parent){
        // LTM restriction
        if(variable instanceof LatentVariable && parent instanceof ObservedVariable)
            throw new IllegalArgumentException("A Latent Variable cannot have an Observed Variable as parent");

        dag.getParentSet(variable.getVariable()).addParent(parent.getVariable());
    }
}
