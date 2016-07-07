package mt.ferjorosa.core.learning.structural;

import eu.amidst.core.datastream.Attribute;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.learning.parametric.ParameterLearningAlgorithm;
import eu.amidst.core.learning.parametric.bayesian.SVB;
import mt.ferjorosa.core.learning.LTMLearningEngine;
import mt.ferjorosa.core.learning.structural.variables.FSSMeasure;
import mt.ferjorosa.core.learning.structural.variables.MutualInformation;
import mt.ferjorosa.core.models.LTM;
import mt.ferjorosa.core.models.ltvariables.ObservedVariable;
import mt.ferjorosa.core.util.graph.DirectedTree;
import mt.ferjorosa.core.util.graph.UndirectedGraph;
import mt.ferjorosa.core.util.pair.SymmetricPair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * This is an approximation of the Bridged Islands algorithm proposed by Liu et.al in their article:
 *
 * Liu, T., Zhang, N. L., Poon, K. M., Liu, H., & Wang, Y. (2012, September). A novel ltm-based method for multi-partition
 * clustering. In 6th European Workshop on Probabilistic Graphical Models (pp. 203-210).
 *
 * Later extended in this article:
 *
 * Liu, T. F., Zhang, N. L., Chen, P., Liu, A. H., Poon, L. K., & Wang, Y. (2015). Greedy learning of latent tree models
 * for multidimensional clustering. Machine Learning, 98(1-2), 301-330.
 *
 * This algorithm learns both the structure and the parameters of a Bayesian network. Its composed of 4 stages
 * 1 - Calculate the sibling clusters.
 * 2 - Generate a LCM (a LCM is a LTM with only one Latent variable) from each sibling cluster by assigning a Latent
 * variable on top of the chosen observed variables.
 * 3 - Learn each LCM's parameters, then repeatedly consider to increase the cardinality of the LV, stopping when the
 * used score decreases (BIC, BDe, AIC,etc., depending on the parameter Learning algorithm used).
 * 4 - Determine the connections among the latent variables so that they form a tree (LTM).
 * 5 - Refine the model.
 * 6* - Determine possible connections between the observed variables.
 *
 *
 * TODO: Deberia tenerse en cuenta que hay un mínimo en el numero de atributos para poder ejecutar el algoritmo
 * TODO: Mirar si un mejor score es MAYOR o MENOR que un peor score (findBest2dimensionalModel)
 * TODO: Recordar  al hacer ejemplos o tests de el metodo de generar combinaciones el añadir un comentario en la doc del metodo
 * TODO: Punto 6 del algoritmo
 * TODO: Este algoritmo siempre genera un LCM de 3 variables cuando recibe solo 3 atributos, aunque busque un arbol de 2 LVs, siempre queda igual.
 * TODO: connectSiblingClusters escoge una raiz del MWST de forma aleatoria.
 * TODO: Cambiar la forma en la que se calcula la MI de las variables latentes (rellenar el dataset)
 * TODO: calculateSiblingClusters para <= 2 necesita repaso, ya que lo de añadir esas dos variables a dos clusters...no se
 * TODO: Cada learnModel debe resetear las entrañas del algoritmo, ya que sino se queda basura de el anterior aprendizaje
 */
public class ApproximateBIAlgorithm implements StructuralLearning {

    /** The measure that is going to be used to select the closest attributes when forming the sibling clusters. */
    private FSSMeasure siblingClustersMeasure;

    /** List of attributes used when constructing a single cluster (island). */
    private List<Attribute> outSetAttributes;

    /** The Latent Tree Model learning engine. */
    private LTMLearningEngine ltmLearner;

    /** The list of unconnected LCMs, also called sibling clusters. */
    private ArrayList<LTM> siblingClusters;

    /** Configuration parameters for the algorithm. */
    private ApproximateBIConfig config;

    /**
     * Default constructor for the Bridged Islands algorithm. It creates an instance of the algorithm with the
     * "Streaming Variational Bayes" as the parameter learning algorithm and the "Mutual Information" as the FSS Measure
     */
    public ApproximateBIAlgorithm(ApproximateBIConfig config){

        // Defines the default parameter learning algorithm
        SVB streamingVariationalBayes = new SVB();
        streamingVariationalBayes.setWindowsSize(100);

        // Assign the parameter learning algorithm to the LTM learning engine
        this.ltmLearner = new LTMLearningEngine(streamingVariationalBayes);
        this.siblingClustersMeasure = new MutualInformation();

        // Algorithm's configuration
        this.config = config;
    }

    /**
     * Creates an instance of the algorithm with a specific parameter learning algorithm and the "Mutual Information" as
     * the FSS Measure.
     * @param parameterLearningAlgorithm the selected parameter learning algorithm that is going to be used after
     *                                   learning the LTM structure.
     */
    public ApproximateBIAlgorithm(ApproximateBIConfig config, ParameterLearningAlgorithm parameterLearningAlgorithm){
        this.config = config;
        this.ltmLearner = new LTMLearningEngine(parameterLearningAlgorithm);
        this.siblingClustersMeasure = new MutualInformation();
    }

    /**
     * Creates an instance of the algorithm with a specific parameter learning algorithm and FSS Measure.
     * @param parameterLearningAlgorithm the selected parameter learning algorithm that is going to be used after
     *                                   learning the LTM structure.
     * @param siblingClustersMeasure the measure that is going to be used to select the closest attributes when forming
     *                               the sibling clusters.
     */
    public ApproximateBIAlgorithm(ApproximateBIConfig config, ParameterLearningAlgorithm parameterLearningAlgorithm, FSSMeasure siblingClustersMeasure){
        this.config = config;
        this.ltmLearner = new LTMLearningEngine(parameterLearningAlgorithm);
        this.siblingClustersMeasure = siblingClustersMeasure;
    }

    /**
     * Learns a Flat-LTM using a given {@link DataOnMemory} object.
     * @param batch a {@link DataOnMemory} object that is going to be used to learn the model.
     * @return the learnt LTM.
     */
    @Override
    public LTM learnModel(DataOnMemory<DataInstance> batch) {

        this.siblingClusters = new ArrayList<>();
        siblingClustersMeasure.setData(batch);

        // 1 - Calculate sibling clusters (islands)
        calculateSiblingClusters(batch);

        int cont = 0;
        for(LTM island : siblingClusters){
            System.out.println("Island "+cont+":");

            String s = "";
            for(ObservedVariable obv : island.getLtdag().getObservedVariables()){
                s += obv.getAttribute().getName() + ",";
            }
            System.out.println(s);
            System.out.println("-------------");
            cont++;
        }

        // 2 - Refine each cluster's LV cardinality
        refineSiblingClustersCardinality(batch);

        // 3 - Form a Tree by connecting the sibling clusters. It connects each cluster's latent variable forming a
        // Maximum Weight Spanning Tree and then randomly chooses a cluster as root.
        LTM ltmLearned = connectSiblingClusters(batch);

        // TODO: 4 - Refine the model by doing the following:
        /*
            4.1 - Cambiar la cardinalidad de cada LV y ver si mejora en algo el score conjunto
                   Nota: No tiene sentido en mi opinion refinar la cardinalidad en 2 sitios
            4.2 -
         */
        return refineModel(ltmLearned);
    }

    /**
     * Learns each sibling cluster (unconnected LCMs) and store them in a list.
     * @param batch a {@link DataOnMemory} object that is going to be used to learn the model.
     */
    private void calculateSiblingClusters(DataOnMemory<DataInstance> batch){
        outSetAttributes = new ArrayList<>(batch.getAttributes().getFullListOfAttributes());

        // We first compute all the attributes' bivariate MI scores
        siblingClustersMeasure.computeAllPairScores(outSetAttributes);

        while(outSetAttributes.size() > 0){

            // Special case when we only have 2 attributes left
            if(outSetAttributes.size() <= 2){
                if(siblingClusters.isEmpty()){
                    // If we only have 2 attributes in the dataSet, a LCM is learned and most the next steps will be avoided
                    LTM ltmWith2Variables = ltmLearner.learnUnidimensionalLTM(outSetAttributes,config.getBaseLvCardinality(), siblingClusters.size(),batch);
                    siblingClusters.add(ltmWith2Variables);
                }else{
                    // We look for the best sibling cluster to add each of these attributes
                    // when we find it, we remove that substitute that sibling cluster with a  new one with an extra
                    // observed variable.
                    LTM improvedLTM = searchBestCluster(outSetAttributes.get(0), batch);
                    outSetAttributes.remove(outSetAttributes.get(0));
                    siblingClusters.add(improvedLTM);
                }
            }else {
                LTM singleIslandLTM = singleIslandLearner(batch);
                siblingClusters.add(singleIslandLTM);
            }
        }
    }

    /**
     * Searches in all the learnt clusters looking for the best match for a "lonely" attribute.
     * @param attribute the attribute that needs to be inserted.
     * @param batch a {@link DataOnMemory} object that is going to be used to learn the model.
     * @return the new LTM with the passed attribute.
     */
    private LTM searchBestCluster(Attribute attribute, DataOnMemory<DataInstance> batch){

        // simple initialization.
        LTM bestcluster = siblingClusters.get(0);
        double bestScore = 0;

        // Creates a single item list.
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(attribute);

        // Iterates the sibling clusters looking for the cluster that maximizes the bivariate score with the attribute
        // to find the best-suited cluster.
        for(LTM cluster: siblingClusters){
            List<Attribute> clusterAttributes = cluster.getLtdag().getLTVariables().getAttributes();
            double currentScore = siblingClustersMeasure.getMaxBivariateScore(clusterAttributes, attributes);
            if(currentScore > bestScore){
                bestScore = currentScore;
                bestcluster = cluster;
            }
        }

        // Learns a new LTM with one more attribute, removes the old cluster and returns the new one.
        List<Attribute> newClusterAttributes = bestcluster.getLtdag().getLTVariables().getAttributes();
        newClusterAttributes.add(attribute);
        siblingClusters.remove(bestcluster);
        return ltmLearner.learnUnidimensionalLTM(newClusterAttributes, config.getBaseLvCardinality(), siblingClusters.size(), batch);
    }

    /**
     * Learns a LCM (a LTM with only one Latent variable) by using a heuristic proposed by Liu et.al
     * @param batch a {@link DataOnMemory} object that is going to be used to learn the model.
     * @return the cluster.
     */
    private LTM singleIslandLearner(DataOnMemory<DataInstance> batch){

        /**
         * Each cluster has its own not-sharable list of attributes: the "activeSet".
         * The "outSet" contains the attributes that doesn't currently belong to a cluster.
         */

        // The UD test boolean variable. If false, the cluster expansion stops.
        boolean UnidimensionalityTestCondition = true;

        // Selects the pair of attributes with the highest MI.
        SymmetricPair<Attribute, Attribute> bestPair =  siblingClustersMeasure.getBestPair(outSetAttributes);

        // Creates the activeSet, containing the list of attributes that belong only to this cluster.
        List<Attribute> activeSet = new ArrayList<>();

        activeSet.add(bestPair.getFirst());
        activeSet.add(bestPair.getSecond());

        outSetAttributes.remove(bestPair.getFirst());
        outSetAttributes.remove(bestPair.getSecond());

        LTM islandLTM = null;

        /**
         * After choosing the best pair of attributes that are going to form the cluster's activeSet, it keeps adding
         * new attributes until one of the following conditions is false:
         * - The Unidimensionality test has failed.
         * - There are no more attributes left.
         * - The maximum allowed number of attributes inside an island have been reached.
         */
        while(!outSetAttributes.isEmpty() && UnidimensionalityTestCondition && activeSet.size() <= config.getMaxIslandSize()) {

            // Looks for the next closest attribute to the activeSet
            Attribute closestAttribute = siblingClustersMeasure.getClosestAttributeToSet(activeSet,outSetAttributes);
            activeSet.add(closestAttribute);
            outSetAttributes.remove(closestAttribute);

            // BI uses heuristics to construct the LTM. After >= 4 attributes in the activeSet, it starts executing the UD test.
            if(activeSet.size() >= 4){

                LTM ltmSubModel = ltmLearner.learnUnidimensionalLTM(activeSet, config.getBaseLvCardinality(), siblingClusters.size(), batch);
                LTM ltm2dimensionalBestModel = findBest2dimensionalLTM(activeSet, batch);

                islandLTM = ltmSubModel;

                /**
                 * This is the Unidimensionality test. It compares the score of a 2-dimensional LTM vs a LCM (1-dimensional LTM)
                 * In case that the 2-dimensional has higher score than the LCM then we do not consider the previously
                 * chosen attribute and the cluster's expansion stops.
                 */

                double unidimensionalScore = ltmSubModel.getScore();
                double bidimensionalScore = ltm2dimensionalBestModel.getScore();
                // Fails the UD test if its score difference is lower than the threshold
                if(unidimensionalScore < bidimensionalScore ||
                        (unidimensionalScore - bidimensionalScore) < (config.getUdTestThreshold() * (-unidimensionalScore) / 100))
                {
                    UnidimensionalityTestCondition = false;
                    activeSet.remove(closestAttribute);
                    outSetAttributes.add(closestAttribute);
                    islandLTM = null;
                }
            }
        }

        // It always learns a LTM, at least with 2 variables, current best pair.
        if(islandLTM == null)
            islandLTM = ltmLearner.learnUnidimensionalLTM(activeSet, config.getBaseLvCardinality(), siblingClusters.size(), batch);


        //System.out.println("isla de "+islandLTM.getLtdag().getObservedVariables().size()+" variables");

        return islandLTM;
    }

    /**
     * Searches the best 2-dimensional LTM (with 2 latent variables) for the list of attributes. It generates a list of
     * LTMs with respect to a list of possible attribute combinations. After learning each of these LTMs, it returns the
     * one with the highest score.
     * @param attributes the list of attributes used to construct the LTMs.
     * @param batch a {@link DataOnMemory} object that is going to be used to learn the models.
     * @return the best 2-dimensional LTM.
     */
    private LTM findBest2dimensionalLTM(List<Attribute> attributes, DataOnMemory<DataInstance> batch){

        /**
         * First step is to generate all non-repeated attribute combinations with 2 latent variables. This is an approximation
         * of the procedure used by Liu et.al. They use a variant of the Hill Climbing algorithm called "EAST", so to
         * obtain similar results without the EAST algorithm, this algorithm acts similar by searching specific attribute
         * combinations.
         */
        ArrayList<int[]> leftCombinations = generateAttributeCombinations(attributes.size());

        // The score of the best model
        double bestScore = Double.NEGATIVE_INFINITY;

        // The best known model
        LTM bestLTM = null;

        for(int[] attributeCombination: leftCombinations){
            List<Attribute> leftAttributes = new ArrayList<>();
            List<Attribute> rightAttributes = new ArrayList<>();

            // Left-Tree attributes are returned as an array of attribute indexes. We transform it to a list of attributes.
            for(int i = 0; i< attributeCombination.length; i++)
                leftAttributes.add(attributes.get(attributeCombination[i]));

            // It only returns the left tree attributes, so to know the right tree attributes it is necessary to filter.
            rightAttributes = attributes.stream()
                    .filter(attribute -> !leftAttributes.contains(attribute))
                    .collect(Collectors.toList());

            LTM model = ltmLearner.learn2dimensionalLTM(leftAttributes, rightAttributes, config.getBaseLvCardinality(), config.getBaseLvCardinality(), batch);

            // TODO: Definir un metodo que compare scores, ya que dependiendo del score la comparacion es diferente
            // (mayor que no tiene porque significar siempre que el score sea mejor)
            if(model.getScore() > bestScore)
                bestLTM = model;
        }
        return bestLTM;
    }

    /**
     * It is an utility method that is only used in this class (at least for the moment). It generates a list of attribute
     * combinations for 2-dimensional LTMs. Instead of returning a pair of arrays of attribute indexes(left and right trees),
     * it only returns the left tree ones, so afterwards it would be necessary to filter to obtain the right tree attributes.
     * @param numberOfAttributes the number of attributes.
     * @return a list of arrays containing the left tree attribute indexes.
     */
    private ArrayList<int[]> generateAttributeCombinations(int numberOfAttributes){

        ArrayList<int[]> leftCombinations = new ArrayList<>();

        // First step is to generate all the non-repeated combinations of attributes with which construct a 2-latent-variable-LTM
        for(int leftTreeSize = 2 ; leftTreeSize <= numberOfAttributes/2; leftTreeSize++) {

            // Stores its index with respect to the activeSet (not its real attribute index).
            int[] leftIndexes = new int[leftTreeSize];

            // Initialize the non-repeated indexes
            for (int alpha = 0; alpha < leftIndexes.length; alpha++) {
                leftIndexes[alpha] = alpha;
            }

            int pos = leftIndexes.length - 1; // leftIndexes.final
            int resetPos = leftIndexes.length - 1;

            while (leftIndexes[0] < (numberOfAttributes - (leftIndexes.length - 1))){

                // It always iterates through the last position, everything else is called a "reset"
                while(leftIndexes[pos] < numberOfAttributes){
                    leftCombinations.add(leftIndexes.clone());
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

    /**
     * Refines current list of unconnected clusters (LCMs) by increasing their cardinality until the learnt model's score
     * ceases to increase.
     * @param batch a {@link DataOnMemory} object that is going to be used to learn the models.
     */
    private void refineSiblingClustersCardinality(DataOnMemory<DataInstance> batch){

        // A list with the refined sibling clusters
        ArrayList<LTM> newSiblingClusters = new ArrayList<>();

        for(LTM cluster: siblingClusters){

            LTM currentModel = cluster;
            List<Attribute> clusterAttributes = cluster.getLtdag().getLTVariables().getAttributes();

            // Starting with the base cardinality
            int currentCardinality = config.getBaseLvCardinality();
            boolean increaseCardinality = true;

            // Increase the model's cardinality until its score ceases to increase.
            while(increaseCardinality) {
                currentCardinality++;

                LTM newModel = ltmLearner.learnUnidimensionalLTM(clusterAttributes, currentCardinality, newSiblingClusters.size(), batch);

                if(newModel.getScore()  <= currentModel.getScore())
                    // New cardinality doesn't improve the score, therefore the while loop is stopped
                    increaseCardinality = false;
                else
                    // New cardinality improves the model's score, so it stores current refined model
                    currentModel = newModel;
            }

            newSiblingClusters.add(currentModel);
        }

        this.siblingClusters = newSiblingClusters;
    }

    /**
     * This method connects the list of sibling clusters by their latent variables. It uses a Chow-Liu variation to
     * generate the Maximum Weight Spanning Tree. Chow-Liu’s algorithm can be adapted to link up the latent variables of
     * the sibling clusters. It needs to specify how the MI between two latent variables from two disjoint LCMs is to be
     * estimated.
     *
     * In this case, instead of using the MI between the latent variables (which is not easy to do because
     * they are not present in the batch of instances) it uses an approximation: ts value will be the highest MI between
     * their children observed variables.
     * @param batch a {@link DataOnMemory} object that is going to be used to learn the models.
     * @return the flat-LTM generated.
     */
    private LTM connectSiblingClusters(DataOnMemory<DataInstance> batch){

        // An undirected graph is created to represent the MWST between the Latent Variables.
        UndirectedGraph completeGraph = new UndirectedGraph(siblingClusters.size());

        // Iterates through each pair of clusters to fill the undirected graph edges
        for(int i =0; i < siblingClusters.size(); i++)
            for(int j =0; j < siblingClusters.size(); j++){
                if(i != j){
                    // It is necessary to obtain right and left attributes in order to calculate their max bivariate MI
                    List<Attribute> clusterAttributes = new ArrayList<>();
                    List<Attribute> pairClusterAttributes = new ArrayList<>();

                    List<ObservedVariable> clusterOVs = siblingClusters.get(i).getLtdag().getObservedVariables();
                    List<ObservedVariable> pairClusterOVs = siblingClusters.get(j).getLtdag().getObservedVariables();

                    for(ObservedVariable observedVariable: clusterOVs)
                        clusterAttributes.add(observedVariable.getVariable().getAttribute());

                    for(ObservedVariable observedVariable: pairClusterOVs)
                        pairClusterAttributes.add(observedVariable.getVariable().getAttribute());

                    // The edge weight between 2 LVs is the max bivariate score between their observed variables
                    double score = siblingClustersMeasure.getMaxBivariateScore(clusterAttributes,pairClusterAttributes);

                    completeGraph.addEdge(i,j,score);
                }
            }

        // Once the complete undirected graph has been generated, the MWST is calculated
        UndirectedGraph mwst = UndirectedGraph.obtainMaximumWeightSpanningTree(completeGraph);

        // After calculating the MWST, a random root is selected and the undirected tree is converted into a directed tree.
        //Random rn = new Random();
        //int rootIndex = rn.nextInt(mwst.getNVertices());
        DirectedTree rootedMWST = new DirectedTree(mwst, 0);

        // Finally, a flat-LTM is learnt in relation to the previously calculated MWST
        return ltmLearner.learnFlatLTM(rootedMWST,siblingClusters,batch);

    }

    /**
     * Refines the model
     * @param ltm the Latent Tree Model that is going to be refined
     * @return the refined Latent Tree Model
     */
    private LTM refineModel(LTM ltm){
        return ltm;
    }

}