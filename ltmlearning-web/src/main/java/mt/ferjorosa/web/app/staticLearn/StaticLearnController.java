package mt.ferjorosa.web.app.staticLearn;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.learning.parametric.bayesian.SVB;
import mt.ferjorosa.core.learning.structural.ApproximateBIAlgorithm;
import mt.ferjorosa.core.learning.structural.ApproximateBIConfig;
import mt.ferjorosa.core.learning.structural.StructuralLearning;
import mt.ferjorosa.core.learning.structural.variables.FSSMeasureFactory;
import mt.ferjorosa.core.models.LTM;
import mt.ferjorosa.core.util.exportBN.format.ConvertBN;
import mt.ferjorosa.web.app.staticLearn.parameters.ABIParameters;
import mt.ferjorosa.web.app.localData.LocalDataService;
import spark.Request;
import spark.Response;
import spark.Route;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

/**
 * Created by fernando on 5/09/16.
 */
public class StaticLearnController {


    public static Route learnFlatLtmABI = (Request request, Response response) -> {
        try {
            String algorithmParameters = request.body();
            Gson gson = new Gson();
            ABIParameters params = gson.fromJson(algorithmParameters, ABIParameters.class);

            if (!params.isValid()) {
                response.status(HTTP_BAD_REQUEST);
                return "Invalid Json";
            }

            //Creamos la configuracion del ABI
            ApproximateBIConfig approximateBIConfig = new ApproximateBIConfig(params.getMaxIslandSize(), params.getBaseLvCardinality(), params.getUdTestThreshold());
            // Llamamos al metodo pasandole los parametros
            SVB parameterLearningAlgorithm = new SVB();
            StructuralLearning structuralLearningAlgorithm = new ApproximateBIAlgorithm(approximateBIConfig, parameterLearningAlgorithm, FSSMeasureFactory.retrieveInstance(params.getFssMeasure()));
            // Devolvemos una respuesta en formato JSON conteniendo la BN
            LTM learntModel = null;
            DataStream<DataInstance> data = LocalDataService.openDataFile(params.getDataFileName());
            for (DataOnMemory<DataInstance> batch : data.iterableOverBatches(params.getBatchSize())) {
                learntModel = structuralLearningAlgorithm.learnModel(batch);
            }
            //Return the BN in Json format
            return ConvertBN.toCytoscapeJson(learntModel.getLearntBayesianNetwork(), false);

        }catch(JsonParseException jpe){
            response.status(HTTP_BAD_REQUEST);
            return "Json Parse Exception";
        }catch(Exception e){
            response.status(HTTP_INTERNAL_ERROR);
            return "Server Error";
        }
    };
}
