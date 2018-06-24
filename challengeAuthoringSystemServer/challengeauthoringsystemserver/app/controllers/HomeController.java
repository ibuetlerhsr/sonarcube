package controllers;

import play.libs.ws.WSClient;
import play.mvc.*;

import javax.inject.Inject;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    private final WSClient wsChallengeClient;
    @Inject
    public HomeController(WSClient ws){
        wsChallengeClient = ws;
    }
    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        return ok("Dies ist eine Testseite");
    }


}
