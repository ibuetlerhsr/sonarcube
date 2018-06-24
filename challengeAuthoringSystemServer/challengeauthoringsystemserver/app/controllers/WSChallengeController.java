package controllers;

import akka.NotUsed;
import actors.ChallengeParentActor;
import akka.actor.ActorRef;
import akka.stream.javadsl.Flow;
import akka.util.Timeout;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import play.Logger;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.WebSocket;
import scala.concurrent.duration.Duration;
import utils.ControllerUtil;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import static akka.pattern.PatternsCS.ask;

public class WSChallengeController {

    private Config configuration;
    private final Timeout t = new Timeout(Duration.create(1, TimeUnit.SECONDS));
    private final ActorRef userParentActor;

    @Inject
    public WSChallengeController(@Named("challengeParentActor") ActorRef userParentActor) {
        configuration = ConfigFactory.load();
        this.userParentActor = userParentActor;
    }

    public WebSocket ws(String queryString) {
        return WebSocket.Text.acceptOrResult(request -> {
            if (originCheck(request)) {
                final CompletionStage<Flow<String, String, NotUsed>> future = wsFutureFlow(request, queryString);
                final CompletionStage<F.Either<Result, Flow<String, String, ?>>> stage = future.thenApply(F.Either::Right);
                return stage.exceptionally(this::logException);
            } else {
                return forbiddenResult();
            }

        });
    }

    @SuppressWarnings("unchecked")
    private CompletionStage<Flow<String, String, NotUsed>> wsFutureFlow(Http.RequestHeader request, String queryString) {
        long id = request.asScala().id();
        Logger.info("WSFutureFlow:" + id);
        Logger.info("ChallengeId: " + queryString);
        int lastIndexOfSlash = 0;
        int length = 0;
        String[] attributes = new String[4];
        if(!queryString.isEmpty()){
            length = queryString.length();
            queryString = queryString.substring(lastIndexOfSlash, length);
            attributes = queryString.split("-;-");
        }

        //TODO:Prüfen, dass nur jeweils ein WebSocket auf eine neue Challenge existiert
        ChallengeParentActor.Create create = new ChallengeParentActor.Create(Long.toString(id), attributes[0], attributes[1], attributes[2], attributes[3]);

        return ask(userParentActor, create, t).thenApply((Object flow) -> {
            final Flow<String, String, NotUsed> f = (Flow<String, String, NotUsed>) flow;
            return f.named("websocket");
        });
    }

    private CompletionStage<F.Either<Result, Flow<String, String, ?>>> forbiddenResult() {
        final Result forbidden = Results.forbidden("forbidden");
        final F.Either<Result, Flow<String, String, ?>> left = F.Either.Left(forbidden);

        return CompletableFuture.completedFuture(left);
    }

    private F.Either<Result, Flow<String, String, ?>> logException(Throwable throwable) {
        Logger.error("Cannot create websocket", throwable);
        Result result = Results.internalServerError("error");
        return F.Either.Left(result);
    }

    private boolean originCheck(Http.RequestHeader rh) {
        final Optional<String> origin = rh.header("Origin");
        final Optional<String> rolesHeaderValue = rh.header("X-Auth-Roles");
        String roles = rolesHeaderValue.orElse(null);

        if (! origin.isPresent()) {
            Logger.error("originCheck: rejecting request because no Origin header found");
            return false;
        } else if (originMatches(origin.get())) {
            Logger.debug("originCheck: originValue = " + origin);
            if(roleCheck(roles)) {
                Logger.debug("roleCheck: roleValue = " + roles);
                return true;
            } else {
                Logger.error("roleCheck: rejecting request because X-Auth_Roles header value " + roles + " has no correct roles");
                return false;
            }
        } else {
            Logger.error("originCheck: rejecting request because Origin header value " + origin + " is not in the same origin");
            return false;
        }
    }

    private boolean roleCheck(String roles) {
        ArrayList<String> rolesList = ControllerUtil.splitSeparatedString(configuration.getString("authorized.editorRoles"));
        if(rolesList != null) {
            for(String role : rolesList) {
                if(roles != null && roles.contains(role)){
                    return true;
                }
            }
        }
        rolesList = ControllerUtil.splitSeparatedString(configuration.getString("authorized.adminRoles"));
        if(rolesList == null)
            return false;
        for(String role : rolesList) {
            if(roles != null && roles.contains(role)){
                return true;
            }
        }
        return false;
    }

    private boolean originMatches(String origin) {
        return origin.contains("localhost:9000") || origin.contains("localhost:3001") || origin.contains("hl.ygubler.ch") || origin.contains("idocker.hacking-lab.com") || origin.contains(ConfigFactory.load().getString("domain.value"));
    }
}
