package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import models.DataTransferObject.ConsumerDto;
import models.DatabaseObject.user.MandantCCS;
import models.DatabaseObject.user.Role;
import models.DatabaseObject.user.UserCAS;
import play.Logger;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import services.UserService;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ImportActionAuthenticator extends Security.Authenticator {

    private final WSClient wsChallengeClient;
    private Config configuration;

    @Inject
    public ImportActionAuthenticator(WSClient wsChallengeClient, UserService userService) {
        configuration = ConfigFactory.load();
        this.wsChallengeClient = wsChallengeClient;
    }

    private String getTokenFromHeader(Http.Context context) {
        Optional<String> authTokenHeaderValue = context.request().getHeaders().get(SecurityController.AUTH_KEY_HEADER);
        return authTokenHeaderValue.orElse(null);
    }

    @Override
    public String getUsername(Http.Context context){
        String apiKey = configuration.getString("challengeImport.apiKey");
        String token = getTokenFromHeader(context);
        if(token == null || token.isEmpty())
            return null;
        if(token.equals(apiKey)) {
            return "access granted";
        }
        return null;
    }

    @Override
    public Result onUnauthorized(Http.Context context) {
        return super.onUnauthorized(context);
    }
}
