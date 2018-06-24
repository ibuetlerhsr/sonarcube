package controllers;

import akka.stream.Materializer;
import akka.stream.impl.io.OutputStreamSink;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.typesafe.config.Config;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.ApplicationLoader;
import play.Environment;
import play.core.j.JavaResultExtractor;
import play.http.HttpEntity;
import play.inject.guice.GuiceApplicationBuilder;
import play.inject.guice.GuiceApplicationLoader;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;
import utils.TestUtil;

import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

public class ChallengeControllerTest extends WithApplication {

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

        String testFileData = TestUtil.readTestFile("fatChallengeJson.json");
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
    public void testIndex() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .header("X-Auth-Roles", configuration.getString("authorized.defaultRoles"))
                .header("X-Auth-Username", "test_user")
                .uri("/api/consumers");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testGetNewChallengeId() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .header("X-Auth-Roles", configuration.getString("authorized.defaultRoles"))
                .header("X-Auth-Username", "test_user")
                .uri("/api/challenge/getNewChallengeId");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testGetNewSectionId() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .uri("/api/section/getNewSectionId?challengeId=24634bab-e068-463e-820c-c5f650198f08");

        Result result = route(application, request);
        assertEquals(OK, result.status());
        JsonNode bodyJson = Json.parse(contentAsString(result));
        String sectionId = bodyJson.get("sectionId").asText();
        assertTrue(sectionId != null);
    }

    @Test
    public void testGetNewSectionIdWrong() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .uri("/api/section/getNewSectionId");

        Result result = route(application, request);
        assertEquals(400, result.status());
    }

    @Test
    public void testGetNewInstructionId() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .uri("/api/step/getNewInstructionId?challengeId=24634bab-e068-463e-820c-c5f650198f08&sectionId=2d4fcfb3-1ee7-4a16-866a-051fd985cf07");

        Result result = route(application, request);
        assertEquals(OK, result.status());
        JsonNode bodyJson = Json.parse(contentAsString(result));
        String instructionId = bodyJson.get("instructionId").asText();
        String sectionId = bodyJson.get("sectionId").asText();
        assertTrue(instructionId != null);
        assertEquals("2d4fcfb3-1ee7-4a16-866a-051fd985cf07", sectionId);
    }

    @Test
    public void testGetNewInstructionIdWrong() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .uri("/api/step/getNewInstructionId");

        Result result = route(application, request);
        assertEquals(400, result.status());
    }

    @Test
    public void testGetNewHintId() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .uri("/api/step/getNewHintId?challengeId=24634bab-e068-463e-820c-c5f650198f08&sectionId=2d4fcfb3-1ee7-4a16-866a-051fd985cf07");

        Result result = route(application, request);
        assertEquals(OK, result.status());
        JsonNode bodyJson = Json.parse(contentAsString(result));
        String hintId = bodyJson.get("hintId").asText();
        String sectionId = bodyJson.get("sectionId").asText();
        assertTrue(hintId != null);
        assertEquals("2d4fcfb3-1ee7-4a16-866a-051fd985cf07", sectionId);
    }

    @Test
    public void testGetNewHintIdWrong() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .uri("/api/step/getNewHintId");

        Result result = route(application, request);
        assertEquals(400, result.status());
    }

    @Test
    public void testGetNewSolutionId() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .uri("/api/challenge/getNewSolutionId?challengeId=24634bab-e068-463e-820c-c5f650198f08");

        Result result = route(application, request);
        assertEquals(OK, result.status());
        JsonNode bodyJson = Json.parse(contentAsString(result));
        String solutionIdString = bodyJson.get("solutionId").asText();
        Long solutionId = Long.parseLong(solutionIdString);
        assertTrue(solutionId >= 0L);
    }

    @Test
    public void testGetNewSolutionIdWrong() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .uri("/api/challenge/getNewSolutionId");

        Result result = route(application, request);
        assertEquals(400, result.status());
    }

    @Test
    public void testGetNewAbstractId() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .uri("/api/challenge/getNewAbstractId?challengeId=24634bab-e068-463e-820c-c5f650198f08");

        Result result = route(application, request);
        assertEquals(OK, result.status());
        JsonNode bodyJson = Json.parse(contentAsString(result));
        String abstractIdString = bodyJson.get("abstractId").asText();
        Long abstractId = Long.parseLong(abstractIdString);
        assertTrue(abstractId >= 0L);
    }

    @Test
    public void testGetNewAbstractIdWrong() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .header("X-Auth-Roles", configuration.getString("authorized.editorRoles"))
                .header("X-Auth-Username", "test_user")
                .uri("/api/challenge/getNewAbstractId");

        Result result = route(application, request);
        assertEquals(400, result.status());
    }
}
