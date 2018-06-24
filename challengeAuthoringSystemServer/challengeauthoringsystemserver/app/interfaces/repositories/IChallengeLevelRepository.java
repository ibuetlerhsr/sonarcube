package interfaces.repositories;

import io.ebean.Finder;
import models.DatabaseObject.challenge.ChallengeLevel;
import models.DatabaseObject.user.UserCAS;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface IChallengeLevelRepository {
    CompletionStage<Long> insert(ChallengeLevel challengeLevel);
    CompletionStage<Optional<Long>> update(Long id, ChallengeLevel newChallengeLevel);
    CompletionStage<Optional<Long>>  delete(Long id);
    Finder<Long, ChallengeLevel> find();
}
