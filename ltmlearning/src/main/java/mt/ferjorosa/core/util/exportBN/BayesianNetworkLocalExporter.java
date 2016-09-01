package mt.ferjorosa.core.util.exportBN;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Fernando on 07/08/2016.
 */
public class BayesianNetworkLocalExporter {

    public static void writeJsonFile(String bayesianNetworkJson, String path){
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(bayesianNetworkJson);
        }catch (IOException ioException){
            ioException.printStackTrace();
        }
    }
}
