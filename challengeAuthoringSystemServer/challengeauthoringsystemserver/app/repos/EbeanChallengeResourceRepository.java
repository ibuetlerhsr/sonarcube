package repos;

import interfaces.repositories.IChallengeResourceRepository;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Finder;
import io.ebean.Transaction;
import models.DatabaseObject.challenge.ChallengeResource;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class EbeanChallengeResourceRepository implements IChallengeResourceRepository {
    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public EbeanChallengeResourceRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<String> insert(ChallengeResource challengeResource) {
        return supplyAsync(() -> {
            ebeanServer.insert(challengeResource);
            return challengeResource.getId();
        }, executionContext);
    }

    @Override
    public CompletionStage<Optional<String>> update(String id, ChallengeResource challengeResource) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<String> value = Optional.empty();
            try {
                ChallengeResource savedChallengeResource = ebeanServer.find(ChallengeResource.class).setId(id).findUnique();
                if (savedChallengeResource != null) {
                    savedChallengeResource.setType(challengeResource.getType());
                    savedChallengeResource.setChallenges(challengeResource.getChallenges());
                    savedChallengeResource.update();
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
    public CompletionStage<Optional<String>>  delete(String id) {
        return supplyAsync(() -> {
            try {
                final Optional<ChallengeResource> challengeResourceOptional = Optional.ofNullable(ebeanServer.find(ChallengeResource.class).setId(id).findUnique());
                challengeResourceOptional.ifPresent(c -> c.delete());
                return challengeResourceOptional.map(c -> c.getId());
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    public Finder<String, ChallengeResource> find(){
        return new Finder<>(ChallengeResource.class);
    }
}
