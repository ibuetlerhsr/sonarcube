package repos;

import interfaces.repositories.IAbstractRepository;
import io.ebean.*;
import models.DatabaseObject.challenge.Abstract;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * A repository that executes database operations in a different
 * execution context.
 */
public class EbeanAbstractRepository implements IAbstractRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public EbeanAbstractRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    public Finder<Long, Abstract> find(){
        return new Finder<>(Abstract.class);
    }

    public CompletionStage<Optional<Abstract>> lookup(Long id) {
        return supplyAsync(() -> {
            return Optional.ofNullable(ebeanServer.find(Abstract.class).setId(id).findUnique());
        }, executionContext);
    }

    public CompletionStage<Optional<Long>> update(Long id, Abstract newAbstract) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<Long> value = Optional.empty();
            try {
                Abstract savedAbstract = ebeanServer.find(Abstract.class).setId(id).findUnique();
                if (savedAbstract != null) {
                    savedAbstract.setText(newAbstract.getText());
                    savedAbstract.update();
                    txn.commit();
                    value = Optional.of(id);
                }
            } finally {
                txn.end();
            }
            return value;
        }, executionContext);
    }

    public CompletionStage<Optional<Long>>  delete(Long id) {
        return supplyAsync(() -> {
            try {
                final Optional<Abstract> abstractOptional = Optional.ofNullable(ebeanServer.find(Abstract.class).setId(id).findUnique());
                abstractOptional.ifPresent(c -> c.delete());
                return abstractOptional.map(c -> c.getId());
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    public CompletionStage<Long> insert(Abstract newAbstract ) {
        return supplyAsync(() -> {
             ebeanServer.insert(newAbstract);
             return newAbstract.getId();
        }, executionContext);
    }
}
