package mt.ferjorosa.core.learning;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.learning.parametric.ParameterLearningAlgorithm;
import mt.ferjorosa.core.learning.conceptdrift.ConceptDriftStates;
import mt.ferjorosa.core.learning.conceptdrift.ConceptDriftMeasure;
import mt.ferjorosa.core.learning.structural.StructuralLearning;
import mt.ferjorosa.core.models.LTM;

import java.util.ArrayList;

/**
 * This class allows to learn a changing Latent Tree Model from a streaming of data batches. It requires a structural
 * learning algorithm that will learn the LTM structure and parameters once the batch arrives, updating the currently
 * stored model. However, before starting to receive streaming data it is necessary to learn an initial model.
 *
 * When a batch of data arrives, it will be first tested with current model to see if exists a concept drift. There are
 * 3 possible cases:
 * - Concept drift: Gradual changes in the model. It requires to learn the parameters of the model.
 * - Concept shift: Abrupt changes in the model. It requires to learn a brand new model.
 * - Not enough change: It keeps the current model.
 *
 * TODO: Revisar el nombre del interfaz de Fading learner y tmb el nombre del atributo de dicho tipo de esta clase.
 * TODO: No me gusta el hecho de tener el engine de aprender los LTM combinado con el ABI
 * TODO: Quizas seria posible evaluar que partes del model son las que peor resultados da y actualizar solo una parte del modelo.
 * TODO: Es necesario un método para parar el streaming (tras X iteraciones o "manualmente" cambiando la constante streamIsRunning)
 *       para ello seguramente haya que hacer uso de varios hilos (Peligro)
 */
public class StreamingLTMLearningEngine {

    /** The measure that is going to be used to determine if a concept drift has occurred. */
    private ConceptDriftMeasure conceptDriftMeasure;

    /** The data stream used to update the model. */
    private DataStream<DataInstance> dataStream;

    /** The structural learning algorithm used to learn the model when a Concept Shift occurs. */
    private StructuralLearning structuralLearningAlgorithm;

    /** The parameter learning algorithm used to update the model when a new batch of data arrives. */
    private ParameterLearningAlgorithm parameterLearningAlgorithm;

    /** Currently stored Latent Tree Model. */
    private LTM currentModel;

    /** Size of the batch used in the streaming of data. */
    private int batchSize = 1000;

    private double lastBatchScore = 0;

    /** */
    private boolean streamIsRunning;

    /**
     * Creates a Streaming LTM learning engine by passing the  measure to be used to distinguish  between the possible
     * {@link ConceptDriftStates} that have happened and the structural learning algorithm that is going to be used when
     * a concept drift has occurred.
     * @param conceptDriftMeasure the measure being used to distinguish (i.e the likelihood of the data with current model).
     * @param structuralLearningAlgorithm the algorithm used to fully learn a new model.
     */
    public StreamingLTMLearningEngine(ConceptDriftMeasure conceptDriftMeasure,
                                      StructuralLearning structuralLearningAlgorithm){
        this.conceptDriftMeasure = conceptDriftMeasure;
        this.structuralLearningAlgorithm = structuralLearningAlgorithm;
    }

    /**
     * Sets the data streams used to learn the model.
     * @param dataStream the {@link DataStream} object used.
     */
    public void setDataStream(DataStream<DataInstance> dataStream) {
        this.dataStream = dataStream;
    }

    /**
     * Sets the size of the batch used in the stream.
     * @param batchSize the size of the batch used in the stream.
     */
    public void setBatchSize(int batchSize){
        this.batchSize = batchSize;
    }

    public LTM getCurrentModel(){
        return this.currentModel;
    }

    public double getLastBatchScore(){
        return this.lastBatchScore;
    }

    /**
     * First method to be called before starting the data stream. By passing a batch of data instances it learns an initial
     * model that will be then compared to see if exists a Concept drift. It could be considered as the first stream's
     * batch of data.
     * @param batch the first batch used to fully learn the model.
     */
    public void initLearning(DataOnMemory<DataInstance> batch){
        this.currentModel = structuralLearningAlgorithm.learnModel(batch);
    }

    /**
     * This method starts running the streaming and updating the model. With each batch of data instances determines if a
     * concept drift has occurred by comparing the obtained measure's score with an established factor.
     */
    public void runLearning() {

        if(this.currentModel == null)
            throw new IllegalStateException("It is necessary to call initLearning() before starting to stream data");

        this.streamIsRunning = true;

        int currentIteration = 0;
        int iterationLastDrift = 0;
        //ArrayList<Double> differences = new ArrayList<>();

        for (DataOnMemory<DataInstance> batch : dataStream.iterableOverBatches(batchSize)){

            currentIteration++;

            //Updates current model with the new data
            double batchScore = currentModel.updateModel(batch);

            /**
             * Checks if the new data isn't properly represented by current structure.
             *
             * Learns 2 models and compares them. First updates current model's parameters with the new batch of data and then
             * learns a new model with the same structure. This second model will not take into consideration previous data
             * as the first one does.
             */
            ConceptDriftStates state = conceptDriftMeasure.checkConceptDriftPH(currentModel, batchScore, batch, currentIteration, iterationLastDrift);

            // Different ways of action depending on the state
            switch (state){

                // If there is a concept shift, then it learns a new model using the new batch as seed.
                case CONCEPT_DRIFT:
                    this.currentModel = structuralLearningAlgorithm.learnModel(batch);
                    iterationLastDrift = currentIteration;
                    conceptDriftMeasure.reset();
                    break;

                // Nothing needs to be changed
                default : break;
            }
        }
    }

    private int currentIteration = 0;
    private int iterationLastDrift = 0;

    public void updateModel(DataOnMemory<DataInstance> batch){

        this.currentIteration++;

        if(this.currentModel == null)
            throw new IllegalStateException("It is necessary to call initLearning() before starting to stream data");

        //Updates current model with the new data
        lastBatchScore = currentModel.updateModel(batch);

        /**
         * Checks if the new data isn't properly represented by current structure.
         *
         * Learns 2 models and compares them. First updates current model's parameters with the new batch of data and then
         * learns a new model with the same structure. This second model will not take into consideration previous data
         * as the first one does.
         */

        ConceptDriftStates state = conceptDriftMeasure.checkConceptDriftPH(currentModel, lastBatchScore, batch, currentIteration, iterationLastDrift);

        // Different ways of action depending on the state
        switch (state){

            // If there is a concept shift, then it learns a new model using the new batch as seed.
            case CONCEPT_DRIFT:
                this.currentModel = structuralLearningAlgorithm.learnModel(batch);
                iterationLastDrift = currentIteration;
                conceptDriftMeasure.reset();
                break;

            // Nothing needs to be changed
            default : break;
        }
    }
}


