package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.typesafe.config.Config;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.ApplicationLoader;
import play.Environment;
import play.inject.guice.GuiceApplicationBuilder;
import play.inject.guice.GuiceApplicationLoader;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;
import utils.TestUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

public class MediaControllerTest extends WithApplication {

    @Inject
    Application application;

    @Inject
    Config configuration;

    @Before
    public void setup() {
        Module testModule = new AbstractModule() {
            @Override
            public void configure() {
            }
        };

        GuiceApplicationBuilder builder = new GuiceApplicationLoader()
                .builder(new ApplicationLoader.Context(Environment.simple()))
                .overrides(testModule);
        Guice.createInjector(builder.applicationModule()).injectMembers(this);

        Helpers.start(application);

        String testFileData = TestUtil.readTestFile("fatChallengeJsonForMedia.json");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("auth_key", configuration.getString("challengeImport.apiKey"))
                .bodyJson(Json.parse(testFileData))
                .uri("/api/importChallenge");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    @After
    public void teardown() {
        Helpers.stop(application);
    }


    @Test
    public void testUploadBase64ImageAbstractWithoutBody() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownType=abstract");

        Result result = route(application, request);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void testUploadBase64ImageWithoutQueryString() {
        String base64Image = TestUtil.readTestFile("base64Image.txt");
        String filePath = "/media/png/Test.png";
        JsonNode node = Json.parse("{\"base64Image\": \"" + base64Image + "\", \"filePath\": \"" + filePath +"\"}");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .bodyJson(node)
                .uri("/api/upload_image_b64");

        Result result = route(application, request);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void testUploadBase64ImageAbstractWrongPath() {
        String base64Image = TestUtil.readTestFile("base64Image.txt");
        String filePath = "/media/png/Test.png";
        JsonNode node = Json.parse("{\"base64Image\": \"" + base64Image + "\", \"filePath\": \"" + filePath +"\"}");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .bodyJson(node)
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownType=abstract");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testUploadBase64ImageAbstractUpperCaseMarkdownType() {
        String base64Image = TestUtil.readTestFile("base64Image.txt");
        String filePath = "/media/png/Test.png";
        JsonNode node = Json.parse("{\"base64Image\": \"" + base64Image + "\", \"filePath\": \"" + filePath +"\"}");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .bodyJson(node)
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownType=ABSTRACT");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testUploadBase64ImageAbstractUrlWrongPath() {
        String base64Image = TestUtil.readTestFile("base64Url.txt");
        String filePath = "/media/png/Test.png";
        JsonNode node = Json.parse("{\"base64Image\": \"" + base64Image + "\", \"filePath\": \"" + filePath +"\"}");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .bodyJson(node)
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownType=abstract");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testUploadBase64ImageAbstractCorrectly() {
        String base64Image = TestUtil.readTestFile("base64Image.txt");
        String filePath = "\\\\media\\\\png\\\\Test.png";
        JsonNode node = Json.parse("{\"base64Image\": \"" + base64Image + "\", \"filePath\": \"" + filePath +"\"}");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .bodyJson(node)
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownType=abstract");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testUploadBase64ImageAbstractUrlCorrectly() {
        String base64Image = TestUtil.readTestFile("base64Url.txt");
        String filePath = "\\\\media\\\\png\\\\Test.png";
        JsonNode node = Json.parse("{\"base64Image\": \"" + base64Image + "\", \"filePath\": \"" + filePath +"\"}");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .bodyJson(node)
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownType=abstract");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testUploadBase64ImageSolutionWithoutBody() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownType=solution");

        Result result = route(application, request);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void testUploadBase64ImageSolutionWrongPath() {
        String base64Image = TestUtil.readTestFile("base64Image.txt");
        String filePath = "/media/png/Test.png";
        JsonNode node = Json.parse("{\"base64Image\": \"" + base64Image + "\", \"filePath\": \"" + filePath +"\"}");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .bodyJson(node)
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownType=solution");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testUploadBase64ImageSolutionUpperCaseMarkdownType() {
        String base64Image = TestUtil.readTestFile("base64Image.txt");
        String filePath = "/media/png/Test.png";
        JsonNode node = Json.parse("{\"base64Image\": \"" + base64Image + "\", \"filePath\": \"" + filePath +"\"}");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .bodyJson(node)
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownType=SOLUTION");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testUploadBase64ImageSolutionUrlWrongPath() {
        String base64Image = TestUtil.readTestFile("base64Url.txt");
        String filePath = "/media/png/Test.png";
        JsonNode node = Json.parse("{\"base64Image\": \"" + base64Image + "\", \"filePath\": \"" + filePath +"\"}");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .bodyJson(node)
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownType=solution");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testUploadBase64ImageSolutionCorrectly() {
        String base64Image = TestUtil.readTestFile("base64Image.txt");
        String filePath = "\\\\media\\\\png\\\\Test.png";
        JsonNode node = Json.parse("{\"base64Image\": \"" + base64Image + "\", \"filePath\": \"" + filePath +"\"}");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .bodyJson(node)
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownType=solution");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testUploadBase64ImageSolutionUrlCorrectly() {
        String base64Image = TestUtil.readTestFile("base64Url.txt");
        String filePath = "\\\\media\\\\png\\\\Test.png";
        JsonNode node = Json.parse("{\"base64Image\": \"" + base64Image + "\", \"filePath\": \"" + filePath +"\"}");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .bodyJson(node)
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownType=solution");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testUploadBase64ImageSectionWithoutBody() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownType=section");

        Result result = route(application, request);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void testUploadBase64ImageSectionWithoutChallengeId() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .uri("/api/upload_image_b64?languageCode=en&markdownId=7777cfb3-1ee7-4a16-866a-051fd985cf07&markdownType=section");

        Result result = route(application, request);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void testUploadBase64ImageSectionWithoutMarkdownId() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownType=section");

        Result result = route(application, request);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void testUploadBase64ImageSectionWithoutMarkdownAndChallengeId() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .uri("/api/upload_image_b64?languageCode=en&markdownType=section");

        Result result = route(application, request);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void testUploadBase64ImageSectionWrongPath() {
        String base64Image = TestUtil.readTestFile("base64Image.txt");
        String filePath = "/media/png/Test.png";
        JsonNode node = Json.parse("{\"base64Image\": \"" + base64Image + "\", \"filePath\": \"" + filePath +"\"}");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .bodyJson(node)
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownId=7777cfb3-1ee7-4a16-866a-051fd985cf07&markdownType=section");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testUploadBase64ImageSectionUpperCaseMarkdownType() {
        String base64Image = TestUtil.readTestFile("base64Image.txt");
        String filePath = "/media/png/Test.png";
        JsonNode node = Json.parse("{\"base64Image\": \"" + base64Image + "\", \"filePath\": \"" + filePath +"\"}");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .bodyJson(node)
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownId=7777cfb3-1ee7-4a16-866a-051fd985cf07&markdownType=SECTION");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testUploadBase64ImageSectionUrlWrongPath() {
        String base64Image = TestUtil.readTestFile("base64Url.txt");
        String filePath = "/media/png/Test.png";
        JsonNode node = Json.parse("{\"base64Image\": \"" + base64Image + "\", \"filePath\": \"" + filePath +"\"}");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .bodyJson(node)
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownId=7777cfb3-1ee7-4a16-866a-051fd985cf07&markdownType=section");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testUploadBase64ImageSectionCorrectly() {
        String base64Image = TestUtil.readTestFile("base64Image.txt");
        String filePath = "\\\\media\\\\png\\\\Test.png";
        JsonNode node = Json.parse("{\"base64Image\": \"" + base64Image + "\", \"filePath\": \"" + filePath +"\"}");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .bodyJson(node)
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownId=7777cfb3-1ee7-4a16-866a-051fd985cf07&markdownType=section");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testUploadBase64ImageSectionUrlCorrectly() {
        String base64Image = TestUtil.readTestFile("base64Url.txt");
        String filePath = "\\\\media\\\\png\\\\Test.png";
        JsonNode node = Json.parse("{\"base64Image\": \"" + base64Image + "\", \"filePath\": \"" + filePath +"\"}");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .bodyJson(node)
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownId=7777cfb3-1ee7-4a16-866a-051fd985cf07&markdownType=section");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    //hier
    @Test
    public void testUploadBase64ImageHintWithoutBody() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownType=hint");

        Result result = route(application, request);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void testUploadBase64ImageHintWithoutChallengeId() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                
                .uri("/api/upload_image_b64?languageCode=en&markdownId=777767f3-2e2a-4bd0-891b-fa92c1cad6b4&markdownType=hint");

        Result result = route(application, request);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void testUploadBase64ImageHintWithoutMarkdownId() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownType=hint");

        Result result = route(application, request);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void testUploadBase64ImageHintWithoutMarkdownAndChallengeId() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .uri("/api/upload_image_b64?languageCode=en&markdownType=hint");

        Result result = route(application, request);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void testUploadBase64ImageHintWrongPath() {
        String base64Image = TestUtil.readTestFile("base64Image.txt");
        String filePath = "/media/png/Test.png";
        JsonNode node = Json.parse("{\"base64Image\": \"" + base64Image + "\", \"filePath\": \"" + filePath +"\"}");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .bodyJson(node)
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownId=777767f3-2e2a-4bd0-891b-fa92c1cad6b4&markdownType=hint");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testUploadBase64ImageHintUpperCaseMarkdownType() {
        String base64Image = TestUtil.readTestFile("base64Image.txt");
        String filePath = "/media/png/Test.png";
        JsonNode node = Json.parse("{\"base64Image\": \"" + base64Image + "\", \"filePath\": \"" + filePath +"\"}");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .bodyJson(node)
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownId=777767f3-2e2a-4bd0-891b-fa92c1cad6b4&markdownType=HINT");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testUploadBase64ImageHintUrlWrongPath() {
        String base64Image = TestUtil.readTestFile("base64Url.txt");
        String filePath = "/media/png/Test2.png";
        JsonNode node = Json.parse("{\"base64Image\": \"" + base64Image + "\", \"filePath\": \"" + filePath +"\"}");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .bodyJson(node)
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownId=777767f3-2e2a-4bd0-891b-fa92c1cad6b4&markdownType=hint");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testUploadBase64ImageHintCorrectly() {
        String base64Image = TestUtil.readTestFile("base64Image.txt");
        String filePath = "\\\\media\\\\png\\\\Test.png";
        JsonNode node = Json.parse("{\"base64Image\": \"" + base64Image + "\", \"filePath\": \"" + filePath +"\"}");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .bodyJson(node)
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownId=777767f3-2e2a-4bd0-891b-fa92c1cad6b4&markdownType=hint");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testUploadBase64ImageHintUrlCorrectly() {
        String base64Image = TestUtil.readTestFile("base64Url.txt");
        String filePath = "\\\\media\\\\png\\\\Test.png";
        JsonNode node = Json.parse("{\"base64Image\": \"" + base64Image + "\", \"filePath\": \"" + filePath +"\"}");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .bodyJson(node)
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownId=777767f3-2e2a-4bd0-891b-fa92c1cad6b4&markdownType=hint");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testUploadBase64ImageInstructionWithoutBody() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownType=instruction");

        Result result = route(application, request);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void testUploadBase64ImageInstructionWithoutChallengeId() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .uri("/api/upload_image_b64?languageCode=en&markdownId=77771284-f716-4c21-b246-6422e3c4029f&markdownType=instruction");

        Result result = route(application, request);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void testUploadBase64ImageInstructionWithoutMarkdownId() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownType=instruction");

        Result result = route(application, request);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void testUploadBase64ImageInstructionWithoutMarkdownAndChallengeId() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .uri("/api/upload_image_b64?languageCode=en&markdownType=instruction");

        Result result = route(application, request);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void testUploadBase64ImageInstructionWrongPath() {
        String base64Image = TestUtil.readTestFile("base64Image.txt");
        String filePath = "/media/png/Test.png";
        JsonNode node = Json.parse("{\"base64Image\": \"" + base64Image + "\", \"filePath\": \"" + filePath +"\"}");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .bodyJson(node)
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownId=77771284-f716-4c21-b246-6422e3c4029f&markdownType=instruction");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testUploadBase64ImageInstructionUpperCaseMarkdownType() {
        String base64Image = TestUtil.readTestFile("base64Image.txt");
        String filePath = "/media/png/Test.png";
        JsonNode node = Json.parse("{\"base64Image\": \"" + base64Image + "\", \"filePath\": \"" + filePath +"\"}");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .bodyJson(node)
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownId=77771284-f716-4c21-b246-6422e3c4029f&markdownType=INSTRUCTION");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testUploadBase64ImageInstructionUrlWrongPath() {
        String base64Image = TestUtil.readTestFile("base64Url.txt");
        String filePath = "/media/png/Test.png";
        JsonNode node = Json.parse("{\"base64Image\": \"" + base64Image + "\", \"filePath\": \"" + filePath +"\"}");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .bodyJson(node)
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownId=77771284-f716-4c21-b246-6422e3c4029f&markdownType=instruction");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testUploadBase64ImageInstructionCorrectly() {
        String base64Image = TestUtil.readTestFile("base64Image.txt");
        String filePath = "\\\\media\\\\png\\\\Test.png";
        JsonNode node = Json.parse("{\"base64Image\": \"" + base64Image + "\", \"filePath\": \"" + filePath +"\"}");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .bodyJson(node)
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownId=77771284-f716-4c21-b246-6422e3c4029f&markdownType=instruction");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testUploadBase64ImageInstructionUrlCorrectly() {
        String base64Image = TestUtil.readTestFile("base64Url.txt");
        String filePath = "\\\\media\\\\png\\\\Test.png";
        JsonNode node = Json.parse("{\"base64Image\": \"" + base64Image + "\", \"filePath\": \"" + filePath +"\"}");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .bodyJson(node)
                .uri("/api/upload_image_b64?challengeId=77774bab-e068-463e-820c-c5f650198f08&languageCode=en&markdownId=77771284-f716-4c21-b246-6422e3c4029f&markdownType=instruction");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testRemoveImage() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(DELETE)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .uri("/api/removeImage?challengeId=77774bab-e068-463e-820c-c5f650198f08&mediaId=4");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testRemoveImageWithoutChallengeId() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(DELETE)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .uri("/api/removeImage?mediaId=5");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testRemoveImageNonExistingMediaId() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(DELETE)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .uri("/api/removeImage?mediaId=-1");

        Result result = route(application, request);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void testRemoveImageWithoutQueryString() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(DELETE)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .uri("/api/removeImage");

        Result result = route(application, request);
        assertEquals(BAD_REQUEST, result.status());
    }
}
