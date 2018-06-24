package models.DatabaseObject;

import services.ChallengeService;
import services.TranslationService;
import services.UserService;

import javax.inject.Inject;

public class InitialDBState {
    @Inject
    public InitialDBState(TranslationService translationService,
                          UserService userService,
                          ChallengeService challengeService){
        translationService.intializeTranslationDbValues();
        userService.intializeUserDbValues();
        challengeService.intializeChallengeDbValues();
    }
}
