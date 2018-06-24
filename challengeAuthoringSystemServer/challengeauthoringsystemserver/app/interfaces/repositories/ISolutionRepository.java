package interfaces.repositories;

import io.ebean.Finder;
import models.DatabaseObject.challenge.Solution;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ISolutionRepository {
    CompletionStage<Optional<Solution>> lookup(Long id);
    CompletionStage<Optional<Long>> update(Long id, Solution newSolution);
    CompletionStage<Optional<Long>>  delete(Long id);
    CompletionStage<Long> insert(Solution newSolution);
    Finder<Long, Solution> find();
}
