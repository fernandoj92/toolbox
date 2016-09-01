package mt.ferjorosa.core.util.exportBN.format;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.amidst.core.models.BayesianNetwork;
import mt.ferjorosa.core.util.exportBN.format.json.CytoscapeJsonFormatBN;

/**
 * Created by Fernando on 27/08/2016.
 */
public class ConvertBN {

    public static String toCytoscapeJson(BayesianNetwork bayesianNetwork, boolean prettyPrinting){

        if(prettyPrinting){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(new CytoscapeJsonFormatBN(bayesianNetwork));
        }
        else {
            Gson gson = new Gson();
            return gson.toJson(new CytoscapeJsonFormatBN(bayesianNetwork));
        }
    }
}
