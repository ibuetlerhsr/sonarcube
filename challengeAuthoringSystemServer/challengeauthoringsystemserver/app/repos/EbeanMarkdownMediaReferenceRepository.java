package repos;

import interfaces.repositories.IMarkdownMediaReferenceRepository;
import interfaces.repositories.IMarkdownMediaReferenceRepository;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Finder;
import io.ebean.Transaction;
import models.DatabaseObject.challenge.MarkdownMediaReference;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class EbeanMarkdownMediaReferenceRepository implements IMarkdownMediaReferenceRepository {
    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public EbeanMarkdownMediaReferenceRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<Long> insert(MarkdownMediaReference newMarkdownMediaReference) {
        return supplyAsync(() -> {
            ebeanServer.insert(newMarkdownMediaReference);
            return newMarkdownMediaReference.getId();
        }, executionContext);
    }

    @Override
    public CompletionStage<Optional<Long>> update(Long id, MarkdownMediaReference newMarkdownMediaReference) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<Long> value = Optional.empty();
            try {
                MarkdownMediaReference savedMarkdownMediaReference = ebeanServer.find(MarkdownMediaReference.class).setId(id).findUnique();
                if (savedMarkdownMediaReference != null) {
                    savedMarkdownMediaReference.setMarkdownId(newMarkdownMediaReference.getMarkdownId());
                    savedMarkdownMediaReference.setMarkdownType(newMarkdownMediaReference.getMarkdownType());
                    savedMarkdownMediaReference.setMediaReference(newMarkdownMediaReference.getMediaReference());
                    newMarkdownMediaReference.update();
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
                final Optional<MarkdownMediaReference> markdownMediaReferenceOptional = Optional.ofNullable(ebeanServer.find(MarkdownMediaReference.class).setId(id).findUnique());
                markdownMediaReferenceOptional.ifPresent(c -> c.delete());
                return markdownMediaReferenceOptional.map(c -> c.getId());
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    public Finder<Long, MarkdownMediaReference> find(){
        return new Finder<>(MarkdownMediaReference.class);
    }


}
