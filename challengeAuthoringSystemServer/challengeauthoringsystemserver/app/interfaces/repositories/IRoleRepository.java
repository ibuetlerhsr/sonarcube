package interfaces.repositories;

import io.ebean.Finder;
import models.DatabaseObject.user.Message;
import models.DatabaseObject.user.Role;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface IRoleRepository {
    CompletionStage<Long> insert(Role role);
    CompletionStage<Optional<Long>> update(Long id, Role newRole);
    CompletionStage<Optional<Long>>  delete(Long id);
    Finder<Long, Role> find();
}
