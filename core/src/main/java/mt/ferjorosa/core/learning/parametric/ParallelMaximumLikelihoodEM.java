package mt.ferjorosa.core.learning.parametric;

import com.google.common.util.concurrent.AtomicDouble;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.exponentialfamily.EF_BayesianNetwork;
import eu.amidst.core.exponentialfamily.SufficientStatistics;
import eu.amidst.core.inference.InferenceAlgorithm;
import eu.amidst.core.learning.parametric.ParameterLearningAlgorithm;
import eu.amidst.core.models.BayesianNetwork;
import eu.amidst.core.models.DAG;

/*TODO: El método EM es muy dependiente de las instanciaciones iniciales. Estas se pueden realizar de forma manual o aleatoria
 por lo tanto es necesario*/
public class ParallelMaximumLikelihoodEM implements ParameterLearningAlgorithm {

    /** Represents the batch size used for learning the parameters. */
    protected int batchSize = 1000;

    /** Indicates the parallel processing mode, initialized here as {@code true}. */
    protected boolean parallelMode = true;

    /** Represents the {@link DataStream} used for learning the parameters. */
    protected DataStream<DataInstance> dataStream;

    /** Represents the directed acyclic graph {@link DAG}.*/
    protected DAG dag;

    /** Represents the data instance count. */
    protected AtomicDouble dataInstanceCount;

    /** Represents the sufficient statistics used for parameter learning. */
    protected SufficientStatistics sumSS;

    /** Represents a {@link EF_BayesianNetwork} object */
    protected EF_BayesianNetwork efBayesianNetwork;

    /** Represents if the class is in debug mode*/
    protected boolean debug = true;

    /** Represents whether Laplace correction (i.e. MAP estimation) is used*/
    protected boolean laplace = true;

    /** Number of iterations, with a default value */
    protected int numberOfIterations = 10;

    /** The threshold for stopping EM */
    protected double threshold = 0.001;

    /** The inference algorithm used in the expectation step */
    protected InferenceAlgorithm inferenceAlgorithm;

    /**
     * Sets whether Laplace correction (i.e. MAP estimation) is used
     * @param laplace, a boolean value.
     */
    public void setLaplace(boolean laplace) {
        this.laplace = laplace;
    }

    /**
     * Sets the debug mode of the class
     * @param debug a boolean setting whether to execute in debug mode or not.
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Sets the batch size.
     * @param batchSize_ the batch size.
     */
    public void setBatchSize(int batchSize_) {
        batchSize = batchSize_;
    }

    /**
     * Sets the number of iterations
     * @param numberOfIterations_ the number of iterations
     */
    public void setNumberOfIterations(int numberOfIterations_){
        numberOfIterations = numberOfIterations_;
    }

    /**
     * Sets the EM threshold
     * @param threshold_ the EM threshold
     */
    public void setThreshold(double threshold_){
        threshold = threshold_;
    }

    /**
     * Sets the inference algorithm that will be used in the expectation step
     * @param inferenceAlgorithm_ the inference algorithm
     */
    public void setInferenceAlgorithm(InferenceAlgorithm inferenceAlgorithm_){
        inferenceAlgorithm = inferenceAlgorithm_;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initLearning() {
        efBayesianNetwork = new EF_BayesianNetwork(dag);
        if (laplace) {
            sumSS = efBayesianNetwork.createInitSufficientStatistics();
            dataInstanceCount = new AtomicDouble(1.0); //Initial counts
        }else {
            sumSS = efBayesianNetwork.createZeroSufficientStatistics();
            dataInstanceCount = new AtomicDouble(0.0); //Initial counts
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double updateModel(DataOnMemory<DataInstance> batch) {
        //Por cada batch recibido se realiza una serie de iteraciones del algoritmo EM

        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDataStream(DataStream<DataInstance> data) { this.dataStream=data; }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getLogMarginalProbability() {
        throw new UnsupportedOperationException("Method not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runLearning() {
        throw new UnsupportedOperationException("Method not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDAG(DAG dag_) {
        dag = dag_;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSeed(int seed) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BayesianNetwork getLearntBayesianNetwork() {
        //Normalize the sufficient statistics
        SufficientStatistics normalizedSS = efBayesianNetwork.createZeroSufficientStatistics();
        normalizedSS.copy(sumSS);
        normalizedSS.divideBy(dataInstanceCount.get());

        efBayesianNetwork.setMomentParameters(normalizedSS);
        return efBayesianNetwork.toBayesianNetwork(dag);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setParallelMode(boolean parallelMode_) {
        parallelMode = parallelMode_;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOutput(boolean activateOutput) {

    }

    private void expectationStep(){

    }

    private void maximizationStep(){

    }
}
