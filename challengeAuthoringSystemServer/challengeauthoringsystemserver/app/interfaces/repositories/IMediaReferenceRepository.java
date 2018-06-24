package interfaces.repositories;

import io.ebean.Finder;
import models.DatabaseObject.challenge.MediaReference;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface IMediaReferenceRepository {
    CompletionStage<Long> insert(MediaReference newMediaReference);
    CompletionStage<Optional<Long>> update(Long id, MediaReference mediaReference);
    CompletionStage<Optional<Long>> delete(Long id);
    Finder<Long, MediaReference> find();
}
