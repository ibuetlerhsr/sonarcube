package modules;

import com.google.inject.AbstractModule;
import interfaces.repositories.*;
import play.libs.akka.AkkaGuiceSupport;
import repos.*;


public class ChallengeRepositoriesModule extends AbstractModule implements AkkaGuiceSupport {
    @Override
    protected void configure() {
        bind(IChallengeRepository.class).
                to(EbeanChallengeRepository.class);
        bind(IChallengeVersionRepository.class).
                to(EbeanChallengeVersionRepository.class);
        bind(IInstructionRepository.class).
                to(EbeanInstructionRepository.class);
        bind(ISectionRepository.class).
                to(EbeanSectionRepository.class);
        bind(IHintRepository.class).
                to(EbeanHintRepository.class);
        bind(IAbstractRepository.class).
                to(EbeanAbstractRepository.class);
        bind(ISolutionRepository.class).
                to(EbeanSolutionRepository.class);
        bind(IRatingRepository.class).
                to(EbeanRatingRepository.class);
        bind(IChallengeLevelRepository.class).
                to(EbeanChallengeLevelRepository.class);
        bind(IChallengeTypeRepository.class).
                to(EbeanChallengeTypeRepository.class);
        bind(ICategoryRepository.class).
                to(EbeanCategoryRepository.class);
        bind(IChallengeUsageRepository.class).
                to(EbeanChallengeUsageRepository.class);
        bind(IKeywordRepository.class).
                to(EbeanKeywordRepository.class);
        bind(IChallengeResourceRepository.class).
                to(EbeanChallengeResourceRepository.class);
    }
}