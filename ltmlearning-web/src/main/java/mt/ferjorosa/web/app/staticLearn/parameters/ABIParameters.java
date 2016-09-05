package mt.ferjorosa.web.app.staticLearn.parameters;

import mt.ferjorosa.core.learning.structural.variables.FSSMeasures;

/**
 * Created by fernando on 5/09/16.
 */
public class ABIParameters {

    private String fssMeasure;

    private Integer maxIslandSize;

    private Integer baseLvCardinality;

    private Integer udTestThreshold;

    private Integer batchSize;

    private String dataFileName;

    public String getFssMeasure() {
        return fssMeasure;
    }

    public Integer getMaxIslandSize() {
        return maxIslandSize;
    }

    public Integer getBaseLvCardinality() {
        return baseLvCardinality;
    }

    public Integer getUdTestThreshold() {
        return udTestThreshold;
    }

    public Integer getBatchSize(){
        return batchSize;
    }

    public String getDataFileName(){
        return dataFileName;
    }

    public boolean isValid(){
        return validate(fssMeasure) &&
                maxIslandSize != null &&
                baseLvCardinality != null &&
                udTestThreshold != null &&
                batchSize != null &&
                batchSize != 0 &&
                dataFileName != null &&
                !dataFileName.isEmpty();
    }

    private boolean validate(String fssMeasure){
        return fssMeasure != null &&
                !fssMeasure.isEmpty() &&
                (fssMeasure.equals(FSSMeasures.MUTUAL_INFORMATION.toString()));
    }

}
