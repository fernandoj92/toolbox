package mt.ferjorosa.web.app.localData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;

/**
 * Created by fernando on 5/09/16.
 */
public class LocalDataController {

    public static Route listLocalFiles = (Request request, Response response) -> {
        List<String> localFiles = LocalDataService.listDataFiles();
        Gson gson = new GsonBuilder().create();
        return gson.toJson(localFiles);
    };
}
