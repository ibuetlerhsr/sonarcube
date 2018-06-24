package repos;

import interfaces.repositories.IMediaObjectRepository;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Finder;
import io.ebean.Transaction;
import models.DatabaseObject.challenge.MediaObject;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class EbeanMediaObjectRepository implements IMediaObjectRepository {
    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public EbeanMediaObjectRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<Long> insert(MediaObject newMediaObject) {
        return supplyAsync(() -> {
            ebeanServer.insert(newMediaObject);
            return newMediaObject.getId();
        }, executionContext);
    }

    @Override
    public CompletionStage<Optional<Long>> update(Long id, MediaObject newMediaObject) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<Long> value = Optional.empty();
            try {
                MediaObject savedMediaObject = ebeanServer.find(MediaObject.class).setId(id).findUnique();
                if (savedMediaObject != null) {
                    savedMediaObject.setIsoLanguageCode(newMediaObject.getIsoLanguageCode());
                    savedMediaObject.setContent(newMediaObject.getContent());
                    savedMediaObject.setMediaReference(newMediaObject.getMediaReference());
                    newMediaObject.update();
                    txn.commit();
                    value = Optional.of(id);
                }
            } finally {
                txn.end();
            }
            return value;
        }, executionContext);
    }

    @Override
    public CompletionStage<Optional<Long>>  delete(Long id) {
        return supplyAsync(() -> {
            try {
                final Optional<MediaObject> mediaObjectOptional = Optional.ofNullable(ebeanServer.find(MediaObject.class).setId(id).findUnique());
                mediaObjectOptional.ifPresent(c -> c.delete());
                return mediaObjectOptional.map(c -> c.getId());
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    public Finder<Long, MediaObject> find(){
        return new Finder<>(MediaObject.class);
    }


}
