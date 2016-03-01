package mt.ferjorosa.core.ltm;

import com.sun.javaws.exceptions.InvalidArgumentException;
import eu.amidst.core.distribution.ConditionalDistribution;
import eu.amidst.core.distribution.Distribution;
import eu.amidst.core.models.DAG;
import eu.amidst.core.models.ParentSet;
import eu.amidst.core.utils.Utils;
import eu.amidst.core.variables.Assignment;
import eu.amidst.core.variables.Variable;
import eu.amidst.core.variables.Variables;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Reimplemento todo desde 0 ya que la clase mas "cercana" seria BayesianNetwork pero es "final" y seguramente habria que
 * realizar una refactorización del código para que puediera extenderse correctamente, hay muchos métodos que seria posible
 * compartir.
 *
 * Actualmente solo se pueden crear LCMs con variables discretas ya que tenemos que calcular la similitud de las variables
 * mediante la información mútua
 */
public final class DiscreteLatentClusterModel implements Serializable{

    /** Represents the serial version ID for serializing the object. */
    private static final long serialVersionUID = 9107523324901381856L;

    /** Represents the list of conditional probability distributions defining the LCM parameters. */
    private List<ConditionalDistribution> distributions;

    /** Represents the Directed Acyclic Graph ({@link DAG}) defining the LCM graphical structure. */
    private DAG dag;

    public DiscreteLatentClusterModel(DAG dag) throws IllegalArgumentException{
        // Check if the DAG structure follows the LCM structure
        if(isLCM(dag)) {
            this.dag = dag;
            initializeDistributions();
        }else
            throw new IllegalArgumentException("DAG must follow the LCM structure");
    }

    public DiscreteLatentClusterModel(DAG dag, Random randomInitialization) throws IllegalArgumentException{
        // Check if the DAG structure follows the LCM structure
        if(isLCM(dag)) {
            this.dag = dag;
            initializeDistributions();
            randomInitialization(randomInitialization);
        }else
            throw new IllegalArgumentException("DAG must follow the LCM structure");
    }

    /**
     * Returns the name of the LCM
     * @return a String object
     */
    public String getName() {
        return this.dag.getName();
    }

    /**
     * Sets the name of the LCM
     * @param name, a String object
     */
    public void setName(String name) {
        this.dag.setName(name);
    }

    /**
     * Returns the conditional probability distribution of a variable.
     * @param <E> a class extending {@link ConditionalDistribution}.
     * @param var a variable of type {@link Variable}.
     * @return a conditional probability distribution.
     */
    public <E extends ConditionalDistribution> E getConditionalDistribution(Variable var) {
        return (E) distributions.get(var.getVarID());
    }

    /**
     * Sets the conditional probability distribution of a variable.
     * @param var a variable of type {@link Variable}.
     * @param dist Conditional probability distribution of type {@link ConditionalDistribution}.
     */
    public void setConditionalDistribution(Variable var, ConditionalDistribution dist){
        this.distributions.set(var.getVarID(),dist);
    }

    /**
     * Returns the total number of variables in this BayesianNetwork.
     * @return the number of variables.
     */
    public int getNumberOfVars() {
        return this.getDAG().getVariables().getNumberOfVars();
    }

    /**
     * Returns the set of variables in this BayesianNetwork.
     * @return set of variables of type {@link Variables}.
     */
    public Variables getVariables() {
        return this.getDAG().getVariables();
    }

    /**
     * Returns the directed acyclic graph of this BayesianNetwork.
     * @return a directed acyclic graph of type {@link DAG}.
     */
    public DAG getDAG() {
        return dag;
    }

    /**
     * Returns the parameter values of this BayesianNetwork.
     * @return an array containing the parameter values of all distributions.
     */
    public double[] getParameters(){

        int size = this.distributions.stream().mapToInt(dist -> dist.getNumberOfParameters()).sum();

        double[] param = new double[size];

        int count = 0;

        for (Distribution dist : this.distributions){
            System.arraycopy(dist.getParameters(), 0, param, count, dist.getNumberOfParameters());
            count+=dist.getNumberOfParameters();
        }

        return param;
    }

    /**
     * Initializes the distributions of this BayesianNetwork.
     * The initialization is performed for each variable depending on its distribution type.
     * as well as the distribution type of its parent set (if that variable has parents).
     */
    private void initializeDistributions() {

        this.distributions = new ArrayList(this.getNumberOfVars());

        for (Variable var : getVariables()) {
            ParentSet parentSet = this.getDAG().getParentSet(var);
            int varID = var.getVarID();
            this.distributions.add(varID, var.newConditionalDistribution(parentSet.getParents()));
            parentSet.blockParents();
        }

        //this.distributions = Collections.unmodifiableList(this.distributions);
    }

    /**
     *
     */
    private boolean isLCM(DAG dag){
        //Solo puede existir una variable oculta y dicha variable debe de ser el padre de todas las demás
        return true;
    }


    /**
     * Returns the log probability of a valid assignment.
     * @param assignment an object of type {@link Assignment}.
     * @return the log probability of an assignment.
     */
    public double getLogProbabilityOf(Assignment assignment) {
        double logProb = 0;
        for (Variable var : this.getVariables()) {
            if (assignment.getValue(var) == Utils.missingValue()) {
                throw new UnsupportedOperationException("This method can not compute the probabilty of a partial assignment.");
            }

            logProb += this.distributions.get(var.getVarID()).getLogConditionalProbability(assignment);
        }
        return logProb;
    }

    /**
     * Returns the list of the conditional probability distributions of this BayesianNetwork.
     * @return a list of {@link ConditionalDistribution}.
     */
    public List<ConditionalDistribution> getConditionalDistributions() {
        return this.distributions;
    }

    /**
     * Returns a textual representation of this BayesianNetwork.
     * @return a String description of this BayesianNetwork.
     */
    public String toString() {

        StringBuilder str = new StringBuilder();
        str.append("Latent Cluster Model:\n");

        for (Variable var : this.getVariables()) {

            if (this.getDAG().getParentSet(var).getNumberOfParents() == 0) {
                str.append("P(" + var.getName() + ") follows a ");
                str.append(this.getConditionalDistribution(var).label() + "\n");
            } else {
                str.append("P(" + var.getName() + " | ");

                for (Variable parent : this.getDAG().getParentSet(var)) {
                    str.append(parent.getName() + ", ");
                }
                str.delete(str.length()-2,str.length());
                if (this.getDAG().getParentSet(var).getNumberOfParents() > 0) {
                    str.substring(0, str.length() - 2);
                    str.append(") follows a ");
                    str.append(this.getConditionalDistribution(var).label() + "\n");
                }
            }
            //Variable distribution
            str.append(this.getConditionalDistribution(var).toString() + "\n");
        }
        return str.toString();
    }

    /**
     * Initializes the distributions of this BayesianNetwork randomly.
     * @param random an object of type {@link java.util.Random}.
     */
    public void randomInitialization(Random random) {
        //this.distributions = new ArrayList(this.getNumberOfVars());

        this.distributions.stream().forEach(w -> w.randomInitialization(random));


    }

    /**
     * Tests if two Bayesian networks are equals.
     * A two Bayesian networks are considered equals if they have an equal conditional distribution for each variable.
     * @param lcm a given Latent Cluster Model to be compared with this Latent Cluster Model.
     * @param threshold a threshold value.
     * @return a boolean indicating if the two BNs are equals or not.
     */
    public boolean equalLCMs(DiscreteLatentClusterModel lcm, double threshold) {
        boolean equals = true;
        if (this.getDAG().equals(lcm.getDAG())){
            for (Variable var : this.getVariables()) {
                equals = equals && this.getConditionalDistribution(var).equalDist(lcm.getConditionalDistribution(var), threshold);
            }
        }
        return equals;
    }

    /**
     * Returns this class name.
     * @return a String representing this class name.
     */
    public static String listOptions() {
        return  classNameID();
    }

    public static String listOptionsRecursively() {
        return listOptions()
                + "\n" +  "test";
    }

    public static String classNameID() {
        return "BayesianNetwork";
    }

    public static void loadOptions() {

    }

}
