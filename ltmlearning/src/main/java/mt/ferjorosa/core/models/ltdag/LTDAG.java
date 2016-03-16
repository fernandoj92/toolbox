package mt.ferjorosa.core.models.ltdag;

import eu.amidst.core.models.DAG;

/**
 * Wrapper de la clase DAG que me permite crear un DAG con las restricciones propias de los Latent Tree Models
 * - Existen dos tipos de variables: Observables y Latentes
 * - Las variables observables pueden tener como padres otras variables observables o latentes
 * - Las variables latentes solo pueden tener como padres otras variables latentes
 *
 * Para separar esta clase de las restricciones propias del tipo de red bayesiana utilizada (en este caso son Conjugate
 * exponentials, cuyas restricciones coinciden en su mayor parte con las restricciones impuestas por los LTMs en cuanto
 * al tipo y jerarquía de las variables utilizadas)
 */
// Basicamente lo único que tiene que comprobar diferente de
// los DAGs es que la variable padre de una LV no sea una OV
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
