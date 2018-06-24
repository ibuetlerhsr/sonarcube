package repos;

import interfaces.repositories.IChallengeTypeRepository;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Finder;
import io.ebean.Transaction;
import models.DatabaseObject.challenge.ChallengeType;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class EbeanChallengeTypeRepository implements IChallengeTypeRepository {
    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public EbeanChallengeTypeRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<Long> insert(ChallengeType challengeType) {
        return supplyAsync(() -> {
            ebeanServer.insert(challengeType);
            return challengeType.getId();
        }, executionContext);
    }

    @Override
    public CompletionStage<Optional<Long>> update(Long id, ChallengeType newChallengeType) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<Long> value = Optional.empty();
            try {
                ChallengeType savedChallengeType = ebeanServer.find(ChallengeType.class).setId(id).findUnique();
                if (savedChallengeType != null) {
                    savedChallengeType.setText(newChallengeType.getText());
                    savedChallengeType.update();
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
                final Optional<ChallengeType> challengeTypeOptional = Optional.ofNullable(ebeanServer.find(ChallengeType.class).setId(id).findUnique());
                challengeTypeOptional.ifPresent(c -> c.delete());
                return challengeTypeOptional.map(c -> c.getId());
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    public Finder<Long, ChallengeType> find(){
        return new Finder<>(ChallengeType.class);
    }


}
