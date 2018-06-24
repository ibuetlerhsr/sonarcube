package repos;

import interfaces.repositories.ITranslatableAttributeRepository;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Finder;
import models.DatabaseObject.translation.TranslatableAttribute;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class EbeanTranslatableAttributeRepository implements ITranslatableAttributeRepository {
    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public EbeanTranslatableAttributeRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<Long> insert(TranslatableAttribute translatableAttribute) {
        return supplyAsync(() -> {
            ebeanServer.insert(translatableAttribute);
            return translatableAttribute.getId();
        }, executionContext);
    }

    @Override
    public CompletionStage<Optional<Long>>  delete(Long id) {
        return supplyAsync(() -> {
            try {
                final Optional<TranslatableAttribute> translatableAttributeOptional = Optional.ofNullable(ebeanServer.find(TranslatableAttribute.class).setId(id).findUnique());
                translatableAttributeOptional.ifPresent(c -> c.delete());
                return translatableAttributeOptional.map(c -> c.getId());
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    public Finder<Long, TranslatableAttribute> find(){
        return new Finder<>(TranslatableAttribute.class);
    }


}
