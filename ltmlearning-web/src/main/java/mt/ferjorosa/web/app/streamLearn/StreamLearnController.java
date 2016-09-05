package mt.ferjorosa.web.app.streamLearn;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Simple controller without dependency injection. This controller will pass the webSocket connections to its handler.
 */
public final class StreamLearnController {

    public static Route test = (Request request, Response response) -> {
        return "Yolo";
    };
}
