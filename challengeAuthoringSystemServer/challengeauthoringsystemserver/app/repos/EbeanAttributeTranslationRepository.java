package repos;

import interfaces.repositories.IAttributeTranslationRepository;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Finder;
import io.ebean.Transaction;
import models.DatabaseObject.translation.AttributeTranslation;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class EbeanAttributeTranslationRepository implements IAttributeTranslationRepository {
    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public EbeanAttributeTranslationRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<Long> insert(AttributeTranslation attributeTranslation) {
        return supplyAsync(() -> {
            ebeanServer.insert(attributeTranslation);
            return attributeTranslation.getId();
        }, executionContext);
    }

    @Override
    public CompletionStage<Optional<Long>> update(Long id, AttributeTranslation newAttributeTranslation) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<Long> value = Optional.empty();
            try {
                AttributeTranslation savedAttributeTranslation = ebeanServer.find(AttributeTranslation.class).setId(id).findUnique();
                if (savedAttributeTranslation != null) {
                    savedAttributeTranslation.setTranslation(newAttributeTranslation.getTranslation());
                    savedAttributeTranslation.setUserCreated(newAttributeTranslation.isUserCreated());
                    savedAttributeTranslation.update();
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
                final Optional<AttributeTranslation> attributeTranslationOptional = Optional.ofNullable(ebeanServer.find(AttributeTranslation.class).setId(id).findUnique());
                attributeTranslationOptional.ifPresent(c -> c.delete());
                return attributeTranslationOptional.map(c -> c.getId());
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    public Finder<Long, AttributeTranslation> find(){
        return new Finder<>(AttributeTranslation.class);
    }
}
