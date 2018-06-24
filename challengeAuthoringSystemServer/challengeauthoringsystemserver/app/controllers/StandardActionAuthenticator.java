package controllers;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import play.libs.ws.WSClient;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import services.UserService;
import utils.ControllerUtil;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Optional;

public class StandardActionAuthenticator extends Security.Authenticator {
    private Config configuration;

    @Inject
    public StandardActionAuthenticator(WSClient wsChallengeClient, UserService userService) {
        configuration = ConfigFactory.load();
    }

    @Override
    public String getUsername(Http.Context context){
        String roles = getRolesFromHeader(context);
        ArrayList<String> rolesList = ControllerUtil.splitSeparatedString(configuration.getString("authorized.defaultRoles"));
        if(rolesList != null) {
            for(String role : rolesList) {
                if(roles != null && roles.contains(role)){
                    return getUsernameFromHeader(context);
                }
            }
        }
        rolesList = ControllerUtil.splitSeparatedString(configuration.getString("authorized.adminRoles"));
        if(rolesList == null)
            return null;
        for(String role : rolesList) {
            if(roles != null && roles.contains(role)){
                return getUsernameFromHeader(context);
            }
        }
        return null;
    }

    @Override
    public Result onUnauthorized(Http.Context context) {
        return super.onUnauthorized(context);
    }


    private String getRolesFromHeader(Http.Context context) {
        Optional<String> rolesHeaderValue = context.request().getHeaders().get("X-Auth-Roles");
        return rolesHeaderValue.orElse(null);
    }

    private String getUsernameFromHeader(Http.Context context) {
        Optional<String> usernameHeaderValue = context.request().getHeaders().get("X-Auth-Username");
        return usernameHeaderValue.orElse(null);
    }
}
