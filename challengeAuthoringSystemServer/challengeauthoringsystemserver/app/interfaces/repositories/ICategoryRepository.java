package interfaces.repositories;

import io.ebean.Finder;
import models.DatabaseObject.challenge.Category;
import models.DatabaseObject.challenge.ChallengeLevel;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ICategoryRepository {
    CompletionStage<Long> insert(Category category);
    CompletionStage<Optional<Long>> update(Long id, Category newCategory);
    CompletionStage<Optional<Long>>  delete(Long id);
    Finder<Long, Category> find();
}
