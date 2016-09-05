package mt.ferjorosa.core.learning.structural;

/**
 * Configuration object for the Approximate Bridged Islands algorithm.
 */
public class ApproximateBIConfig {

    /** A restriction about the maximum number of observed variables in a single cluster, under a single latent variable. */
    private int maxIslandSize = 30;

    /** The base cardinality of every Latent variable created in the algorithm. */
    private int baseLvCardinality = 2;

    /** The percentage value used in the UD test. */
    private double udTestThreshold = 3.0;

    /**
     * Creates a default configuration for the Approximate Bridged Islands algorithm.
     */
    public ApproximateBIConfig(){
        this.maxIslandSize = 30;
        this.baseLvCardinality = 2;
        this.udTestThreshold = 3.0;
    }

    /**
     * Creates a specified configuration for the Approximate Bridged Islands algorithm.
     * @param maxIslandSize the
     * @param baseLvCardinality
     * @param udTestThreshold
     */
    public ApproximateBIConfig(int maxIslandSize, int baseLvCardinality, double udTestThreshold) {
        this.maxIslandSize = maxIslandSize;
        this.baseLvCardinality = baseLvCardinality;
        this.udTestThreshold = udTestThreshold;
    }

    /**
     * Returns the allowed maximum number of observed variables in a single cluster, under a single latent variable.
     * @return the allowed maximum number of observed variables in a single cluster, under a single latent variable.
     */
    public int getMaxIslandSize() {
        return maxIslandSize;
    }

    /**
     * Returns the base cardinality of every Latent variable created in the ABI algorithm
     * @return the base cardinality of every Latent variable created in the ABI algorithm
     */
    public int getBaseLvCardinality() {
        return baseLvCardinality;
    }

    /**
     * Returns the percentage value used in the UD test.
     * @return the percentage value used in the UD test.
     */
    public double getUdTestThreshold() {
        return udTestThreshold;
    }


}
