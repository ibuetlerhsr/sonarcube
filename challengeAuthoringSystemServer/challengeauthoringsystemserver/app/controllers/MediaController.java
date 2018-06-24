package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import binders.MediaQueryString;
import models.DatabaseObject.challenge.MediaObject;
import parsers.MyMultipartFileBodyParser;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.mvc.*;
import services.ChallengeService;
import services.MediaService;
import utils.ControllerUtil;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class MediaController extends Controller {

    private final static String IMAGE_JSON_ATTR = "base64Image";
    private final static String IMAGE_PATH_JSON_ATTR = "filePath";

    private Config configuration;
    private final MediaService mediaService;
    private final ChallengeService challengeService;

    @Inject
    public MediaController(WSClient ws,
                           MediaService mediaService,
                           ChallengeService challengeService){
        configuration = ConfigFactory.load();
        this.mediaService = mediaService;
        this.challengeService = challengeService;
    }

    private CompletionStage<Result> badJsonRequest(String content) {
        Result result = Results.badRequest(content);
        return CompletableFuture.completedFuture(result);
    }

    private Result proveEssentialValueAvailable(String value, String valueName) {
        if(value == null || value.equals("")) {
            return badRequest(ControllerUtil.createErrorMessageNode(valueName + " is not available. Please resend request"));
        }
        return null;
    }

    @Security.Authenticated(EditorActionAuthenticator.class)
    @BodyParser.Of(MyMultipartFileBodyParser.class)
    public Result upload(MediaQueryString mediaQueryString) {
        String challengeId = mediaQueryString.challengeId;
        String languageCode = mediaQueryString.languageIsoCode;
        final Http.MultipartFormData<File> formData = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> filePart = formData.getFile("files[]");
        File file = null;
        if(filePart != null)
            file = filePart.getFile();
        else
            file = formData.getFile("file").getFile();
        try{
            final long mediaId = this.mediaService.operateOnTempFile(file, languageCode, mediaQueryString.markdownType, mediaQueryString.markdownId);
            this.challengeService.connectChallengeWithMedia(challengeId, mediaId);
            return ok(Json.parse("{\"mediaId\": \"" + mediaId + "\"}"));
        } catch (IOException e) {
            return badRequest(ControllerUtil.createErrorMessageNode("Error while uploading image!"));
        }
    }

    @Security.Authenticated(EditorActionAuthenticator.class)
    public Result uploadBase64(MediaQueryString mediaQueryString) {
        String challengeId = mediaQueryString.challengeId;
        String languageCode = mediaQueryString.languageIsoCode;
        String markdownId = mediaQueryString.markdownId;
        String markdownType = mediaQueryString.markdownType;
        Result result = this.proveEssentialValueAvailable(markdownType, "Markdown-Type");
        if(result != null) {
            return result;
        }
        markdownType = markdownType.toLowerCase();
        if(challengeId == null && markdownId == null) {
            return badRequest(ControllerUtil.createErrorMessageNode("Challenge-ID and Markdown-ID are empty. Please resend request"));
        } else if(challengeId == null || challengeId.equals("")) {
            result = this.proveEssentialValueAvailable(markdownId, "Markdown-ID");
            if(result != null) {
                return result;
            }
        } else if(markdownId == null || markdownId.equals("")) {
            result = this.proveEssentialValueAvailable(challengeId, "Challenge-ID");
            if(result != null) {
                return result;
            }
            if(!markdownType.equals("abstract") && !markdownType.equals("solution")) {
                return badRequest(ControllerUtil.createErrorMessageNode("Markdown-ID cannot be null if markdown type is 'section', 'hint' or 'instruction'"));
            }
        }
        JsonNode mediaJsonNode = request().body().asJson();
        if(mediaJsonNode == null)
            return badRequest(ControllerUtil.createErrorMessageNode("Json data expected!"));
        String base64Image = mediaJsonNode.get(MediaController.IMAGE_JSON_ATTR).textValue();
        if( base64Image == null)
            return badRequest(ControllerUtil.createErrorMessageNode("Missing parameter: " + MediaController.IMAGE_JSON_ATTR));
        base64Image = base64Image.replace("data:image/png;base64,", "");
        String filePath = mediaJsonNode.get(MediaController.IMAGE_PATH_JSON_ATTR).textValue();
        if( filePath == null)
            return badRequest(ControllerUtil.createErrorMessageNode("Missing parameter: " + MediaController.IMAGE_PATH_JSON_ATTR));
        if(markdownId == null || markdownId.equals("")) {
            markdownId = this.challengeService.getSimpleMarkdownIdByChallengeId(challengeId, markdownType).toString();
        }
        final long mediaId = this.mediaService.createMediaFromBase64(filePath, base64Image, languageCode, markdownType, markdownId);
        return ok(Json.parse("{\"mediaId\": \"" + mediaId + "\"}"));
    }

    @Security.Authenticated(EditorActionAuthenticator.class)
    public Result removeImage(MediaQueryString mediaQueryString) {
        String mediaId = mediaQueryString.mediaId;
        String challengeId = mediaQueryString.challengeId;
        Result result = this.proveEssentialValueAvailable(mediaId, "Media-ID");
        if(result != null) {
            return result;
        }
        if(ControllerUtil.tryParseLong(mediaId) && this.mediaService.getMediaReferenceById(Long.parseLong(mediaId)) == null)
            return badRequest(ControllerUtil.createErrorMessageNode("Media-ID "+ mediaId + " is not available. Please resend request"));
        if(ControllerUtil.tryParseLong(mediaId) && challengeId != null) {
            this.challengeService.disconnectChallengeFromMedia(challengeId, Long.parseLong(mediaId));
        } else {
            if(ControllerUtil.tryParseLong(mediaId)) {
                this.challengeService.disconnectChallengeFromMediaByMediaId(Long.parseLong(mediaId));
            } else {
                return badRequest(ControllerUtil.createErrorMessageNode("Media-ID "+ mediaId + " must be a number!"));
            }
        }
        return ok(ControllerUtil.createSuccessMessageNode("Image successfully removed"));
    }

    @Security.Authenticated(StandardActionAuthenticator.class)
    public Result getImage(String path) {
        MediaObject mediaObject = null;
        if(ControllerUtil.tryParseLong(path)) {
            mediaObject = this.mediaService.getMediaObjectById(Long.parseLong(path));
        } else {
            mediaObject = this.mediaService.getMediaObjectByUrlAndLanguage(path, "en");
        }
        if(mediaObject != null) {
            return ok(this.mediaService.writeMediaObjectToFile(path, mediaObject.getContent()));
        }
        else {
            return badRequest(ControllerUtil.createErrorMessageNode("File not found"));
        }
    }
}
