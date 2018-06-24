package interfaces.repositories;

import io.ebean.Finder;
import io.ebean.PagedList;
import models.DatabaseObject.challenge.ChallengeVersion;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface IChallengeVersionRepository {
    CompletionStage<PagedList<ChallengeVersion>> page(int page, int pageSize, String sortBy, String order, String filter);
    CompletionStage<Optional<ChallengeVersion>> lookup(Long id);
    CompletionStage<Optional<Long>> update(Long id, ChallengeVersion newChallengeVersionData);
    CompletionStage<Optional<Long>>  delete(Long id);
    CompletionStage<Long> insert(ChallengeVersion challengeVersion);
    Finder<Long, ChallengeVersion> find();
}
