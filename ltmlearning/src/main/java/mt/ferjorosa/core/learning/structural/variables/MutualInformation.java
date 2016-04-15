package mt.ferjorosa.core.learning.structural.variables;

import eu.amidst.core.datastream.Attribute;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import mt.ferjorosa.core.util.pair.SymmetricPair;

import java.util.*;

/**
 * La forma optimizada tendría que mantener una estrucutra de datos alternativa que guardase todas las posibles combinaciones
 *
 * Usa el paso de valor a string porque es la unica manera de acceder al valor de un atributo de la instancia
 *
 * TODO: Revisar como seria el funcionamiento para un mismo objeto y varios SetData.
 * TODO: Es posible que sea necesario dividir esta clase en 2, hay parte del estado que no se utiliza siempre y es liante
 * por ejemplo si solo queremos computar bivariates
 */
public class MutualInformation implements FSSMeasure{

    /** The data used for calculating the MI. */
    private DataOnMemory<DataInstance> data;

    /**
     * The stored pair scores. It cannot contain scores between a pair of attributes not present in the data, but
     * it doesnt have to contain all the pair-scores between all the attributes presented in the data.
     */
    private Map<SymmetricPair<Attribute,Attribute>,Double> pairScores = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void setData(DataOnMemory<DataInstance> data){
        this.data = data;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double computeBivariateScore(Attribute attributeX, Attribute attributeY){
        int logBase = 2; // Bivariate Mutual Information

        // Setup
        Map<String,Integer> attrXCounts = new HashMap<>();
        Map<String,Integer> attrYCounts = new HashMap<>();
        Map<String,Integer> crossCounts = new HashMap<>();

        // Attribute1 counts initialization
        for(int i=0; i<attributeX.getNumberOfStates();i++){
            attrXCounts.put(i + "",0);
        }
        // Attribute2 counts initialization
        for(int i=0; i<attributeY.getNumberOfStates();i++){
            attrYCounts.put(i + "",0);
        }
        // Cross-attribute counts initialization
        for(int i = 0; i<attributeX.getNumberOfStates();i++) {
            String sX = i + "";
            for (int j = 0; j < attributeY.getNumberOfStates(); j++) {
                String sY = j + "";
                crossCounts.put(sX + ";" + sY, 0);
            }
        }

        // State counts
        for(DataInstance instance: data){
            String valueXAsString = (int) instance.getValue(attributeX) + "";
            String valueYAsString = (int) instance.getValue(attributeY) + "";

            attrXCounts.put(valueXAsString,attrXCounts.get(valueXAsString) + 1);
            attrYCounts.put(valueYAsString,attrYCounts.get(valueYAsString) + 1);
            crossCounts.put(valueXAsString+";"+valueYAsString,crossCounts.get(valueXAsString+";"+valueYAsString) + 1);
        }

        double sum = 0;
        int Ninstances = data.getNumberOfDataInstances();

        // Mutual Information formula
        for(int i = 0; i<attributeX.getNumberOfStates();i++) {
            String sX = i + "";
            for (int j = 0; j < attributeY.getNumberOfStates(); j++) {
                String sY = j + "";

                double Nx = attrXCounts.get(sX);
                double Ny = attrYCounts.get(sY);
                double Nxy = crossCounts.get(sX + ";" + sY);

                double Px = Nx /Ninstances;
                double Py = Ny/Ninstances;
                double Pxy = Nxy/Ninstances;

                /**
                 * log(0) = -Infinity
                 *  To deal with that, in information theory there is a convention stating that log(0) = 0
                 */
                if(Pxy == 0 || Px == 0 || Py == 0)
                    sum += 0;
                else
                    sum += Pxy * Math.log(Pxy/(Px*Py));
            }
        }
        // Calculation needed to make it a logarithm with base @logBase
        return sum/Math.log(logBase);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void computeAllPairScores(List<Attribute> attributes){

        double bivariateScore;

        // Symmetric: The score is the same for (i,j) that for (j,i)
        Set<SymmetricPair<Attribute, Attribute>> possiblePairs = new HashSet<>();

        for(Attribute attr : attributes)
            for(Attribute attr1 : attributes)
                if(attr1.getIndex() != attr.getIndex()){
                    SymmetricPair<Attribute,Attribute> pair = new SymmetricPair<>(attr, attr1);
                    if(!possiblePairs.contains(pair)){
                        // Store its value
                        possiblePairs.add(pair);
                        bivariateScore = computeBivariateScore(attr,attr1);
                        if(!pairScores.containsKey(pair))
                            pairScores.put(pair,bivariateScore);
                    }
                }
    }

    /**
     * Returns all the currently calculated Pair Scores.
     * @return all the currently calculated Pair Scores.
     */
    public Map<SymmetricPair<Attribute,Attribute>,Double> getAllPairScores(){
        return this.pairScores;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SymmetricPair<Attribute, Attribute> getBestPair(List<Attribute> attributes){

        if(this.pairScores.isEmpty())
            throw new IllegalStateException("No Pair scores have been computed");

        // Checks that the user is not asking for attributes that aren't present in the data
        long attributesNotPresent = checkAbsentAttributes(attributes);
        if(attributesNotPresent > 0)
            throw new IllegalArgumentException("Some attributes aren't presented in the data used for calculating the MI");

        double bivariateScore;
        double maxScore = Double.NEGATIVE_INFINITY;
        Attribute first = null;
        Attribute second = null;

        for(Attribute attr : attributes)
            for(Attribute attr1 : attributes)
                if(attr1.getIndex() != attr.getIndex()){
                    bivariateScore = pairScores.get(new SymmetricPair<>(attr,attr1));
                    if(bivariateScore > maxScore){
                        first = attr;
                        second = attr1;
                    }
                }

        return new SymmetricPair<>(first,second);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Attribute getClosestAttributeToSet(List<Attribute> activeSet, List<Attribute> outSet){

        if(this.pairScores.isEmpty())
            throw new IllegalStateException("No Pair scores have been computed");

        // Checks that the user is not asking for attributes that aren't present in the data
        long activeSetAbsentAttributes = checkAbsentAttributes(activeSet);
        long outSetAbsentAttributes = checkAbsentAttributes(outSet);
        if(activeSetAbsentAttributes > 0 || outSetAbsentAttributes > 0)
            throw new IllegalArgumentException("Some attributes aren't presented in the data used for calculating the MI");

        Attribute closestOutAttribute = null;
        double maxScore = Double.NEGATIVE_INFINITY;
        double bivariateScore = Double.NEGATIVE_INFINITY;

        for(Attribute outAttribute : outSet)
            for(Attribute activeAttribute : activeSet){
                SymmetricPair<Attribute, Attribute> currentPair = new SymmetricPair<>(outAttribute,activeAttribute);
                bivariateScore = pairScores.get(currentPair);
                if(bivariateScore > maxScore){
                    maxScore = bivariateScore;
                    closestOutAttribute = outAttribute;
                }
            }
        return closestOutAttribute;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMaxBivariateScore(List<Attribute> firstSet, List<Attribute> secondSet){

        if(this.pairScores.isEmpty())
            throw new IllegalStateException("No Pair scores have been computed");

        // Checks that the user is not asking for attributes that aren't present in the data
        long firstSetAbsentAttributes = checkAbsentAttributes(firstSet);
        long secondSetAbsentAttributes = checkAbsentAttributes(secondSet);
        if(firstSetAbsentAttributes > 0 || secondSetAbsentAttributes > 0)
            throw new IllegalArgumentException("Some attributes aren't presented in the data used for calculating the MI");

        double maxScore = Double.NEGATIVE_INFINITY;
        double bivariateScore = Double.NEGATIVE_INFINITY;

        for(Attribute firstAttribute : firstSet)
            for(Attribute secondAttribute : secondSet){
                SymmetricPair<Attribute, Attribute> currentPair = new SymmetricPair<>(firstAttribute,secondAttribute);
                bivariateScore = pairScores.get(currentPair);
                if(bivariateScore > maxScore){
                    maxScore = bivariateScore;
                }
            }

        return maxScore;
    }

    /**
     * Checks that the user is not asking for attributes that are absent in the data.
     * @param attributes the attributes being passed by the user.
     * @return the number of absent attributes.
     */
    private long checkAbsentAttributes(List<Attribute> attributes){

        long absentAttributes = attributes.stream()
                .map(attribute -> data.getAttributes().getFullListOfAttributes().contains(attribute))
                .filter(res -> res == false)
                .count();

        return absentAttributes;
    }
}
