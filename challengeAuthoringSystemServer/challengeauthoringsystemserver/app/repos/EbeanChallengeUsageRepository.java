package repos;

import interfaces.repositories.IChallengeUsageRepository;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Finder;
import io.ebean.Transaction;
import models.DatabaseObject.challenge.ChallengeUsage;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class EbeanChallengeUsageRepository implements IChallengeUsageRepository {
    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public EbeanChallengeUsageRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<Long> insert(ChallengeUsage challengeUsage) {
        return supplyAsync(() -> {
            ebeanServer.insert(challengeUsage);
            return challengeUsage.getId();
        }, executionContext);
    }

    @Override
    public CompletionStage<Optional<Long>> update(Long id, ChallengeUsage newChallengeUsage) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<Long> value = Optional.empty();
            try {
                ChallengeUsage savedChallengeUsage = ebeanServer.find(ChallengeUsage.class).setId(id).findUnique();
                if (savedChallengeUsage != null) {
                    savedChallengeUsage.setChallenges(newChallengeUsage.getChallenges());
                    savedChallengeUsage.update();
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
                final Optional<ChallengeUsage> challengeUsageOptional = Optional.ofNullable(ebeanServer.find(ChallengeUsage.class).setId(id).findUnique());
                challengeUsageOptional.ifPresent(c -> c.delete());
                return challengeUsageOptional.map(c -> c.getId());
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    public Finder<Long, ChallengeUsage> find(){
        return new Finder<>(ChallengeUsage.class);
    }


}
