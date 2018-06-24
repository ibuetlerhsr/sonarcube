package services;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.ApplicationLoader.Context;
import play.Environment;
import play.inject.guice.GuiceApplicationBuilder;
import play.inject.guice.GuiceApplicationLoader;
import play.test.Helpers;
import utils.TestUtil;

import static org.junit.Assert.*;

import javax.inject.Inject;

public class MediaServiceTest {
    @Inject private MediaService service;
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
    public void testExctractFileName() {
        String path = "media/png/globus.png";
        String fileName = (String) TestUtil.genericInvokeMethod(this.service, "extractFileName", 1, path);
        assertEquals("globus", fileName);
    }

    @Test
    public void testExctractFileNameTwo() {
        String path = "media\\png\\globus.png";
        String fileName = (String) TestUtil.genericInvokeMethod(this.service, "extractFileName", 1, path);
        assertEquals("globus", fileName);
    }

    @Test
    public void testExtractFileNameWrong() {
        String path = null;
        String fileName = (String) TestUtil.genericInvokeMethod(this.service, "extractFileName", 1, path);
        assertNull(fileName);
    }


    @Test
    public void testExtractFileExtension() {
        String path = "media/png/globus.png";
        String fileExtension = (String) TestUtil.genericInvokeMethod(this.service, "extractFileExtension", 1, path);
        assertEquals("png", fileExtension);
    }

    @Test
    public void testExtractFileExtensionTwo() {
        String path = "media\\png\\globus.png";
        String fileExtension = (String) TestUtil.genericInvokeMethod(this.service, "extractFileExtension", 1, path);
        assertEquals("png", fileExtension);
    }

    @Test
    public void testExtractFileExtensionWrong() {
        String path = null;
        String fileName = (String) TestUtil.genericInvokeMethod(this.service, "extractFileExtension", 1, path);
        assertNull(fileName);
    }

    @Test
    public void testWriteMediaObjectToFile() {
        String testFileContent = TestUtil.readTestFile("base64Image.txt");
        assertNotNull(service.writeMediaObjectToFile("/media/globus.png", testFileContent));
    }

    @Test
    public void testWriteMediaObjectToFileTwo() {
        String testFileContent = TestUtil.readTestFile("base64Image.txt");
        assertNotNull(service.writeMediaObjectToFile("\\media\\globus.png", testFileContent));
    }

}
