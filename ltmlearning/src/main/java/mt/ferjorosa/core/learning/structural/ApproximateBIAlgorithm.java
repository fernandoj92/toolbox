package mt.ferjorosa.core.learning.structural;

import eu.amidst.core.datastream.Attribute;
import eu.amidst.core.datastream.Attributes;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.learning.parametric.ParameterLearningAlgorithm;
import eu.amidst.core.learning.parametric.bayesian.SVB;
import mt.ferjorosa.core.learning.structural.variables.FSSMeasure;
import mt.ferjorosa.core.learning.structural.variables.MutualInformation;
import mt.ferjorosa.core.models.LatentTreeModel;
import org.apache.commons.lang3.tuple.Pair;


import java.util.*;

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
 */
public class ApproximateBIAlgorithm implements LTMLearning {

    /** Valor asignado segun el código original de Zhang */
    private FSSMeasure siblingClustersMeasure;

    /** Valor asignado segun el código original de Zhang */
    private Attributes initialAttributes;

    /** Valor asignado segun el código original de Zhang */
    private List<Attribute> outSetAttributes;

    /** Valor asignado segun el código original de Zhang */
    private ParameterLearningAlgorithm parameterLearning;

    /** Valor asignado segun el código original de Zhang */
    private LatentTreeModel ltmLearned;

    /** Valor asignado segun el código original de Zhang */
    private int maxIslandSize = 30;

    /**
     *
     * @return
     */
    public LatentTreeModel getLatentTreeModel(){
        return this.ltmLearned;
    }

    public ApproximateBIAlgorithm(Attributes attributes){
        this.initialAttributes = attributes;
        this.parameterLearning = new SVB();
        this.siblingClustersMeasure = new MutualInformation();
    }

    public ApproximateBIAlgorithm(Attributes attributes, ParameterLearningAlgorithm parameterLearningAlgorithm){
        this.initialAttributes = attributes;
        this.parameterLearning = parameterLearningAlgorithm;
        this.siblingClustersMeasure = new MutualInformation();
    }

    public ApproximateBIAlgorithm(Attributes attributes, ParameterLearningAlgorithm parameterLearningAlgorithm, FSSMeasure siblingClustersMeasure){
        this.initialAttributes = attributes;
        this.parameterLearning = parameterLearningAlgorithm;
        this.siblingClustersMeasure = siblingClustersMeasure;
    }

    /**
     * @param batch a {@link DataOnMemory} object.
     * @return
     */
    // Creo que deberia devolver un score indicando como de bien representa el LTM actual el batch de datos
    // con el cual se ha aprendido (e.g elBO)
    @Override
    public double updateModel(DataOnMemory<DataInstance> batch) {
        if(batch.getAttributes() != this.initialAttributes)
            throw new IllegalArgumentException("Batch attributes don't correspond with initial attributes");

        siblingClustersMeasure.setData(batch);

        // 1 - Calculate sibling clusters (islands)
        calculateSiblingClusters(batch);
        // 2 - Generate LCMs
        generateLCMs();
        // 3 - Learn the LCM parameters and determine their LV's cardinality
        learnLCMs();
        // 4 - Generate the LTM
        generateLTM();

        return 0;
    }

    private void calculateSiblingClusters(DataOnMemory<DataInstance> batch){

        outSetAttributes = batch.getAttributes().getFullListOfAttributes();

        // Calculamos la información mútua entre todos los atributos
        siblingClustersMeasure.computeAllPairScores(outSetAttributes);

        while(outSetAttributes.size() > 0){
            singleIslandLearner();
        }
    }

    private void singleIslandLearner(){
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

        //Tras haber escogido el primer par de atributos que van a formar parte de la isla, iteramos
        // y vamos añadiendo nuevos atributos hasta que se deje de cumplir alguna de las siguientes condiciones
        while(!outSetAttributes.isEmpty() && UnidimensionalityTestCondition && activeSet.size() <= maxIslandSize) {

            Attribute closestAttribute = siblingClustersMeasure.getClosestAttributeToSet(activeSet,outSetAttributes);

            activeSet.add(closestAttribute);

            if(activeSet.size() >= 4 || outSetAttributes.size() < 4){

            }
        }
    }

    private void generateLCMs(){

    }

    private void learnLCMs(){

    }

    private void generateLTM(){

    }

}
