package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import models.DataTransferObject.ConsumerDto;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import utils.ControllerUtil;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class ConsumerController extends Controller{

    private Config configuration;

    private final WSClient wsChallengeClient;
    @Inject
    public ConsumerController(WSClient ws){
        configuration = ConfigFactory.load();
        wsChallengeClient = ws;
    }


    @Security.Authenticated(StandardActionAuthenticator.class)
    public CompletionStage<Result> getConsumer() {
        WSRequest request = wsChallengeClient.url(configuration.getString("challengeDirectorySystem.consumerUrl"));
        request = request.addHeader("Content-Type", "application/json");
        request = request.addHeader("cache-control", "no-cache");
        WSRequest complexRequest = request.addHeader("x-apikey", configuration.getString("challengedirectorysystem.apiKey"));
        return complexRequest.get().thenApply(response -> {
            ObjectMapper objectMapper = new ObjectMapper();
            List<ConsumerDto> consumers = new ArrayList<>();
            try{
                consumers = Arrays.asList(objectMapper.readValue(response.getBody(), ConsumerDto[].class));
            } catch(Exception ex) {
                return badRequest(ControllerUtil.createErrorMessageNode("Error while requesting consumers!"));
            }
            return ok(response.getBody());
        });
    }

    @Security.Authenticated(StandardActionAuthenticator.class)
    public CompletionStage<Result> getSpecificConsumer() {
        Optional<String> consumerId = request().getHeaders().get("Consumer-ID");
        String consumerString = consumerId.orElse("");
        String url = configuration.getString("challengeDirectorySystem.consumerUrlObject").replace("(OBJECT_ID)", consumerString);
        WSRequest request = wsChallengeClient.url(url);
        request = request.addHeader("Content-Type", "application/json");
        WSRequest complexRequest = request.addHeader("x-apikey", configuration.getString("challengedirectorysystem.apiKey"));

        return complexRequest.get().thenApply(response -> {
            ObjectMapper objectMapper = new ObjectMapper();
            ConsumerDto consumer;
            try {
                consumer = objectMapper.readValue(response.getBody(), ConsumerDto.class);
                if (consumer == null)
                    return badRequest();
            } catch (Exception ex) {
                return badRequest(ControllerUtil.createErrorMessageNode("Error while getting specific consumer!"));
            }
            return ok(response.getBody());
        });
    }
}
