package interfaces.repositories;

import io.ebean.Finder;
import models.DatabaseObject.challenge.ChallengeUsage;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface IChallengeUsageRepository {
    CompletionStage<Long> insert(ChallengeUsage challengeUsage);
    CompletionStage<Optional<Long>> update(Long id, ChallengeUsage newChallengeUsage);
    CompletionStage<Optional<Long>>  delete(Long id);
    Finder<Long, ChallengeUsage> find();
}
