package utils;

import models.DatabaseObject.user.UserCAS;
import play.Environment;
import play.Logger;
import services.UserService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DemoData {

    public UserCAS user1;
    public UserCAS mockUser;
    public UserCAS user2;
    private final UserService userService;

    @Inject
    public DemoData(Environment environment, UserService userService) {
        this.userService = userService;
        if (environment.isDev() || environment.isTest()) {
            if (userService.findByUsernameAndPassword("mock_user", "mock_pw") == null) {
                Logger.info("Loading Demo Data");

                mockUser = new UserCAS();
                mockUser.setUsername("mock_user");
                mockUser.setName("Mock");
                mockUser.setLanguageIsoCode("de");
                mockUser.save();

                user1 = new UserCAS();
                user1.setFirstName("User1");
                user1.setName("Test");
                user1.setLanguageIsoCode("de");
                user1.save();


                user2 = new UserCAS();
                user2.setFirstName("User2");
                user2.setName("Test");
                user2.setLanguageIsoCode("en");
                user2.save();
            }
        }
    }

}
