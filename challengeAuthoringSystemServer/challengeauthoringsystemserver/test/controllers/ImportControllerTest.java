package controllers;

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

public class ImportControllerTest extends WithApplication {

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
    }

    @After
    public void teardown() {
        Helpers.stop(application);
    }

    @Test
    public void testImportChallengeJsonWindows() {
        String testFileData = TestUtil.readTestFile("fatChallengeJson.json");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("auth_key", configuration.getString("challengeImport.apiKey"))
                .bodyJson(Json.parse(testFileData))
                .uri("/api/importChallenge");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testImportChallengeJsonUnix() {
        String testFileData = TestUtil.readTestFile("fatChallengeJsonUnix.json");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("auth_key", configuration.getString("challengeImport.apiKey"))
                .bodyJson(Json.parse(testFileData))
                .uri("/api/importChallenge");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testImportChallengeJsonNew() {
        String testFileData = TestUtil.readTestFile("fatChallengeJsonForMedia.json");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("auth_key", configuration.getString("challengeImport.apiKey"))
                .bodyJson(Json.parse(testFileData))
                .uri("/api/importChallenge");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testImportChallengeJsonSameTitle() {
        String testFileData = TestUtil.readTestFile("fatChallengeJsonForMedia.json");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("auth_key", configuration.getString("challengeImport.apiKey"))
                .bodyJson(Json.parse(testFileData))
                .uri("/api/importChallenge");

        Result result = route(application, request);
        assertEquals(OK, result.status());
    }
}
