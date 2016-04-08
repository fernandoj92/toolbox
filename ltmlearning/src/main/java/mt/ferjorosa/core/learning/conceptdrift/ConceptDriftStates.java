package mt.ferjorosa.core.learning.conceptdrift;

/**
 * Enumerates all the possible concept drift states:
 * <li>{@link #CONCEPT_SHIFT}</li>
 * <li>{@link #CONCEPT_DRIFT}</li>
 * <li>{@link #NONE}</li>
 */
public enum ConceptDriftStates {

    /** This term is usually associated to abrupt changes in the model. */
    CONCEPT_SHIFT,

    /** This term is usually associated to gradual changes in the model. */
    CONCEPT_DRIFT,

    /** When there is no concept drift. */
    NONE
}
