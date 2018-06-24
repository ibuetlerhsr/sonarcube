package interfaces.repositories;

import io.ebean.Finder;
import models.DatabaseObject.challenge.Rating;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface IRatingRepository {
    CompletionStage<Optional<Rating>> lookup(Long id);
    CompletionStage<Optional<Long>> update(Long id, Rating newRating);
    CompletionStage<Optional<Long>>  delete(Long id);
    CompletionStage<Long> insert(Rating rating);
    Finder<Long, Rating> find();
}
