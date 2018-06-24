package services;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import models.DatabaseObject.challenge.Category;
import models.DatabaseObject.challenge.Challenge;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.Environment;
import play.inject.guice.GuiceApplicationLoader;
import play.ApplicationLoader.Context;
import play.test.Helpers;
import utils.TestUtil;
import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.running;

import play.Application;
import play.inject.guice.GuiceApplicationBuilder;

import java.util.List;

public class ChallengeServiceTest{
    @Inject private ChallengeService service;
    @Inject private Application application;

    @Before
    public void setup() {
        Module testModule = new AbstractModule() {
            @Override
            public void configure() {
            }
        };

        GuiceApplicationBuilder builder = new GuiceApplicationLoader()
                .builder(new Context(Environment.simple()))
                .overrides(testModule);
        Guice.createInjector(builder.applicationModule()).injectMembers(this);

        Helpers.start(application);
    }

    @After
    public void teardown() {
        Helpers.stop(application);
    }

    @Test
    public void testGetFilteredChallengeMetadata() {
        assertTrue(service.getFilteredChallengesMetadata("30.06.2018", "02.07.2018").size() >= 1);
    }

    @Test
    public void testGetFilteredChallengeMetadataFromDateNull() {
        assertTrue(service.getFilteredChallengesMetadata(null, "02.07.2018").size() >= 0);
    }

    @Test
    public void testGetFilteredChallengeMetadataToDateNull() {
        assertTrue(service.getFilteredChallengesMetadata("02.07.2018", null).size() == 0);
    }

    @Test
    public void testGetFilteredChallengeMetadataFromDateEmpty() {
        assertTrue(service.getFilteredChallengesMetadata("", "02.07.2018").size() >= 0);
    }

    @Test
    public void testGetFilteredChallengeMetadataToDatenEmpty() {
        assertTrue(service.getFilteredChallengesMetadata("02.07.2018", "").size() == 0);
    }
}
