package services;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import models.DatabaseObject.challenge.Category;
import models.DatabaseObject.challenge.Challenge;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.ApplicationLoader;
import play.Environment;
import play.inject.guice.GuiceApplicationBuilder;
import play.inject.guice.GuiceApplicationLoader;
import play.test.Helpers;
import utils.TestUtil;

import javax.inject.Inject;
import java.util.List;

public class DataTransferServiceTest {
    @Inject
    private DataTransferService transferService;
    @Inject
    private ChallengeService challengeService;
    @Inject private Application application;

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

    //region trimCategoryStringPartsTest-Method
    @Test
    public void testTrimCategoryStringPart() {
        String categoryString = "; SecondCategory";
        String trimmedValue = (String) TestUtil.genericInvokeMethod(this.transferService, "trimCategoryStringPart", 1, categoryString);
        if(trimmedValue.equals("SecondCategory"))
            assert true;
        else
            assert false;

        categoryString = ";SecondCategory";
        trimmedValue = (String) TestUtil.genericInvokeMethod(this.transferService, "trimCategoryStringPart", 1, categoryString);
        if(trimmedValue.equals("SecondCategory"))
            assert true;
        else
            assert false;

        categoryString = ";       SecondCategory            ";
        trimmedValue = (String) TestUtil.genericInvokeMethod(this.transferService, "trimCategoryStringPart", 1, categoryString);
        if(trimmedValue.equals("SecondCategory"))
            assert true;
        else
            assert false;

        categoryString = ";      Second Catego ry         ";
        trimmedValue = (String) TestUtil.genericInvokeMethod(this.transferService, "trimCategoryStringPart", 1, categoryString);
        if(trimmedValue.equals("Second Catego ry"))
            assert true;
        else
            assert false;

        categoryString = "             ;      Second Catego ry         ";
        trimmedValue = (String) TestUtil.genericInvokeMethod(this.transferService, "trimCategoryStringPart", 1, categoryString);
        if(trimmedValue.equals("Second Catego ry"))
            assert true;
        else
            assert false;
    }
    //endregion

    //region handleImportCategoryTest-Method
    @Test
    public void testHandleImportCategoryThreeSemicolon() {
        Challenge newChallenge = this.challengeService.createChallengeById("klsdfheiuhaheerl1");

        String categoryString = "FirstTestCategory; SecondCategory; ThirdCategory;";

        TestUtil.genericInvokeMethod(this.transferService, "handleImportCategory", 2, newChallenge, categoryString);

        List<Category> categories = this.challengeService.getCategoriesByChallengeId(newChallenge.getId());
        if(categories != null && categories.size()==3)
            assert true;
        else
            assert false;
    }

    @Test
    public void testHandleImportCategoryThreeSemicolonWithoutSpaces() {
        Challenge newChallenge = this.challengeService.createChallengeById("klsdfheiuhaheerl2");

        String categoryString = "FirstTestCategory;SecondCategory;ThirdCategory;";

        TestUtil.genericInvokeMethod(this.transferService, "handleImportCategory", 2, newChallenge, categoryString);

        List<Category> categories = this.challengeService.getCategoriesByChallengeId(newChallenge.getId());
        if(categories != null && categories.size()==3)
            assert true;
        else
            assert false;
    }

    @Test
    public void testHandleImportCategoryTwoSemicolon() {
        Challenge newChallenge = this.challengeService.createChallengeById("klsdfheiuhaheerl3");
        String categoryString = "FirstTestCategory; SecondCategory;";

        TestUtil.genericInvokeMethod(this.transferService, "handleImportCategory", 2, newChallenge, categoryString);

        List<Category> categories = this.challengeService.getCategoriesByChallengeId(newChallenge.getId());
        if(categories != null && categories.size()==2)
            assert true;
        else
            assert false;
    }

    @Test
    public void testHandleImportCategoryTwoSemicolonWithoutSpaces() {
        Challenge newChallenge = this.challengeService.createChallengeById("klsdfheiuhaheerl4");

        String categoryString = "FirstTestCategory;SecondCategory;";
        TestUtil.genericInvokeMethod(this.transferService, "handleImportCategory", 2, newChallenge, categoryString);


        List<Category> categories = this.challengeService.getCategoriesByChallengeId(newChallenge.getId());
        if(categories != null && categories.size()==2)
            assert true;
        else
            assert false;
    }

    @Test
    public void testHandleImportCategoryOneSemicolonWithoutSpaces() {
        Challenge newChallenge = this.challengeService.createChallengeById("klsdfheiuhaheerl4");

        String categoryString = "FirstTestCategory;SecondCategory";
        TestUtil.genericInvokeMethod(this.transferService, "handleImportCategory", 2, newChallenge, categoryString);


        List<Category> categories = this.challengeService.getCategoriesByChallengeId(newChallenge.getId());
        if(categories != null && categories.size()==2)
            assert true;
        else
            assert false;
    }

    @Test
    public void testHandleImportCategoryOneSemicolon() {
        Challenge newChallenge = this.challengeService.createChallengeById("klsdfheiuhaheerl4");

        String categoryString = "FirstTestCategory; SecondCategory";
        TestUtil.genericInvokeMethod(this.transferService, "handleImportCategory", 2, newChallenge, categoryString);


        List<Category> categories = this.challengeService.getCategoriesByChallengeId(newChallenge.getId());
        if(categories != null && categories.size()==2)
            assert true;
        else
            assert false;
    }

    @Test
    public void testHandleImportCategorySingleCategory() {
        Challenge newChallenge = this.challengeService.createChallengeById("klsdfheiuhaheerl5");

        String categoryString = "FirstTestCategory";

        TestUtil.genericInvokeMethod(this.transferService, "handleImportCategory", 2, newChallenge, categoryString);

        List<Category> categories = this.challengeService.getCategoriesByChallengeId(newChallenge.getId());
        if(categories != null && categories.size()==1)
            assert true;
        else
            assert false;
    }
    //endregion

}
