package repos;

import interfaces.repositories.ISolutionRepository;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Finder;
import io.ebean.Transaction;
import models.DatabaseObject.challenge.Solution;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * A repository that executes database operations in a different
 * execution context.
 */
public class EbeanSolutionRepository implements ISolutionRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public EbeanSolutionRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    public Finder<Long, Solution> find(){
        return new Finder<>(Solution.class);
    }

    public CompletionStage<Optional<Solution>> lookup(Long id) {
        return supplyAsync(() -> {
            return Optional.ofNullable(ebeanServer.find(Solution.class).setId(id).findUnique());
        }, executionContext);
    }

    public CompletionStage<Optional<Long>> update(Long id, Solution newSolution) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<Long> value = Optional.empty();
            try {
                Solution savedSolution = ebeanServer.find(Solution.class).setId(id).findUnique();
                if (savedSolution != null) {
                    savedSolution.setText(newSolution.getText());
                    savedSolution.update();
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
                final Optional<Solution> solutionOptional = Optional.ofNullable(ebeanServer.find(Solution.class).setId(id).findUnique());
                solutionOptional.ifPresent(c -> c.delete());
                return solutionOptional.map(c -> c.getId());
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    public CompletionStage<Long> insert(Solution newSolution ) {
        return supplyAsync(() -> {
             ebeanServer.insert(newSolution);
             return newSolution.getId();
        }, executionContext);
    }
}
