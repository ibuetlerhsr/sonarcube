package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import models.DataTransferObject.ImportedChallengeDtos.FatChallengeDto;
import play.libs.ws.WSClient;
import play.mvc.*;
import services.DataTransferService;
import services.TranslationService;
import utils.ControllerUtil;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;


/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class ImportController extends Controller {
    private final static String CHALLENGE_ID_JSON_ATTR = "id";
    private final static String CHALLENGE_NAME_JSON_ATTR = "name";
    private final static String CHALLENGE_LEVEL_JSON_ATTR = "level";
    private final static String CHALLENGE_TITLE_JSON_ATTR = "title";
    private final static String CHALLENGE_TYPE_JSON_ATTR = "type";
    private final static String CHALLENGE_USAGE_JSON_ATTR = "usages";
    private final static String CHALLENGE_KEYWORDS_JSON_ATTR = "keywords";
    private final static String CHALLENGE_CATEGORIES_JSON_ATTR = "categories";
    private final static String CHALLENGE_CREATED_JSON_ATTR = "created";
    private final static String CHALLENGE_AUTHOR_JSON_ATTR = "author";
    private final static String CHALLENGE_UPDATE_JSON_ATTR = "last update";
    private final static String CHALLENGE_GIT_COMMIT_JSON_ATTR = "last git commit";
    private final static String CHALLENGE_RESOURCES_JSON_ATTR = "resources";
    private final static String CHALLENGE_GN_TYPE_JSON_ATTR = "goldnugget-type";
    private final static String CHALLENGE_GN_SECRET_JSON_ATTR = "goldnugget-secret-static";
    private final static String CHALLENGE_SECTIONS_JSON_ATTR = "sections";


    private Config configuration;
    private final DataTransferService dataTransferService;
    private final TranslationService translationService;

    private final WSClient wsChallengeClient;

    @Inject
    public ImportController(WSClient ws,
                            DataTransferService dataTransferService,
                            TranslationService translationService) {
        configuration = ConfigFactory.load();
        this.dataTransferService = dataTransferService;
        this.translationService = translationService;
        wsChallengeClient = ws;
    }

    private boolean tryParseLong(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String getSectionFromJson(JsonNode jsonNode) {
        return jsonNode.findPath(ImportController.CHALLENGE_SECTIONS_JSON_ATTR).toString();
    }

    private String getResourceFromJson(JsonNode jsonNode) {
        return jsonNode.findPath(ImportController.CHALLENGE_RESOURCES_JSON_ATTR).toString();
    }

    private String getTextJsonValueForKey(JsonNode jsonNode, String key) {
        JsonNode node = jsonNode.findPath(key);
        if(node != null)
            return node.textValue();
        return null;
    }

    private Long getNumberJsonValueForKey(JsonNode jsonNode, String key) {
        JsonNode node = jsonNode.findPath(key);
        if(node != null)
            return node.asLong();
        return 0L;
    }

    @Security.Authenticated(ImportActionAuthenticator.class)
    public Result importChallenge() {
        ObjectMapper objectMapper = new ObjectMapper();
        FatChallengeDto fatChallengeDto;
        try {
            fatChallengeDto = objectMapper.readValue(request().body().asJson().toString(), FatChallengeDto.class);
            String createdChallengeId = this.dataTransferService.importChallenge(fatChallengeDto);
            if(!createdChallengeId.equals("")) {
                return ok(ControllerUtil.createSuccessMessageNode("Successfully imported challenge"));
            }

        } catch(IOException exception) {
            return badRequest(ControllerUtil.createErrorMessageNode("Error while processing import!"));
        }
        return badRequest(ControllerUtil.createErrorMessageNode("Error while processing import!"));
    }

    @Security.Authenticated(ImportActionAuthenticator.class)
    public Result exportChallenge(String challengeId) {
        FatChallengeDto fatChallenge = this.dataTransferService.getChallengeForExport(challengeId, null);
        if(fatChallenge == null) {
            return badRequest(ControllerUtil.createErrorMessageNode("No such challenge available"));
        }
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node;
        node = objectMapper.valueToTree(fatChallenge);
        return ok(node);
    }

    @Security.Authenticated(ImportActionAuthenticator.class)
    public Result exportChallenges() {
        ArrayList<FatChallengeDto> fatChallenges = this.dataTransferService.getChallengesForExport(null);
        if(fatChallenges == null) {
            return badRequest(ControllerUtil.createErrorMessageNode("No challenges available"));
        }
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node;
        node = objectMapper.valueToTree(fatChallenges);
        return ok(node);
    }
}
