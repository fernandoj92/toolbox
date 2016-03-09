package mt.ferjorosa.core.learning.structural.variables;

import eu.amidst.core.datastream.Attribute;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * La forma optimizada tendría que mantener una estrucutra de datos alternativa que guardase todas las posibles combinaciones
 *
 * Usa el paso de valor a string porque es la unica manera de acceder al valor de un atributo de la instancia
 */
public class MutualInformation implements FSSMeasure, Serializable{

    /** Represents the serial version ID for serializing the object. */
    private static final long serialVersionUID = 5461283324939870839L;

    /** Represents the data to be used for calculating the MI. */
    private DataOnMemory<DataInstance> data;

    /** Indicates if it should calculate all the attributes' probabilities, initialized to {@code true}. */
    private boolean allAttributes = true;

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

}
