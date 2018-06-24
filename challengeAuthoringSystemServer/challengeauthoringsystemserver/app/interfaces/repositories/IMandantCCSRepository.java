package interfaces.repositories;

import io.ebean.Finder;
import models.DatabaseObject.user.MandantCCS;
import models.DatabaseObject.user.UserCAS;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface IMandantCCSRepository {
    CompletionStage<Long> insert(MandantCCS mandantCCS);
    CompletionStage<Optional<Long>> update(Long id, MandantCCS newMandantCCS);
    CompletionStage<Optional<Long>>  delete(Long id);
    Finder<Long, MandantCCS> find();
}
