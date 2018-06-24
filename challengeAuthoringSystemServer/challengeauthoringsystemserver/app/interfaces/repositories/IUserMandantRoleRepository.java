package interfaces.repositories;

import io.ebean.Finder;
import models.DatabaseObject.user.UserCAS;
import models.DatabaseObject.user.UserMandantRole;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface IUserMandantRoleRepository {
    CompletionStage<Long> insert(UserMandantRole userMandantRole);
    CompletionStage<Optional<Long>> update(Long id, UserMandantRole newUserMandantRole);
    CompletionStage<Optional<Long>>  delete(Long id);
    Finder<Long, UserMandantRole> find();
}
