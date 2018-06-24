package controllers;

import binders.FilterQueryString;
import binders.MarkdownItemQueryString;
import binders.SectionQueryString;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import models.ChallengeParameter;
import models.DataTransferObject.*;
import models.DataTransferObject.ImportedChallengeDtos.FatChallengeDto;
import models.DataTransferObject.ImportedChallengeDtos.SolutionMediaDto;
import models.DataTransferObject.ImportedChallengeDtos.TranslatedChallengeDto;
import models.DatabaseObject.challenge.*;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.mvc.*;
import services.ChallengeService;
import services.DataTransferService;
import services.TranslationService;
import utils.ControllerUtil;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class ChallengeController extends Controller {

    private final static String CHALLENGE_NAME_JSON_ATTR = "challengeName";
    private final static String CHALLENGE_LANGUAGE_JSON_ATTR = "Challenge-Language";
    private final static String CHALLENGE_TITLE_JSON_ATTR = "challengeTitle";
    private final static String GOLDNUGGET_TYPE_JSON_ATTR = "goldnuggetType";
    private final static String STATIC_GOLDNUGGET_JSON_ATTR = "staticGoldnuggetSecret";
    private final static String CHALLENGE_TITLE_IMAGE_ID_JSON_ATTR = "titleImageId";
    private final static String CHALLENGE_ID_JSON_ATTR = "challengeId";
    private final static String CHALLENGE_LEVEL_JSON_ATTR = "challengeLevel";
    private final static String CHALLENGE_USAGE_JSON_ATTR = "challengeUsages";
    private final static String CHALLENGE_KEYWORDS_JSON_ATTR = "challengeKeywords";
    private final static String CHALLENGE_CATEGORY_JSON_ATTR = "challengeCategories";
    private final static String CHALLENGE_IS_PRIVATE_JSON_ATTR = "isPrivate";
    private final static String CHALLENGE_TYPE_JSON_ATTR = "challengeType";

    private Config configuration;
    private final ChallengeService challengeService;
    private final DataTransferService dataTransferService;
    private final TranslationService translationService;

    private final WSClient wsChallengeClient;
    @Inject
    public ChallengeController(WSClient ws,
                               ChallengeService challengeService,
                               DataTransferService dataTransferService,
                               TranslationService translationService){
        configuration = ConfigFactory.load();
        this.challengeService = challengeService;
        this.translationService = translationService;
        this.dataTransferService = dataTransferService;
        wsChallengeClient = ws;
    }

    @Security.Authenticated(EditorActionAuthenticator.class)
    private Result handleNewChallenge(ChallengeDto newChallenge, ChallengeParameter challengeParameter){
        JsonNode node = null;
        try {
            this.challengeService.insertToLocalDB(newChallenge, challengeParameter);
            node = Json.parse("{\"challengeName\":\"" + newChallenge.getChallengeName() + "\"}");
        }
        catch(Exception ex) {
            return badRequest(ControllerUtil.createErrorMessageNode("Error creating new challenge on database!"));
        }

        return ok(node);
    }

    private String createJsonMetadataString(ChallengeDto updatedChallenge, ChallengeParameter challengeParameter) {
        ChallengeVersion challengeVersion =  this.challengeService.getChallengeVersionByChallengeId(updatedChallenge.get_id());
        String jsonMetadataValue = "{";
        if(challengeVersion != null) {
            ChallengeLevel level = challengeVersion.getChallengeLevel();
            if(level != null && level.getText() != null) {
                jsonMetadataValue += "\"challengeLevel\": \"" + level.getId() + "\", ";
            } else {
                jsonMetadataValue += "\"challengeLevel\": \"" + -1 + "\", ";
            }
        }
        if(challengeParameter != null) {
            if(challengeParameter.getTitle() != null && !challengeParameter.getTitle().equals(""))
                jsonMetadataValue += "\"challengeTitle\": \"" + challengeParameter.getTitle() + "\", ";
            if(challengeParameter.getGoldnuggetType() != null && !challengeParameter.getGoldnuggetType().equals(""))
                jsonMetadataValue += "\"goldnuggetType\": \"" + challengeParameter.getGoldnuggetType() + "\", ";
            if(challengeParameter.getStaticGoldnuggetSecret() != null && !challengeParameter.getStaticGoldnuggetSecret().equals(""))
                jsonMetadataValue += "\"staticGoldnuggetSecret\": \"" + challengeParameter.getStaticGoldnuggetSecret() + "\", ";
            if(challengeParameter.isPrivate() != null && !challengeParameter.isPrivate().isEmpty())
                jsonMetadataValue += "\"isPrivate\": \"" + challengeParameter.isPrivate() + "\", ";
        }
        if(updatedChallenge != null) {
            if(updatedChallenge.getChallengeName() != null && !updatedChallenge.getChallengeName().equals(""))
                jsonMetadataValue += "\"challengeName\": \"" + updatedChallenge.getChallengeName() + "\", ";
            if(updatedChallenge.getChallengeType() != null && !updatedChallenge.getChallengeType().equals(""))
                jsonMetadataValue += "\"challengeType\": \"" + updatedChallenge.getChallengeType() + "\", ";
        }
        return jsonMetadataValue.substring(0, jsonMetadataValue.lastIndexOf(", ")) + "}";
    }

    @Security.Authenticated(EditorActionAuthenticator.class)
    private Result handleUpdateChallenge(ChallengeDto updatedChallenge, ChallengeParameter challengeParameter){
        JsonNode node = null;
        try {
            this.challengeService.updateLocalDB(updatedChallenge, challengeParameter);
            node = Json.parse(createJsonMetadataString(updatedChallenge, challengeParameter));

        }
        catch(Exception ex) {
            return badRequest(ControllerUtil.createErrorMessageNode("Error creating new challenge on database!"));
        }

        return ok(node);
    }

    @Security.Authenticated(StandardActionAuthenticator.class)
    public Result getLevels() {
        ChallengeLevelDto[] challengeLevelDtos = new ChallengeLevelDto[this.challengeService.getChallengeLevels().size()];
        int index = 0;
        for(ChallengeLevel level: this.challengeService.getChallengeLevels()){
            String translation = this.translationService.getTranslationByAttributeId(level.getText().getId(), "en");
            challengeLevelDtos[index] = new ChallengeLevelDto(level.getId(), translation , level.getMaxPoint());
            index++;
        }
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode node = mapper.valueToTree(challengeLevelDtos);
        return ok(node);
    }

    @Security.Authenticated(StandardActionAuthenticator.class)
    public Result getTypes() {
        ChallengeTypeDto[] challengeTypeDtos = new ChallengeTypeDto[this.challengeService.getChallengeTypes().size()];
        int index = 0;
        for(ChallengeType type: this.challengeService.getChallengeTypes()){
            String translation = this.translationService.getTranslationByAttributeId(type.getText().getId(), "en");
            challengeTypeDtos[index] = new ChallengeTypeDto(type.getId(), translation);
            index++;
        }
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode node = mapper.valueToTree(challengeTypeDtos);
        return ok(node);
    }

    @Security.Authenticated(StandardActionAuthenticator.class)
    public Result getUsages() {
        ChallengeUsageDto[] challengeUsageDtos = new ChallengeUsageDto[this.challengeService.getChallengeUsages().size()];
        int index = 0;
        for(ChallengeUsage usage: this.challengeService.getChallengeUsages()){
            String translation = this.translationService.getTranslationByAttributeId(usage.getText().getId(), "en");
            challengeUsageDtos[index] = new ChallengeUsageDto(usage.getId(), translation);
            index++;
        }
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode node = mapper.valueToTree(challengeUsageDtos);
        return ok(node);
    }

    @Security.Authenticated(StandardActionAuthenticator.class)
    public Result getCategories() {
        ChallengeCategoryDto[] challengeCategoryDtos = new ChallengeCategoryDto[this.challengeService.getCategories().size()];
        int index = 0;
        for(Category category: this.challengeService.getCategories()){
            String translation = this.translationService.getTranslationByAttributeId(category.getCategoryName().getId(), "en");
            challengeCategoryDtos[index] = new ChallengeCategoryDto(category.getId(), translation);
            index++;
        }
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode node = mapper.valueToTree(challengeCategoryDtos);
        return ok(node);
    }

    @Security.Authenticated(StandardActionAuthenticator.class)
    public Result getKeywords() {
        List<Keyword> keywords = this.challengeService.getKeywords();
        KeywordDto[] keywordDtos = new KeywordDto[keywords.size()];
        int index = 0;
        for(Keyword keyword: keywords){
            keywordDtos[index] = new KeywordDto(keyword.getId(), keyword.getText());
            index++;
        }
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode node = mapper.valueToTree(keywordDtos);
        return ok(node);
    }

    private CompletionStage<Result> badJsonRequest(String content) {
        Result result = Results.badRequest(content);
        return CompletableFuture.completedFuture(result);
    }

    @Security.Authenticated(EditorActionAuthenticator.class)
    public Result createNewChallenge(){
        ChallengeDto challengeDto;
        String id = null;
        String type = "";
        String level = "";
        String name = "";
        String title = "";
        String goldnuggetType = "";
        String staticGoldnuggetSecret = "";
        JsonNode challengeJsonNode = request().body().asJson();
        if(challengeJsonNode == null)
            return badRequest(ControllerUtil.createErrorMessageNode("Error Json data expected!"));
        String challengeId = challengeJsonNode.get(ChallengeController.CHALLENGE_ID_JSON_ATTR).textValue();
        if( challengeId == null)
            return badRequest(ControllerUtil.createErrorMessageNode("Missing parameter: " + ChallengeController.CHALLENGE_ID_JSON_ATTR));
        String challengeName = challengeJsonNode.findPath(ChallengeController.CHALLENGE_NAME_JSON_ATTR).textValue();
        String challengeTitle = challengeJsonNode.findPath(ChallengeController.CHALLENGE_TITLE_JSON_ATTR).textValue();
        String challengeLevel = challengeJsonNode.findPath(ChallengeController.CHALLENGE_LEVEL_JSON_ATTR).textValue();
        String challengeType = challengeJsonNode.findPath(ChallengeController.CHALLENGE_TYPE_JSON_ATTR).textValue();
        String challengeUsages = challengeJsonNode.findPath(ChallengeController.CHALLENGE_USAGE_JSON_ATTR).textValue();
        String challengeCategories = challengeJsonNode.findPath(ChallengeController.CHALLENGE_CATEGORY_JSON_ATTR).textValue();
        String challengeKeywords = challengeJsonNode.findPath(ChallengeController.CHALLENGE_KEYWORDS_JSON_ATTR).textValue();
        String isPrivate = challengeJsonNode.findPath(ChallengeController.CHALLENGE_IS_PRIVATE_JSON_ATTR).textValue();
        String goldnuggetTypeFromJson = challengeJsonNode.findPath(ChallengeController.GOLDNUGGET_TYPE_JSON_ATTR).textValue();
        String staticGoldnuggetSecretFromJson = challengeJsonNode.findPath(ChallengeController.STATIC_GOLDNUGGET_JSON_ATTR).textValue();

        Map<String, String> challengeValuesMap = this.challengeService.getValuesFromChallenge(challengeId);

        if(challengeValuesMap != null) {
            type = challengeValuesMap.get("type");
            name = challengeValuesMap.get("name");
            title = challengeValuesMap.get("title");
        }

        if(!challengeId.isEmpty()){
            id = challengeId;
        }
        if(challengeName != null){
            name = challengeName;
        }
        if(challengeType != null){
            type = challengeType;
        }
        if(challengeLevel != null){
            level = this.challengeService.getChallengeLevelTranslationByText(challengeLevel);
        }
        if(challengeTitle != null){
            title = challengeTitle;
        }
        if(staticGoldnuggetSecretFromJson != null) {
            staticGoldnuggetSecret = staticGoldnuggetSecretFromJson;
        }

        if(goldnuggetTypeFromJson != null) {
            goldnuggetType = goldnuggetTypeFromJson;
        }

        challengeDto = new ChallengeDto(id, name, level, type, null, null, "en");

        ChallengeParameter challengeParameter = new ChallengeParameter();
        challengeParameter.setTitle(title);
        challengeParameter.setAuthor(request().getHeaders().get("X-Auth-Username").orElse(null));
        challengeParameter.setChallengeCategories(challengeCategories);
        challengeParameter.setChallengeKeywords(challengeKeywords);
        challengeParameter.setChallengeUsages(challengeUsages);
        challengeParameter.setPrivate(isPrivate);
        challengeParameter.setGoldnuggetType(goldnuggetType);
        challengeParameter.setStaticGoldnuggetSecret(staticGoldnuggetSecret);
        if(challengeValuesMap == null){
            return handleNewChallenge(challengeDto, challengeParameter);
        } else {
            return handleUpdateChallenge(challengeDto, challengeParameter);
        }
    }

    @Security.Authenticated(StandardActionAuthenticator.class)
    public Result getSpecificChallenge(String challengeId) {
        Optional<String> languageHeader = request().getHeaders().get(ChallengeController.CHALLENGE_LANGUAGE_JSON_ATTR);
        String languageIsoCode = null;
        if(languageHeader.isPresent())
            languageIsoCode = languageHeader.orElseGet(null);
        FatChallengeDto fatChallenge = this.dataTransferService.getChallengeForExport(challengeId, languageIsoCode);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node;
        if(fatChallenge != null) {
            node = objectMapper.valueToTree(fatChallenge);
            return ok(node);
        }

        return badRequest(ControllerUtil.createErrorMessageNode("No challenge " + challengeId + " available"));
    }

    @Security.Authenticated(StandardActionAuthenticator.class)
    public Result getSpecificSolution(String challengeId) {
        Optional<String> languageHeader = request().getHeaders().get(ChallengeController.CHALLENGE_LANGUAGE_JSON_ATTR);
        String languageIsoCode = null;
        if(languageHeader.isPresent())
            languageIsoCode = languageHeader.orElseGet(null);
        SolutionMediaDto solutionForExport = this.dataTransferService.getSolutionForExport(challengeId, languageIsoCode);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node;
        if(solutionForExport != null) {
            node = objectMapper.valueToTree(solutionForExport);
            return ok(node);
        }

        return badRequest(ControllerUtil.createErrorMessageNode("No solution for challenge " + challengeId + " available"));
    }

    @Security.Authenticated(StandardActionAuthenticator.class)
    public Result getSpecificTranslatedChallenge(String challengeId) {
        Optional<String> languageHeader = request().getHeaders().get(ChallengeController.CHALLENGE_LANGUAGE_JSON_ATTR);
        String languageIsoCode = null;
        if(languageHeader.isPresent())
            languageIsoCode = languageHeader.orElseGet(null);
        TranslatedChallengeDto translatedChallengeDto = this.dataTransferService.getChallengeTranslationForExport(challengeId, languageIsoCode);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node;
        if(translatedChallengeDto != null) {
            node = objectMapper.valueToTree(translatedChallengeDto);
            return ok(node);
        }

        return badRequest(ControllerUtil.createErrorMessageNode("No challenge " + challengeId + " available"));

    }

    @Security.Authenticated(StandardActionAuthenticator.class)
    public Result getFilteredChallenges(FilterQueryString queryString){
        ArrayNode challengesArray = this.challengeService.getFilteredChallengesMetadata(queryString.fromDate, queryString.toDate);
        return ok(challengesArray);
    }

    @Security.Authenticated(TranslatorActionAuthenticator.class)
    public Result setSpecificChallenge(String challengeId) {
        if(challengeId == null || challengeId.equals(""))
            return badRequest(ControllerUtil.createErrorMessageNode("No Challenge-ID was send in query string!"));
        ChallengeVersion version = this.challengeService.getChallengeVersionByChallengeId(challengeId);
        if(version == null)
            return badRequest(ControllerUtil.createErrorMessageNode("No challenge " + challengeId + " available"));
        JsonNode challengeJsonNode = request().body().asJson();
        if(challengeJsonNode == null)
            return badRequest(ControllerUtil.createErrorMessageNode("Json data expected!"));
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode messageJson = Json.newObject();
        TranslatedChallengeDto translatedChallengeDto;
        try {
            translatedChallengeDto = objectMapper.readValue(request().body().asJson().toString(), TranslatedChallengeDto.class);
            if(translatedChallengeDto.getLanguageIsoCode() == null || translatedChallengeDto.getLanguageIsoCode().equals(""))
                return badRequest(ControllerUtil.createErrorMessageNode("No language specified in body"));;
            this.challengeService.handleTranslatedChallengeDto(version, translatedChallengeDto);
        } catch(IOException exception) {
            return badRequest(ControllerUtil.createErrorMessageNode("Translated challenge could not be saved correctly!"));
        }

        return ok(ControllerUtil.createSuccessMessageNode("Successfully translated challenge"));
    }

    @Security.Authenticated(EditorActionAuthenticator.class)
    public Result removeStep(String stepId) {
        if(stepId == null || stepId.equals("")) {
            return badRequest(ControllerUtil.createErrorMessageNode("No Step-ID was send in query string!"));
        }
        this.challengeService.removeMarkdownById(stepId);
        return ok(ControllerUtil.createSuccessMessageNode("Successfully removed step"));
    }

    @Security.Authenticated(StandardActionAuthenticator.class)
    public Result rateChallenge(String challengeId, String rateValue, String description) {
        return ok();
    }

    @Security.Authenticated(StandardActionAuthenticator.class)
    public Result getNewChallengeId() {
        String challengeUUID = UUID.randomUUID().toString();
        while(this.challengeService.getChallengeById(challengeUUID) != null) {
            challengeUUID = UUID.randomUUID().toString();
        }
        JsonNode node = Json.parse("{\"challengeId\":\"" + challengeUUID + "\"}");
        return ok(node);
    }

    @Security.Authenticated(EditorActionAuthenticator.class)
    public Result getNewSectionId(SectionQueryString sectionQueryString) {
        Section section = this.challengeService.initializeSectionForChallengeOnDb(sectionQueryString.challengeId);
        if(section == null) {
            return badRequest(ControllerUtil.createErrorMessageNode("Section could not be initialized"));
        }
        JsonNode node = Json.parse("{\"sectionId\":\"" + section.getId() + "\", \"order\":\"" + section.getOrder() + "\"}");
        return ok(node);
    }

    private Result proveSectionAvailable(String sectionId) {
        if(sectionId.equals("")) {
            return badRequest(ControllerUtil.createErrorMessageNode("Section-ID is not available. Please resend request"));
        }
        return null;
    }

    private Result proveChallengeIdAvailable(String challengeId) {
        if(challengeId == null || challengeId.equals("")) {
            return badRequest(ControllerUtil.createErrorMessageNode("Challenge-ID is not available. Please resend request"));
        }
        return null;
    }

    @Security.Authenticated(EditorActionAuthenticator.class)
    public Result getNewInstructionId(SectionQueryString sectionQueryString) {
        Result result = this.proveSectionAvailable(sectionQueryString.sectionId);
        if(result != null)
            return result;
        String instructionUUID = UUID.randomUUID().toString();
        while(this.challengeService.getInstructionById(instructionUUID) != null) {
            instructionUUID = UUID.randomUUID().toString();
        }
        Instruction instruction = this.challengeService.initializeInstructionForSectionOnDb(sectionQueryString.sectionId, instructionUUID);
        if(instruction == null) {
            return badRequest(ControllerUtil.createErrorMessageNode("Instruction could not be initialized"));
        }
        JsonNode node = Json.parse("{\"sectionId\":\"" + sectionQueryString.sectionId + "\", \"instructionId\":\"" + instruction.getId() + "\", \"type\":\"instruction\", \"order\":\"" + instruction.getOrder()+ "\"}");
        return ok(node);
    }

    @Security.Authenticated(EditorActionAuthenticator.class)
    public Result getNewHintId(SectionQueryString sectionQueryString) {
        Result result = this.proveSectionAvailable(sectionQueryString.sectionId);
        if(result != null)
            return result;
        String hintUUID = UUID.randomUUID().toString();
        while(this.challengeService.getHintById(hintUUID) != null) {
            hintUUID = UUID.randomUUID().toString();
        }
        Hint hint = this.challengeService.initializeHintForSectionOnDb(sectionQueryString.sectionId, hintUUID);
        if(hint == null) {
            return badRequest(ControllerUtil.createErrorMessageNode("Hint could not be initialized"));
        }
        JsonNode node = Json.parse("{\"sectionId\":\"" + sectionQueryString.sectionId + "\", \"hintId\":\"" + hint.getId() + "\", \"type\":\"hint\", \"order\":\"" + hint.getOrder()+ "\"}");
        return ok(node);
    }

    @Security.Authenticated(EditorActionAuthenticator.class)
    public Result getNewAbstractId(MarkdownItemQueryString markdownItemQueryString) {
        Result result = this.proveChallengeIdAvailable(markdownItemQueryString.challengeId);
        if(result != null) {
            return result;
        }
        Long abstractId = this.challengeService.initializeNewAbstractOnDb(markdownItemQueryString.challengeId);
        JsonNode node = Json.parse("{\"abstractId\":\"" + abstractId + "\"}");
        return ok(node);
    }

    @Security.Authenticated(EditorActionAuthenticator.class)
    public Result getAbstractId(MarkdownItemQueryString markdownItemQueryString) {
        Result result = this.proveChallengeIdAvailable(markdownItemQueryString.challengeId);
        if(result != null) {
            return result;
        }
        ChallengeVersion challengeVersion = this.challengeService.getChallengeVersionByChallengeId(markdownItemQueryString.challengeId);
        if(challengeVersion.getAnAbstract() != null) {
            JsonNode node = Json.parse("{\"abstractId\":\"" + challengeVersion.getAnAbstract().getId() + "\"}");
            return ok(node);
        } else {
            return badRequest(ControllerUtil.createErrorMessageNode("No abstract defined for challenge " + markdownItemQueryString.challengeId));
        }
    }

    @Security.Authenticated(EditorActionAuthenticator.class)
    public Result getNewSolutionId(MarkdownItemQueryString markdownItemQueryString) {
        Result result = this.proveChallengeIdAvailable(markdownItemQueryString.challengeId);
        if(result != null) {
            return result;
        }
        Long solutionId = this.challengeService.initializeNewSolutionOnDb(markdownItemQueryString.challengeId);
        JsonNode node = Json.parse("{\"solutionId\":\"" + solutionId + "\"}");
        return ok(node);
    }

    @Security.Authenticated(EditorActionAuthenticator.class)
    public Result getSolutionId(MarkdownItemQueryString markdownItemQueryString) {
        Result result = this.proveChallengeIdAvailable(markdownItemQueryString.challengeId);
        if(result != null) {
            return result;
        }
        ChallengeVersion challengeVersion = this.challengeService.getChallengeVersionByChallengeId(markdownItemQueryString.challengeId);
        if(challengeVersion.getSolution() != null) {
            JsonNode node = Json.parse("{\"solutionId\":\"" + challengeVersion.getSolution().getId() + "\"}");
            return ok(node);
        } else {
            return badRequest(ControllerUtil.createErrorMessageNode("No solution defined for challenge " + markdownItemQueryString.challengeId));
        }
    }

    @Security.Authenticated(StandardActionAuthenticator.class)
    public Result getChallenges() {
        ArrayNode challengesArray = this.challengeService.getChallengesMetadata();
        return ok(challengesArray);
    }
}
