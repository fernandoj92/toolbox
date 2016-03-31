package mt.ferjorosa.core.learning;

import eu.amidst.core.datastream.Attribute;
import eu.amidst.core.datastream.Attributes;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.learning.parametric.ParameterLearningAlgorithm;
import eu.amidst.core.models.BayesianNetwork;
import eu.amidst.core.variables.Variable;
import eu.amidst.core.variables.Variables;
import mt.ferjorosa.core.models.LTM;
import mt.ferjorosa.core.models.ltdag.LTDAG;
import mt.ferjorosa.core.models.ltdag.LTVariables;
import mt.ferjorosa.core.models.ltdag.LatentVariable;
import mt.ferjorosa.core.models.ltdag.ObservedVariable;
import mt.ferjorosa.core.util.graph.DirectedTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *  Esta va ser la clase que tenga varios metodos de aprendizaje de LTMS, en vez de Known LTM learner
 */
public class LTMLearningEngine {

    /** Parameter learning algorithm used to fully learn the LTM */
    private ParameterLearningAlgorithm parameterLearningAlgorithm;

    public LTMLearningEngine(ParameterLearningAlgorithm parameterLearningAlgorithm){
        this.parameterLearningAlgorithm = parameterLearningAlgorithm;
    }

    public LTM learnKnownStructureLTM(DataOnMemory<DataInstance> batch, LTDAG ltdag){
        LearnKnownStructureLTM learner = new LearnKnownStructureLTM(parameterLearningAlgorithm, ltdag);
        return learner.learnModel(batch);
    }

    public LTM learnUnidimensionalLTM(List<Attribute> attributes, DataOnMemory<DataInstance> batch, int lvCardinality){
        // Transformamos la lista de atributos en un objeto Attributes, necesario para crear las variables
        // de nuestro modelo
        Variables variables = new Variables(new Attributes(attributes));
        LTVariables ltVariables = new LTVariables(variables);

        // Creamos las variables del LTDAG, señalando que cada una de ellas es una variable observada
        List<ObservedVariable> observedVariables = new ArrayList<>();
        for(Variable var: variables){
            ObservedVariable observedVar = ltVariables.newObservedVariable(var);
            observedVariables.add(observedVar);
        }

        // Creamos la variable latente que sera de tipo multinomial con X estados inicialmente
        // y asignamos cada variable observada como hijo de esta
        String[] latentVarStates = new String[lvCardinality];
        for(int i = 0; i<lvCardinality;i++){
            latentVarStates[i] = i+"";
        }

        Variable latentVariable = variables.newMultionomialVariable("LatentVar", Arrays.asList(latentVarStates));
        LatentVariable latentVar = ltVariables.newLatentVariable(latentVariable, 0);

        // Creamos el LTDAG
        LTDAG ltdag = new LTDAG(ltVariables);

        // Añadimos los arcos de la variable latente a las variables observadas
        for(ObservedVariable observedVar : observedVariables){
            ltdag.addParent(observedVar,latentVar);
        }

        // Aprendemos los parámetros del LTM
        LearnKnownStructureLTM learner = new LearnKnownStructureLTM(parameterLearningAlgorithm, ltdag);
        return learner.learnModel(batch);
    }

    public LTM learn2dimensionalLTM(List<Attribute> leftAttributes, List<Attribute> rightAttributes, DataOnMemory<DataInstance> batch){

        List<Attribute> allAttributes = new ArrayList<>(leftAttributes);
        allAttributes.addAll(rightAttributes);

        // Transformamos las listas de atributos objetos 'Attributes', necesario para crear las variables
        // de nuestro modelo
        Variables variables = new Variables(new Attributes(allAttributes));
        LTVariables ltVariables = new LTVariables(variables);

        // Los atributos son variables observadas en nuestro modelo
        List<ObservedVariable> observedVariables = new ArrayList<>();
        for(Variable var: variables){
            ObservedVariable observedVar = ltVariables.newObservedVariable(var);
            observedVariables.add(observedVar);
        }
        // Creamos 2 variables latentes, una para cada subarbol
        Variable leftLatentVariable = variables.newMultionomialVariable("LeftLV", Arrays.asList("0", "1"));
        Variable rightLatentVariable = variables.newMultionomialVariable("RightLV", Arrays.asList("0", "1"));
        LatentVariable leftLatentVar = ltVariables.newLatentVariable(leftLatentVariable, 0);
        LatentVariable rightLatentVar = ltVariables.newLatentVariable(rightLatentVariable, 1);

        // Creamos el LTDAG
        LTDAG ltdag = new LTDAG(ltVariables);

        // Añadimos los arcos de las variable latentes a las variables observadas
        // Cada variable observada pertenece solo a una LV
        for(ObservedVariable observedVar : observedVariables){
            if(leftAttributes.contains(observedVar.getVariable().getAttribute()))
                ltdag.addParent(observedVar,leftLatentVar);
            else
                ltdag.addParent(observedVar,rightLatentVar);
        }

        // Convertimos la LV de la izquierda en raiz del LTM (teoricamente,  tal y como explica Zhang,
        // da igual quien sea la raiz, y con esto reducimos el numero de posibilidades a la mitad)
        ltdag.addParent(rightLatentVar,leftLatentVar);

        // Aprendemos los parámetros del LTM
        LearnKnownStructureLTM learner = new LearnKnownStructureLTM(parameterLearningAlgorithm, ltdag);
        return learner.learnModel(batch);
    }

    // Metodo que aprende un flat-LTM a partir de un arbol dirigido y una lista de LTMs
    public LTM learnFlatLTM(DirectedTree latentVarsTree, ArrayList<LTM> ltms, DataOnMemory<DataInstance> batch){

        // El primer paso es crear el objeto variables. La unica forma es mediante un objeto Attributes, el cual se crea
        // a partir de una lista de Attribute objects. El porque no utilizamos los attributes del batch es por si no
        // se utilizan todos, es decir, por si se ha hecho algun tipo de Feature Subset Selection al crear los LTMs
        List<Attribute> observedAttributes = new ArrayList<>();
        for(LTM latentTreeModel : ltms) {
            // Iteramos por sus variables observadas para obtener los objetos Attribute asociados
            for (ObservedVariable var : latentTreeModel.getLtdag().getObservedVariables())
                observedAttributes.add(var.getVariable().getAttribute());
        }
        Attributes selectedAttributes = new Attributes(observedAttributes);

        // Transformamos la lista de atributos en un objeto Attributes, necesario para crear las variables
        // de nuestro modelo
        Variables variables = new Variables(selectedAttributes);
        LTVariables ltVariables = new LTVariables(variables);

        // Creamos las variables observadas del nuevo LTM
        List<ObservedVariable> observedVariables = new ArrayList<>();
        for(Variable var: variables){
            ObservedVariable observedVar = ltVariables.newObservedVariable(var);
            observedVariables.add(observedVar);
        }

        // Ahora creamos las variables latentes del nuevo LTM, que coinciden con las variables latentes del array de LTMs
        List<LatentVariable> latentVariables = new ArrayList<>();
        for(int treeIndex = 0; treeIndex < ltms.size(); treeIndex++) {
            int lvCardinality = ltms.get(treeIndex).getLtdag().getLatentVariables().get(0).getVariable().getNumberOfStates();
            String[] latentVarStates = new String[lvCardinality];
            for(int i = 0; i<lvCardinality;i++){
                latentVarStates[i] = i+"";
            }
            Variable latentVariable = variables.newMultionomialVariable("H"+treeIndex, Arrays.asList(latentVarStates));
            latentVariables.add(ltVariables.newLatentVariable(latentVariable, treeIndex));
        }

        // Creamos el LTDAG con las anteriores variables
        LTDAG ltdag = new LTDAG(ltVariables);

        // Añadimos las conexiones entre las variables observadas y las variables latentes
        // Iteramos por los LTMs y conectamos las variables observadas del LTM con su variable

        // Nota: Este rollo es porque trabajamos con 2 objetos casi identicos de variables, los de los antiguos LTMs separados
        // Y los del neuvo LTM conjunto
        // TODO: Revisar este codigo posteriormente para reducirlo ya que a lo mejor no es necesaria tanta comprobacion
        for(LTM ltm : ltms){

            for(LatentVariable ltVar : ltm.getLtdag().getLatentVariables()){

                LatentVariable latentVar = null;
                for(LatentVariable latentVariable : latentVariables){
                    if(latentVariable.getIndex() == ltVar.getIndex())
                        latentVar = latentVariable;
                }

                for(ObservedVariable obvVar : ltm.getLtdag().getObservedVariables()){
                    for(ObservedVariable observedVariable : observedVariables)
                        if(observedVariable.getVariable().getAttribute().equals(obvVar.getVariable().getAttribute()))
                            ltdag.addParent(observedVariable,latentVar);
                }
            }

        }

        // Añadimos las conexiones entre las variables latentes
        Map<Integer, Integer> latentVarConnections = latentVarsTree.getEdges();
        for(Integer parentIndex: latentVarConnections.keySet()){
            LatentVariable parent = latentVariables.get(parentIndex);
            LatentVariable son = latentVariables.get(latentVarConnections.get(parentIndex));
            ltdag.addParent(parent, son);
        }

        // Aprendemos los parámetros del LTM
        LearnKnownStructureLTM learner = new LearnKnownStructureLTM(parameterLearningAlgorithm, ltdag);
        return learner.learnModel(batch);
    }

    private class LearnKnownStructureLTM {

        // Le pasamos un LTDAG y nos aprende los parámetros mediante el algoritmo de aprendizaje
        // de parametros seleccionado

        /** Parameter learning algorithm used to fully learn the LTM */
        private ParameterLearningAlgorithm parameterLearningAlgorithm;

        private LTDAG ltdag;

        public LearnKnownStructureLTM(ParameterLearningAlgorithm parameterLearningAlgorithm, LTDAG ltdag){
            this.parameterLearningAlgorithm = parameterLearningAlgorithm;
            this.ltdag = ltdag;
            parameterLearningAlgorithm.setDAG(ltdag.getDAG());
            this.parameterLearningAlgorithm.initLearning();
        }

        /**
         *
         * @param batch
         */
        public LTM learnModel(DataOnMemory<DataInstance> batch){
            double modelScore = parameterLearningAlgorithm.updateModel(batch);
            BayesianNetwork learntModel = parameterLearningAlgorithm.getLearntBayesianNetwork();
            return new LTM(learntModel, modelScore, ltdag);
        }

    }
}
