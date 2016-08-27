package mt.ferjorosa.core.util.exportBN.format;

import com.google.gson.Gson;
import eu.amidst.core.models.BayesianNetwork;
import mt.ferjorosa.core.util.exportBN.format.json.CytoscapeJsonFormatBN;

/**
 * Created by Fernando on 27/08/2016.
 */
public class ConvertBN {

    public String toCytoscapeJson(BayesianNetwork bayesianNetwork){
        Gson gson = new Gson();
        return gson.toJson(new CytoscapeJsonFormatBN(bayesianNetwork));
    }
}
