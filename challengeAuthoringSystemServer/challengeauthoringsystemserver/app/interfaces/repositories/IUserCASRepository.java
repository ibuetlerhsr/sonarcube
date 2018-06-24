package interfaces.repositories;

import io.ebean.Finder;
import models.DatabaseObject.user.UserCAS;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface IUserCASRepository {
    CompletionStage<Long> insert(UserCAS userCAS);
    CompletionStage<Optional<Long>> update(Long id, UserCAS newUserCAS);
    CompletionStage<Optional<Long>>  delete(Long id);
    Finder<Long, UserCAS> find();
}
