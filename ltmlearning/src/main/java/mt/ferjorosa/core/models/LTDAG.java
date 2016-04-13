package mt.ferjorosa.core.models;

import eu.amidst.core.models.DAG;
import mt.ferjorosa.core.models.ltvariables.LTVariable;
import mt.ferjorosa.core.models.ltvariables.LTVariables;
import mt.ferjorosa.core.models.ltvariables.LatentVariable;
import mt.ferjorosa.core.models.ltvariables.ObservedVariable;

import java.util.List;

/**
 * DAG class wrapper that represents the Latent Tree Model (LTM) structure. LTMs have some characteristics that make
 * suitable to create a specific class instead of simply reusing the DAG one:
 *
 * - There are two types of variables: Observed and Latent (hidden).
 * - Observed variables can have other observed variables or latent variables as parents.
 * - Latent variables cannot have observed variables as parents.
 */
public class LTDAG {

    /** Represents the set of variables. */
    private LTVariables ltVariables;

    /** The internal DAG, which contains the core functionality. */
    private DAG dag;

    /**
     * Creates an instance of the class from a set of LT Variables
     * @param ltVariables the set of variables of type {@link LTVariables}.
     */
    public LTDAG(LTVariables ltVariables){
        this.ltVariables = ltVariables;
        this.dag = new DAG(ltVariables.getVariablesObject());
    }

    /**
     * Returns the set of LT Variables in this LTDAG.
     * @return the 'LTVariables' object containing the variables of the LTM.
     */
    public LTVariables getLTVariables(){
        return this.ltVariables;
    }

    /**
     * Returns the observed variables of the LTM.
     * @return the list of observed variables.
     */
    public List<ObservedVariable> getObservedVariables(){
        return this.getLTVariables().getObservedVariables();
    }

    /**
     * Returns the latent variables of the LTM.
     * @return the list of latent variables.
     */
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
