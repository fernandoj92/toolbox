package mt.ferjorosa.core.learning;

import eu.amidst.core.datastream.Attribute;
import eu.amidst.core.datastream.Attributes;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.learning.parametric.ParameterLearningAlgorithm;
import eu.amidst.core.learning.parametric.bayesian.SVB;
import eu.amidst.core.models.BayesianNetwork;
import eu.amidst.core.variables.Variable;
import eu.amidst.core.variables.Variables;
import mt.ferjorosa.core.models.LTM;
import mt.ferjorosa.core.models.LTDAG;
import mt.ferjorosa.core.models.ltvariables.LTVariables;
import mt.ferjorosa.core.models.ltvariables.LatentVariable;
import mt.ferjorosa.core.models.ltvariables.ObservedVariable;
import mt.ferjorosa.core.util.graph.DirectedTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *  This class takes over the responsibility of creating different kinds of Latent Tree Models. Its methods doesn't learn
 *  the structure from data (to do so, go the classes that implement Structural Learning, i.e ApproximateBIAlgorithm), it
 *  only learns specifc kinds of LTMs:
 *
 *  - Unidimensional LTM (also called LCM): a Naive Bayes structure with a hidden class variable (latent).
 *  - 2Dimensional LTM: 2 unidimensional LTMs connected by their class variables.
 *  - Flat LTM: a series of unidimensional LTMs whose LVs form a rooted (directed) Maximum Weight Spanning Tree.
 */
public class LTMLearningEngine {

    /** Parameter learning algorithm used to fully learn the LTM */
    private ParameterLearningAlgorithm parameterLearningAlgorithm;

    /**
     * Creates a LTM learning engine by passing the parameter learning algorithm that is going to be used.
     * @param parameterLearningAlgorithm the parameter learning algorithm that is going to be used.
     */
    public LTMLearningEngine(ParameterLearningAlgorithm parameterLearningAlgorithm){
        this.parameterLearningAlgorithm = parameterLearningAlgorithm;
    }

    /**
     * Learns the parameters a LTM structure for a batch of instances.
     * @param batch a {@link DataOnMemory} object that is going to be used to learn the model.
     * @param ltdag the LTM structure.
     * @return the fully learnt LTM.
     */
    public LTM learnKnownStructureLTM(LTDAG ltdag, DataOnMemory<DataInstance> batch){
        LearnKnownStructureLTM learner = new LearnKnownStructureLTM(parameterLearningAlgorithm, ltdag);
        return learner.learnModel(batch);
    }

    /**
     * Learns a Naive Bayes structure with a hidden class variable.
     * @param attributes the list of  {@link Attribute} that are going to be used as the observed variables of the LTM
     * @param batch a {@link DataOnMemory} object that is going to be used to learn the model.
     * @param lvCardinality the hidden variable cardinality.
     * @param lvIndex the latent variable's index to distinguish it from other LVs
     * @return a fully learnt LTM.
     */
    public LTM learnUnidimensionalLTM(List<Attribute> attributes, int lvCardinality, int lvIndex, DataOnMemory<DataInstance> batch){

        // First creates an 'Attributes' object, necessary for the 'Variables' object creation.
        Variables variables = new Variables(new Attributes(attributes));

        // Creates a multinomial latent variable with the passed number of states.
        String[] latentVarStates = new String[lvCardinality];
        for(int i = 0; i<lvCardinality;i++)
            latentVarStates[i] = i+"";

        Variable latentVariable = variables.newMultionomialVariable("H"+lvIndex, Arrays.asList(latentVarStates));

        LTVariables ltVariables = new LTVariables(variables);

        //Creates the LTDAG latent variable.
        LatentVariable latentVar = ltVariables.newLatentVariable(latentVariable, lvIndex);

        // Creates the observed LTDAG variables.
        List<ObservedVariable> observedVariables = new ArrayList<>();
        for(Variable var: variables){
            // To distinguish between latent variables and observed ones, we make use of their attribute object disparity
            if(var.getAttribute() != null) {
                ObservedVariable observedVar = ltVariables.newObservedVariable(var);
                observedVariables.add(observedVar);
            }
        }

        // Creates the LTDAG
        LTDAG ltdag = new LTDAG(ltVariables);

        // Then assigns each OV as a child of the LV.
        for(ObservedVariable observedVar : observedVariables)
            ltdag.addParent(observedVar,latentVar);

        // Finally, it learns the LTM parameters
        LearnKnownStructureLTM learner = new LearnKnownStructureLTM(parameterLearningAlgorithm, ltdag);
        return learner.learnModel(batch);
    }

    /**
     * Learns 2 unidimensional LTMs connected by their class variables.
     * @param leftAttributes left tree attributes.
     * @param rightAttributes right tree attributes.
     * @param leftCardinality the cardinality of the left latent variable
     * @param rightCardinality the cardinality of the right latent variable
     * @param batch a {@link DataOnMemory} object that is going to be used to learn the model.
     * @return a fully learnt LTM.
     */
    public LTM learn2dimensionalLTM(List<Attribute> leftAttributes, List<Attribute> rightAttributes, int leftCardinality, int rightCardinality, DataOnMemory<DataInstance> batch){

        // First creates an 'Attributes' object, necessary for the 'Variables' object creation.
        List<Attribute> allAttributes = new ArrayList<>(leftAttributes);
        allAttributes.addAll(rightAttributes);
        Variables variables = new Variables(new Attributes(allAttributes));

        // 2 latent variables (with the passed number of states) are created, one for each tree.
        String[] leftVarStates = new String[leftCardinality];
        for(int i = 0; i<leftCardinality;i++)
            leftVarStates[i] = i+"";

        String[] rightVarStates = new String[rightCardinality];
        for(int i = 0; i<rightCardinality;i++)
            rightVarStates[i] = i+"";

        Variable leftLatentVariable = variables.newMultionomialVariable("LeftLV", Arrays.asList(leftVarStates));
        Variable rightLatentVariable = variables.newMultionomialVariable("RightLV", Arrays.asList(rightVarStates));

        LTVariables ltVariables = new LTVariables(variables);

        // Attributes are Observed Variables in the LTM
        List<ObservedVariable> observedVariables = new ArrayList<>();
        for(Variable var: variables){
            // To distinguish between latent variables and observed ones, we make use of their attribute object disparity
            if(var.getAttribute() != null) {
                ObservedVariable observedVar = ltVariables.newObservedVariable(var);
                observedVariables.add(observedVar);
            }
        }

        // LTVariables wrapper for the latent variables
        LatentVariable leftLatentVar = ltVariables.newLatentVariable(leftLatentVariable, 0);
        LatentVariable rightLatentVar = ltVariables.newLatentVariable(rightLatentVariable, 1);

        // Creates the LTM structure
        LTDAG ltdag = new LTDAG(ltVariables);

        // Then assigns each OV as a child of the LV. Each OV can only belong to one LV
        for(ObservedVariable observedVar : observedVariables){
            if(leftAttributes.contains(observedVar.getVariable().getAttribute()))
                ltdag.addParent(observedVar,leftLatentVar);
            else
                ltdag.addParent(observedVar,rightLatentVar);
        }

        /**
         * The left tree is chosen as the root of the tree. This is an heuristic and it's based on what Leary et.al (2013)
         * says: "the LTM root cannot be learnt from data".
         */
        ltdag.addParent(rightLatentVar,leftLatentVar);

        // Finally, it learns the LTM parameters
        LearnKnownStructureLTM learner = new LearnKnownStructureLTM(parameterLearningAlgorithm, ltdag);
        return learner.learnModel(batch);
    }

    /**
     * Learns a LTM from a list of LCMs and directed tree that represents the connections between the Latent variables of
     * the unconnected LCMs.
     * @param latentVarsTree the directed tree that represents the connections between the tree's latent variables.
     * @param ltms the array of unconnected Latent Tree Models.
     * @param batch a {@link DataOnMemory} object that is going to be used to learn the model.
     * @return the new directed Latent Tree Model.
     */
    public LTM learnFlatLTM(DirectedTree latentVarsTree, ArrayList<LTM> ltms, DataOnMemory<DataInstance> batch){

        /**
         * First step is to create the 'Variables' object. The only possible way is by using an 'Attributes' object.
         * It doesn't use the batch attributes because the LTM can contain less attributes than the batch (feature subset
         * selection)
         */
        List<Attribute> observedAttributes = new ArrayList<>();
        for(LTM latentTreeModel : ltms) {
            // By iterating through the OVs, it obtains the 'Attribute' objects
            for (ObservedVariable var : latentTreeModel.getLtdag().getObservedVariables())
                observedAttributes.add(var.getVariable().getAttribute());
        }
        Attributes selectedAttributes = new Attributes(observedAttributes);
        Variables variables = new Variables(selectedAttributes);

        // Creates the latent variables (which doesn't have an associated attribute) with the same internal values.
        List<Variable> baseLatentVariables = new ArrayList<>();
        for(int treeIndex = 0; treeIndex < ltms.size(); treeIndex++) {
            int lvCardinality = ltms.get(treeIndex).getLtdag().getLatentVariables().get(0).getVariable().getNumberOfStates();
            String[] latentVarStates = new String[lvCardinality];
            for(int i = 0; i<lvCardinality;i++){
                latentVarStates[i] = i+"";
            }
            Variable latentVariable = variables.newMultionomialVariable("H"+treeIndex, Arrays.asList(latentVarStates));
            baseLatentVariables.add(latentVariable);
        }

        // Now that all the Variable objects have been created, it creates the LTVariables object from the Variables obj
        LTVariables ltVariables = new LTVariables(variables);

        // It creates the Latent Variables of the new LTM by wrapping them
        List<LatentVariable> latentVariables = new ArrayList<>();
        for(int treeIndex = 0; treeIndex < ltms.size(); treeIndex++) {
            latentVariables.add(ltVariables.newLatentVariable(baseLatentVariables.get(treeIndex), treeIndex));
        }

        // It creates the Observed variables of the new LTM by wrapping them
        List<ObservedVariable> observedVariables = new ArrayList<>();
        for(Variable var: variables){
            // To distinguish between latent variables and observed ones, we make use of their attribute object disparity
            if(var.getAttribute() != null) {
                ObservedVariable observedVar = ltVariables.newObservedVariable(var);
                observedVariables.add(observedVar);
            }
        }

        // Creates the LTM structure
        LTDAG ltdag = new LTDAG(ltVariables);

        // Añadimos las conexiones entre las variables observadas y las variables latentes
        // Iteramos por los LTMs y conectamos las variables observadas del LTM con su variable

        // Nota: Este rollo es porque trabajamos con 2 objetos casi identicos de variables, los de los antiguos LTMs separados
        // Y los del neuvo LTM conjunto
        /**
         * Then assigns each OV as a child of the LV. Each OV can only belong to one LV. There is
         */
        // TODO: Revisar este codigo posteriormente para reducirlo ya que a lo mejor no es necesaria tanta comprobacion
        for(LTM ltm : ltms){

            for(LatentVariable ltVar : ltm.getLtdag().getLatentVariables()){

                LatentVariable latentVar = null;
                for(LatentVariable latentVariable : latentVariables){
                    if(latentVariable.getIndex() == ltVar.getIndex())
                        latentVar = latentVariable;
                }

                // obvVar & observedVariable represent the same object, but are different instances
                for(ObservedVariable obvVar : ltm.getLtdag().getObservedVariables()){
                    for(ObservedVariable observedVariable : observedVariables)
                        if(observedVariable.getVariable().getAttribute().equals(obvVar.getVariable().getAttribute()))
                            ltdag.addParent(observedVariable,latentVar);
                }
            }

        }

        // After that, the connections between LVs are assigned
        Map<Integer, Integer> latentVarConnections = latentVarsTree.getEdges();
        for(Integer parentIndex: latentVarConnections.keySet()){
            LatentVariable parent = latentVariables.get(parentIndex);
            LatentVariable son = latentVariables.get(latentVarConnections.get(parentIndex));
            ltdag.addParent(son, parent);
        }

        // Finally, it learns the LTM parameters
        LearnKnownStructureLTM learner = new LearnKnownStructureLTM(parameterLearningAlgorithm, ltdag);
        return learner.learnModel(batch);
    }

    /**
     * Private class that learns the parameters of a LTM.
     */
    private class LearnKnownStructureLTM {

        /** Parameter learning algorithm used to fully learn the LTM */
        private ParameterLearningAlgorithm parameterLearningAlgorithm;

        /** The LTM's structure */
        private LTDAG ltdag;

        /**
         * Creates a instance of the class by passing it the structure of the LTM to be learnt and the parameter
         * learning algorithm to be used.
         * @param parameterLearningAlgorithm the parameter learning algorithm to be used.
         * @param ltdag the LTM structure.
         */
        public LearnKnownStructureLTM(ParameterLearningAlgorithm parameterLearningAlgorithm, LTDAG ltdag){
            this.parameterLearningAlgorithm = parameterLearningAlgorithm;
            this.ltdag = ltdag;
            this.parameterLearningAlgorithm.setDAG(ltdag.getDAG());
        }

        /**
         * Returns the learnt Latent Tree Model
         * @param batch a {@link DataOnMemory} object that is going to be used to learn the model.
         * @return the fully learnt LTM
         */
        public LTM learnModel(DataOnMemory<DataInstance> batch){

            // Sets the data that is going to be used for learning the LTM parameters
            parameterLearningAlgorithm.setDataStream(batch);
            // Performs the learning. Given that it uses the runLearning() method, it will call initLearning() before each
            // learning process and therefore previous knowledge won't interfere, producing separated LTMs
            parameterLearningAlgorithm.runLearning();
            // And stores the learnt model and the score by storing the parameter learning instance in the new LTM, then returns it
            return new LTM(ltdag, parameterLearningAlgorithm);
        }

    }
}
