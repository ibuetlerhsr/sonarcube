package repos;

import interfaces.repositories.IMediaReferenceRepository;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Finder;
import io.ebean.Transaction;
import models.DatabaseObject.challenge.MediaReference;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class EbeanMediaReferenceRepository implements IMediaReferenceRepository {
    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public EbeanMediaReferenceRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<Long> insert(MediaReference newMediaReference) {
        return supplyAsync(() -> {
            ebeanServer.insert(newMediaReference);
            return newMediaReference.getId();
        }, executionContext);
    }

    @Override
    public CompletionStage<Optional<Long>> update(Long id, MediaReference newMediaReference) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<Long> value = Optional.empty();
            try {
                MediaReference savedMediaReference = ebeanServer.find(MediaReference.class).setId(id).findUnique();
                if (savedMediaReference != null) {
                    savedMediaReference.setUrl(newMediaReference.getUrl());
                    savedMediaReference.update();
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
                final Optional<MediaReference> mediaReferenceOptional = Optional.ofNullable(ebeanServer.find(MediaReference.class).setId(id).findUnique());
                mediaReferenceOptional.ifPresent(c -> c.delete());
                return mediaReferenceOptional.map(c -> c.getId());
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    public Finder<Long, MediaReference> find(){
        return new Finder<>(MediaReference.class);
    }


}
