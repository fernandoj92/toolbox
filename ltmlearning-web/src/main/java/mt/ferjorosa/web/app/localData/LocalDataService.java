package mt.ferjorosa.web.app.localData;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.DataStreamLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fernando on 5/09/16.
 */
public class LocalDataService {

    private static String baseDataPath = "ltmlearning-web/data/";

    public static DataStream<DataInstance> openDataFile(String fileName){
        return DataStreamLoader.openFromFile(baseDataPath+fileName);
    }

    static List<String> listDataFiles(){
        File folder = new File(baseDataPath);
        File[] listOfFiles = folder.listFiles();

        if(listOfFiles == null)
            return new ArrayList<>();

        return Arrays.stream(listOfFiles).map(file -> file.getName()).collect(Collectors.toList());
    }
}
