package interfaces.repositories;

import io.ebean.Finder;
import models.DatabaseObject.challenge.ChallengeResource;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface IChallengeResourceRepository {
    CompletionStage<Optional<String>> update(String id, ChallengeResource newChallengeResource);
    CompletionStage<Optional<String>>  delete(String id);
    CompletionStage<String> insert(ChallengeResource newChallengeResource);
    Finder<String, ChallengeResource> find();
}
