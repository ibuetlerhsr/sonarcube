package repos;

import interfaces.repositories.IHintRepository;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Finder;
import io.ebean.Transaction;
import models.DatabaseObject.challenge.Hint;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * A repository that executes database operations in a different
 * execution context.
 */
public class EbeanHintRepository implements IHintRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public EbeanHintRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    public Finder<String, Hint> find(){
        return new Finder<>(Hint.class);
    }

    public CompletionStage<Optional<Hint>> lookup(String id) {
        return supplyAsync(() -> {
            return Optional.ofNullable(ebeanServer.find(Hint.class).setId(id).findUnique());
        }, executionContext);
    }

    public CompletionStage<Optional<String>> update(String id, Hint newHint) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<String> value = Optional.empty();
            try {
                Hint savedHint = ebeanServer.find(Hint.class).setId(id).findUnique();
                if (savedHint != null) {
                    savedHint.setText(newHint.getText());
                    savedHint.setSection(newHint.getSection());
                    savedHint.update();
                    txn.commit();
                    value = Optional.of(id);
                }
            } finally {
                txn.end();
            }
            return value;
        }, executionContext);
    }

    public CompletionStage<Optional<String>>  delete(String id) {
        return supplyAsync(() -> {
            try {
                final Optional<Hint> HintOptional = Optional.ofNullable(ebeanServer.find(Hint.class).setId(id).findUnique());
                HintOptional.ifPresent(c -> c.delete());
                return HintOptional.map(c -> c.getId());
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    public CompletionStage<String> insert(Hint newHint ) {
        return supplyAsync(() -> {
             ebeanServer.insert(newHint);
             return newHint.getId();
        }, executionContext);
    }
}
