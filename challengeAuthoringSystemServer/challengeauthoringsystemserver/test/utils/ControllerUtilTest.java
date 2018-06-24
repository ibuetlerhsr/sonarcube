package utils;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Module;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.ApplicationLoader;
import play.Environment;
import play.inject.guice.GuiceApplicationBuilder;
import play.inject.guice.GuiceApplicationLoader;
import play.test.Helpers;


import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class ControllerUtilTest {
    @Inject
    Application application;
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
    public void testSimpleReadMetadataFile() {
        ControllerUtil.readMetadataFile("Category.txt");
        ControllerUtil.readMetadataFile("CodeType.txt");
        ControllerUtil.readMetadataFile("Keyword.txt");
        ControllerUtil.readMetadataFile("LanguageDefinition.txt");
        ControllerUtil.readMetadataFile("Level.txt");
        ControllerUtil.readMetadataFile("Type.txt");
        ControllerUtil.readMetadataFile("Usage.txt");
    }

    @Test
    public void testReadMetadataFileKeyword() {
        ArrayList<String> list = ControllerUtil.readMetadataFile("Level.txt");
        assertEquals(list.size(), 5);
    }
}
