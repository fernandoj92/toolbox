package mt.ferjorosa.core.learning.structural;

import eu.amidst.core.datastream.Attribute;
import eu.amidst.core.datastream.Attributes;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.learning.parametric.ParameterLearningAlgorithm;
import eu.amidst.core.learning.parametric.bayesian.SVB;
import eu.amidst.core.variables.Variable;
import mt.ferjorosa.core.learning.LTMLearningEngine;
import mt.ferjorosa.core.learning.structural.variables.FSSMeasure;
import mt.ferjorosa.core.learning.structural.variables.MutualInformation;
import mt.ferjorosa.core.models.LTM;
import mt.ferjorosa.core.models.ltdag.*;
import mt.ferjorosa.core.util.graph.DirectedTree;
import mt.ferjorosa.core.util.graph.UndirectedGraph;
import org.apache.commons.lang3.tuple.Pair;


import java.util.*;
import java.util.stream.Collectors;

/**
 * This is an approximation of the Bridged Islands algorithm proposed by Liu et.al in their article:
 *
 * Liu, T., Zhang, N. L., Poon, K. M., Liu, H., & Wang, Y. (2012, September). A novel ltm-based method for multi-partition clustering.
 * In 6th European Workshop on Probabilistic Graphical Models (pp. 203-210).
 *
 * This algorithm learns both the structure and the parameters of a Bayesian network. Its composed of 4 stages
 * 1 - Calculate the sibling clusters.
 * 2 - Generate a LCM (a LCM is a LTM with only one Latent variable)from each sibling cluster by assigning a Latent
 * variable on top of the chosen observed variables.
 * 3 - Learn each LCM's parameters, then repeatedly consider to increase the cardinality of the LV, stopping when the
 * used score decreases (BIC, BDe, AIC,etc., depending on the parameter Learning algorithm used).
 * 4 - Determine the connections among the latent variables so that they form a tree (LTM).
 * 5 - Refine the model.
 * 6* - Determine possible connections between the observed variables.
 *
 *
 * TODO: Ademas deberia tenerse en cuenta que hay un mínimo en el numero de atributos para poder ejecutar el algoritmo
 * TODO: Es posible hacer estatica la clase y utilizarla como un gran método, pero bueno, depende de como se aplique en el
 * streaming
 */
public class ApproximateBIAlgorithm implements StructuralLearning {

    /** Valor asignado segun el código original de Zhang */
    private FSSMeasure siblingClustersMeasure;

    /** Valor asignado segun el código original de Zhang */
    private Attributes initialAttributes;

    /** Valor asignado segun el código original de Zhang */
    private List<Attribute> outSetAttributes;

    /** Valor asignado segun el código original de Zhang */
    private LTMLearningEngine ltmLearner;

    /** Valor asignado segun el código original de Zhang */
    private int maxIslandSize = 30;

    /** Valor asignado segun el código original de Zhang */
    private int baseLvCardinality = 2;

    /** Valor asignado segun el código original de Zhang */
    private ArrayList<LTM> siblingClusters = new ArrayList<>();


    public ApproximateBIAlgorithm(Attributes attributes){
        this.initialAttributes = attributes;
        this.ltmLearner = new LTMLearningEngine(new SVB());
        this.siblingClustersMeasure = new MutualInformation();
    }

    public ApproximateBIAlgorithm(Attributes attributes, ParameterLearningAlgorithm parameterLearningAlgorithm){
        this.initialAttributes = attributes;
        this.ltmLearner = new LTMLearningEngine(parameterLearningAlgorithm);
        this.siblingClustersMeasure = new MutualInformation();
    }

    public ApproximateBIAlgorithm(Attributes attributes, ParameterLearningAlgorithm parameterLearningAlgorithm, FSSMeasure siblingClustersMeasure){
        this.initialAttributes = attributes;
        this.ltmLearner = new LTMLearningEngine(parameterLearningAlgorithm);
        this.siblingClustersMeasure = siblingClustersMeasure;
    }

    /**
     * @param batch a {@link DataOnMemory} object.
     * @return
     */
    // Creo que deberia devolver un score indicando como de bien representa el LTM actual el batch de datos
    // con el cual se ha aprendido (e.g elBO)
    @Override
    public LTM learnModel(DataOnMemory<DataInstance> batch) {
        if(batch.getAttributes() != this.initialAttributes)
            throw new IllegalArgumentException("Batch attributes don't correspond with initial attributes");

        siblingClustersMeasure.setData(batch);

        // 1 - Calculate sibling clusters (islands)
        calculateSiblingClusters(batch);
        // Modificamos los LCM

        // 2 - Learn the LCM parameters and determine their LV's cardinality
        refineSiblingClustersCardinality(batch);

        // 3 - Form a Tree by connecting the sibling clusters
        // One possibility is to use the Chow-Liu's algorithm
        LTM ltmLearned = connectSiblingClusters(batch);

        // 4 - Refine the model by doing the following:
        /*
            4.1 - Cambiar la cardinalidad de cada LV y ver si mejora en algo el score conjunto
                   Nota: No tiene sentido en mi opinion refinar la cardinalidad en 2 sitios
            4.2 -
         */
        return refineModel(ltmLearned);
    }

    private void calculateSiblingClusters(DataOnMemory<DataInstance> batch){

        outSetAttributes = batch.getAttributes().getFullListOfAttributes();

        // Calculamos la información mútua entre todos los atributos
        siblingClustersMeasure.computeAllPairScores(outSetAttributes);

        while(outSetAttributes.size() > 0){
            LTM singleIslandLTM = singleIslandLearner(batch);
            // Almacenamos cada una de las islas generadas
            siblingClusters.add(singleIslandLTM);
        }
    }

    private LTM singleIslandLearner(DataOnMemory<DataInstance> batch){
        // Cada isla posee su propio activeSet, sin embargo, el outSet es general para todos, ya que inicialmente
        // contiene todos los atributos

        boolean UnidimensionalityTestCondition = true;

        // Seleccionamos el par de atributos con el mayor valor de información mútua
        Pair<Attribute, Attribute> bestPair =  siblingClustersMeasure.getBestPair(outSetAttributes);

        // Creamos el Active Set que va a tener los atributos utilizados en la isla
        List<Attribute> activeSet = new ArrayList<>();

        activeSet.add(bestPair.getLeft());
        activeSet.add(bestPair.getRight());

        outSetAttributes.remove(bestPair.getLeft());
        outSetAttributes.remove(bestPair.getRight());

        LTM islandLTM = null;

        //Tras haber escogido el primer par de atributos que van a formar parte de la isla, iteramos
        // y vamos añadiendo nuevos atributos hasta que se deje de cumplir alguna de las siguientes condiciones
        while(!outSetAttributes.isEmpty() && UnidimensionalityTestCondition && activeSet.size() <= maxIslandSize) {

            Attribute closestAttribute = siblingClustersMeasure.getClosestAttributeToSet(activeSet,outSetAttributes);

            activeSet.add(closestAttribute);

            if(activeSet.size() >= 4 || outSetAttributes.size() < 4){
                LTM ltmSubModel = ltmLearner.learnUnidimensionalLTM(activeSet, batch, 2);
                LTM ltm2dimensionalBestModel = findBest2dimensionalLTM(activeSet, batch);

                islandLTM = ltmSubModel;
                // check the Unidimensionality Test Condition
                // Si el score del modelo multidimensional es mejor que el del modelo unidimensional,
                if(ltmSubModel.getScore() < ltm2dimensionalBestModel.getScore()) {
                    UnidimensionalityTestCondition = false;
                    activeSet.remove(closestAttribute);
                    islandLTM = null;
                }
            }
        }
        // Siempre se aprende al menos un LTM de dos variables, las 2 variables con mayor MI del outSet de atributos
        if(islandLTM == null)
            islandLTM = ltmLearner.learnUnidimensionalLTM(activeSet, batch, 2);

        return islandLTM;
    }

    private LTM findBest2dimensionalLTM(List<Attribute> attributes, DataOnMemory<DataInstance> batch){

        ArrayList<int[]> leftCombinations = generateAttributeCombinations(attributes);
        double bestScore = 0;
        LTM bestLTM = null;

        for(int[] attributeCombination: leftCombinations){
            List<Attribute> leftAttributes = new ArrayList<>();
            List<Attribute> rightAttributes = new ArrayList<>();

            for(int i = 0; i< attributeCombination.length; i++){
                leftAttributes.add(attributes.get(attributeCombination[i]));
            }
            rightAttributes = attributes.stream()
                    .filter( leftAttributes::contains)
                    .collect(Collectors.toList());

            LTM model = ltmLearner.learn2dimensionalLTM(leftAttributes, rightAttributes, batch);
            if(model.getScore() > bestScore)
                bestLTM = model;
        }
        return bestLTM;
    }

    private ArrayList<int[]> generateAttributeCombinations(List<Attribute> attributes){

        ArrayList<int[]> leftCombinations = new ArrayList<>();

        // El primer paso es generar todas las combinaciones no repetidas de atributos con las que formar un LTM
        // con 2 variables latentes
        int numberOfAttributes = attributes.size();
        for(int leftTreeSize = 2 ; leftTreeSize <= numberOfAttributes/2; leftTreeSize++) {
            // Almacenamos el indice respecto a la lista de atributos del activeSet (no el indice real del atributo)
            int[] leftIndexes = new int[leftTreeSize];
            // Inicializamos los indices no-repetidos
            for (int alpha = 0; alpha < leftIndexes.length; alpha++) {
                leftIndexes[alpha] = alpha;
            }

            int pos = leftIndexes.length - 1; // leftIndexes.final
            int resetPos = leftIndexes.length - 1;

            while (leftIndexes[0] < (numberOfAttributes - (leftIndexes.length - 1))){
                // Siempre iteramos en la última posicion, lo demas son "resets"
                while(leftIndexes[pos] < numberOfAttributes){
                    leftCombinations.add(leftIndexes);
                    leftIndexes[pos]++;
                }
                // Reset
                if(resetPos >= 0){

                    if(leftIndexes[resetPos] >= numberOfAttributes){
                        resetPos = resetPos - 1;
                    }

                    int valor = leftIndexes[resetPos];
                    for(int j=resetPos;j < leftIndexes.length;j++){
                        valor = valor + 1;
                        leftIndexes[j] = valor;
                    }
                }
            }
        }

        return  leftCombinations;
    }

    private void refineSiblingClustersCardinality(DataOnMemory<DataInstance> batch){

        ArrayList<LTM> newSiblingClusters = new ArrayList<>();

        for(LTM cluster: siblingClusters){

            LTM currentModel = cluster;

            // Hacemos un while y mientras que el nuevo modelo unidimensional con cardinalidad + 1 sea superior, seguimos
            // aumentando la cardinalidad.
            List<Attribute> clusterAttributes = new ArrayList<>();
            for(Variable variable : cluster.getLearntModel().getVariables())
                clusterAttributes.add(variable.getAttribute());

            int currentCardinality = baseLvCardinality;
            boolean isNotFalse = true;
            while(isNotFalse) {
                currentCardinality++;

                LTM newModel = ltmLearner.learnUnidimensionalLTM(clusterAttributes, batch, currentCardinality);

                if(newModel.getScore() < currentModel.getScore())
                    // La nueva cardinalidad da un score inferior y por lo tanto cortamos la greedy search
                    isNotFalse = false;
                else
                    // Le nueva cardinalidad da mejor score, asi que almacenamos el modelo refinado
                    currentModel = newModel;
            }

            newSiblingClusters.add(currentModel);
        }

        this.siblingClusters = newSiblingClusters;
    }

    // Chow Liu approximation
    private LTM connectSiblingClusters(DataOnMemory<DataInstance> batch){
        // Entendemos cada sibling cluster como un nodo, ya que la union solo se va a realizar sobre sus LVs

        // Chow-Liu’s algorithm can be adapted to link up the latent variables of the sibling clusters. We only
        // need to specify how the MI between two latent variables from two disjoint LCMs is to be estimated.

        // In this case we are using an approximation to the MI between two LVs. Its value will be the highest MI between
        // their OV children

        UndirectedGraph completeGraph = new UndirectedGraph(siblingClusters.size());

        // Iteramos por cada sibling cluster para rellenar los pesos del grafo no dirigido
        for(int i =0; i < siblingClusters.size(); i++)
            for(int j =0; j < siblingClusters.size(); j++){
                if(i != j){
                    List<Attribute> clusterAttributes = new ArrayList<>();
                    List<Attribute> pairClusterAttributes = new ArrayList<>();

                    List<ObservedVariable> clusterOVs = siblingClusters.get(i).getLtdag().getObservedVariables();
                    List<ObservedVariable> pairClusterOVs = siblingClusters.get(j).getLtdag().getObservedVariables();

                    for(ObservedVariable observedVariable: clusterOVs)
                        clusterAttributes.add(observedVariable.getVariable().getAttribute());

                    for(ObservedVariable observedVariable: pairClusterOVs)
                        pairClusterAttributes.add(observedVariable.getVariable().getAttribute());

                    // Ahora que ya tenemos los atributos de ambos clusters, calculamos la maxima MI
                    // y la almacenamos como el peso del edge entre ambos nodos del grafo

                    double score = siblingClustersMeasure.getMaxBivariateScore(clusterAttributes,pairClusterAttributes);

                    completeGraph.addEdge(i,j,score);
                }
            }

        // Una vez tenemos el grafo completo, calculamos el MWST
        UndirectedGraph mwst = UndirectedGraph.obtainMaximumWeightSpanningTree(completeGraph);

        // Tras obtener el MWST seleccionamos un nodo como raiz y lo convertimos en un arbol dirigido
        Random rn = new Random();
        int rootIndex = rn.nextInt(mwst.getNVertices());
        DirectedTree rootedMWST = new DirectedTree(mwst, rootIndex);

        // Finalmente conectamos los clusters según el MWST formando un LTM
        return ltmLearner.learnFlatLTM(rootedMWST,siblingClusters,batch);

    }

    private LTM refineModel(LTM ltm){
        return ltm;
    }

}
