package interfaces.repositories;

import io.ebean.Finder;
import models.DatabaseObject.challenge.Abstract;
import models.DatabaseObject.challenge.Keyword;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface IKeywordRepository {
    CompletionStage<Optional<Long>> update(Long id, Keyword newKeyword);
    CompletionStage<Optional<Long>>  delete(Long id);
    CompletionStage<Long> insert(Keyword newKeyword);
    Finder<Long, Keyword> find();
}
