package mt.ferjorosa.core.learning.structural.variables;

import eu.amidst.core.datastream.Attribute;
import eu.amidst.core.datastream.Attributes;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.*;

/**
 * La forma optimizada tendría que mantener una estrucutra de datos alternativa que guardase todas las posibles combinaciones
 *
 * Usa el paso de valor a string porque es la unica manera de acceder al valor de un atributo de la instancia
 *
 * TODO: Revisar como seria el funcionamiento para un mismo objeto y varios SetData.
 */
public class MutualInformation implements FSSMeasure, Serializable{

    /** Represents the serial version ID for serializing the object. */
    private static final long serialVersionUID = 5461283324939870839L;

    /** Represents the data to be used for calculating the MI. */
    private DataOnMemory<DataInstance> data;

    /** Indicates if it should calculate all the attributes' probabilities, initialized to {@code true}. */
    private boolean allAttributes = true;

    /** */
    private Map<Pair<Attribute,Attribute>,Double> pairScores = new HashMap<>();

    public void setData(DataOnMemory<DataInstance> data){
        this.data = data;
    }

    /**
     * Computes the Bivariate Mutual Information of two attributes of the DataSet
     * @param attributeX the first attribute
     * @param attributeY the second attribute
     * @return the bivariate Mutual Information of the attributes
     */
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
        for(int i=0; i<attributeX.getNumberOfStates();i++){
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

                sum += Pxy * Math.log(Pxy/(Px*Py));
            }
        }
        // Calculation needed to make it a logarithm with base @logBase
        return sum/Math.log(logBase);
    }

    /**
     * Computes all the pair scores
     * @param attributes
     * @return
     */
    public void computeAllPairScores(List<Attribute> attributes){

        //
        double bivariateScore;

        // SIMÉTRICO: El score es identico para (i,j) que para (j,i)
        Set<Pair<Attribute, Attribute>> possiblePairs = new HashSet<>();

        for(Attribute attr : attributes)
            for(Attribute attr1 : attributes)
                if(attr1.getIndex() != attr.getIndex()){
                    Pair<Attribute,Attribute> pair = Pair.of(attr, attr1);
                    if(!possiblePairs.contains(pair)){
                        // 1 - Almacenar su valor
                        possiblePairs.add(pair);
                        bivariateScore = computeBivariateScore(attr,attr1);
                        // Almacenarlo con el Pair como Key o hacer dos HashMaps?
                        pairScores.put(pair,bivariateScore);
                    }
                }
    }

    /**
     *
     * @param attributes
     * @return
     */
    public Pair<Attribute, Attribute> getBestPair(List<Attribute> attributes){

        if(this.pairScores.isEmpty())
            throw new IllegalStateException("No Pair scores have been computed");

        //
        double bivariateScore;
        //
        double maxScore = Double.NEGATIVE_INFINITY;
        Attribute first = null;
        Attribute second = null;

        for(Attribute attr : attributes)
            for(Attribute attr1 : attributes)
                if(attr1.getIndex() != attr.getIndex()){
                    bivariateScore = pairScores.get(Pair.of(attr,attr1));
                    if(bivariateScore > maxScore){
                        first = attr;
                        second = attr1;
                    }
                }

        return Pair.of(first,second);
    }

    /**
     *
     * @param activeSet
     * @param outSet
     * @return
     */
    public Attribute getClosestAttributeToSet(List<Attribute> activeSet, List<Attribute> outSet){

        Attribute closestOutAttribute = null;
        double maxScore = Double.NEGATIVE_INFINITY;
        double bivariateScore = Double.NEGATIVE_INFINITY;

        for(Attribute outAttribute : outSet)
            for(Attribute activeAttribute : activeSet){
                Pair<Attribute, Attribute> currentPair =Pair.of(outAttribute,activeAttribute);
                bivariateScore = pairScores.get(currentPair);
                if(bivariateScore > maxScore){
                    maxScore = bivariateScore;
                    closestOutAttribute = outAttribute;
                }
            }
        return closestOutAttribute;
    }




}
