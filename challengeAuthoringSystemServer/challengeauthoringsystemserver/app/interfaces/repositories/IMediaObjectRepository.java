package interfaces.repositories;

import io.ebean.Finder;
import models.DatabaseObject.challenge.MediaObject;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface IMediaObjectRepository {
    CompletionStage<Long> insert(MediaObject newMediaObject);
    CompletionStage<Optional<Long>> update(Long id, MediaObject mediaObject);
    CompletionStage<Optional<Long>> delete(Long id);
    Finder<Long, MediaObject> find();
}
