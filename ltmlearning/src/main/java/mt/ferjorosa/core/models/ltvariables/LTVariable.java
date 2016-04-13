package mt.ferjorosa.core.models.ltvariables;

import eu.amidst.core.variables.Variable;

/**
 * Interface for the variables that are used inside the LTDAG (Observed and Latent variables)
 */
public interface LTVariable {
    /**
     * Returns the wrapped 'Variable' object.
     * @return the wrapped 'Variable' object
     */
    Variable getVariable();
}

