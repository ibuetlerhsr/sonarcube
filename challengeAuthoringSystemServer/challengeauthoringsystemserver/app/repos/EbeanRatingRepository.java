package repos;

import interfaces.repositories.IRatingRepository;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Finder;
import io.ebean.Transaction;
import models.DatabaseObject.challenge.Rating;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * A repository that executes database operations in a different
 * execution context.
 */
public class EbeanRatingRepository implements IRatingRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public EbeanRatingRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    public Finder<Long, Rating> find(){
        return new Finder<>(Rating.class);
    }

    public CompletionStage<Optional<Rating>> lookup(Long id) {
        return supplyAsync(() -> {
            return Optional.ofNullable(ebeanServer.find(Rating.class).setId(id).findUnique());
        }, executionContext);
    }

    public CompletionStage<Optional<Long>> update(Long id, Rating newRating) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<Long> value = Optional.empty();
            try {
                Rating savedRating = ebeanServer.find(Rating.class).setId(id).findUnique();
                if (savedRating != null) {
                    savedRating.setChallengeVersion(newRating.getChallengeVersion());
                    savedRating.setText(newRating.getText());
                    savedRating.setCreatedBy(newRating.getCreatedBy());
                    savedRating.setValue(newRating.getValue());
                    savedRating.update();
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
                final Optional<Rating> RatingOptional = Optional.ofNullable(ebeanServer.find(Rating.class).setId(id).findUnique());
                RatingOptional.ifPresent(c -> c.delete());
                return RatingOptional.map(c -> c.getId());
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    public CompletionStage<Long> insert(Rating newRating ) {
        return supplyAsync(() -> {
             ebeanServer.insert(newRating);
             return newRating.getId();
        }, executionContext);
    }
}
