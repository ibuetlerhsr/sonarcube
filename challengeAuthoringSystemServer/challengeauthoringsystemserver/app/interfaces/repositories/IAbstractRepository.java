package interfaces.repositories;

import io.ebean.Finder;
import io.ebean.PagedList;
import models.DatabaseObject.challenge.Abstract;
import models.DatabaseObject.challenge.Challenge;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface IAbstractRepository {
    CompletionStage<Optional<Abstract>> lookup(Long id);
    CompletionStage<Optional<Long>> update(Long id, Abstract newAbstract);
    CompletionStage<Optional<Long>>  delete(Long id);
    CompletionStage<Long> insert(Abstract newAbstract);
    Finder<Long, Abstract> find();
}
