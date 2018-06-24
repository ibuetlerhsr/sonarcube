package interfaces.repositories;

import io.ebean.Finder;
import io.ebean.PagedList;
import models.DatabaseObject.challenge.Challenge;
import models.DatabaseObject.challenge.ChallengeVersion;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface IChallengeRepository {
    CompletionStage<PagedList<Challenge>> page(int page, int pageSize, String sortBy, String order, String filter);
    CompletionStage<Optional<Challenge>> lookup(String id);
    CompletionStage<Optional<String>> update(String id, Challenge newChallengeData);
    CompletionStage<Optional<String>>  delete(String id);
    CompletionStage<String> insert(Challenge challenge);
    Finder<String, Challenge> find();
}
