package interfaces.repositories;

import io.ebean.Finder;
import models.DatabaseObject.challenge.ChallengeType;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface IChallengeTypeRepository {
    CompletionStage<Long> insert(ChallengeType challengeType);
    CompletionStage<Optional<Long>> update(Long id, ChallengeType newChallengeType);
    CompletionStage<Optional<Long>>  delete(Long id);
    Finder<Long, ChallengeType> find();
}
