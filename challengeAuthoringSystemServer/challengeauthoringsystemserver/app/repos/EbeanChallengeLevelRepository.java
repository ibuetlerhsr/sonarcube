package repos;

import interfaces.repositories.IChallengeLevelRepository;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Finder;
import io.ebean.Transaction;
import models.DatabaseObject.challenge.ChallengeLevel;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class EbeanChallengeLevelRepository implements IChallengeLevelRepository {
    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public EbeanChallengeLevelRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<Long> insert(ChallengeLevel challengeLevel) {
        return supplyAsync(() -> {
            ebeanServer.insert(challengeLevel);
            return challengeLevel.getId();
        }, executionContext);
    }

    @Override
    public CompletionStage<Optional<Long>> update(Long id, ChallengeLevel newChallengeLevel) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<Long> value = Optional.empty();
            try {
                ChallengeLevel savedChallengeLevel = ebeanServer.find(ChallengeLevel.class).setId(id).findUnique();
                if (savedChallengeLevel != null) {
                    savedChallengeLevel.setMaxPoint(newChallengeLevel.getMaxPoint());
                    savedChallengeLevel.setText(newChallengeLevel.getText());
                    savedChallengeLevel.update();
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
                final Optional<ChallengeLevel> challengeLevelOptional = Optional.ofNullable(ebeanServer.find(ChallengeLevel.class).setId(id).findUnique());
                challengeLevelOptional.ifPresent(c -> c.delete());
                return challengeLevelOptional.map(c -> c.getId());
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    public Finder<Long, ChallengeLevel> find(){
        return new Finder<>(ChallengeLevel.class);
    }


}
