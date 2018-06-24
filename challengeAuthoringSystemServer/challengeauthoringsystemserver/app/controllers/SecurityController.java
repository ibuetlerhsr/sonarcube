package controllers;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

public class SecurityController extends Controller {

    private final WSClient wsChallengeClient;
    private Config configuration;
    final static String AUTH_TOKEN_HEADER = "Authorization";
    final static String AUTH_KEY_HEADER = "auth_key";
    final static String AUTH_TOKEN = "authToken";

    @Inject
    public SecurityController(WSClient ws) {
        configuration = ConfigFactory.load();
        wsChallengeClient = ws;
    }

    public Result options(){
        return ok();
    }
}
