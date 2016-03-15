package mt.ferjorosa.examples.learning;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.variables.Variables;
import mt.ferjorosa.core.models.ltdag.LTVariables;

/**
 * Created by Fer on 15/03/2016.
 */
public class KnownLTMLearningExample {

    public static void main(String[] args) throws Exception {

        DataStream<DataInstance > data = DataStreamLoader.
                openFromFile("datasets/ferjorosaData/sprinklerDataHidden.arff");

        // Create the LTM structure (LTDAG) that is going to be learned

        Variables variables = new Variables(data.getAttributes());

        LTVariables ltVariables = new LTVariables(variables);

        ltVariables.newObservedVariable();

        // En este ejemplo vamos a utilizar la estructura de la red de sprinkler y vamos a convertir la
        // variable cloudy en latente y luego vamos a aprender los parametros. Puede que sea necesario implementar
        // una proyeccion del dataSet, aunque espero que no porque seria estupido y podria llevar un tiempo extra de
        // procesamiento.

        //

    }
}
