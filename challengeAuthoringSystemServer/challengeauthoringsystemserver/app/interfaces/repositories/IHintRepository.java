package interfaces.repositories;

import io.ebean.Finder;
import models.DatabaseObject.challenge.Abstract;
import models.DatabaseObject.challenge.Hint;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface IHintRepository {
    CompletionStage<Optional<Hint>> lookup(String id);
    CompletionStage<Optional<String>> update(String id, Hint newHint);
    CompletionStage<Optional<String>>  delete(String id);
    CompletionStage<String> insert(Hint newHint);
    Finder<String, Hint> find();
}
