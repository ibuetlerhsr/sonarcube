package repos;

import interfaces.repositories.IChallengeRepository;
import io.ebean.*;
import models.DatabaseObject.challenge.Challenge;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * A repository that executes database operations in a different
 * execution context.
 */
public class EbeanChallengeRepository implements IChallengeRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public EbeanChallengeRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    /**
     * Return a paged list of computer
     *
     * @param page     Page to display
     * @param pageSize Number of computers per page
     * @param sortBy   Computer property used for sorting
     * @param order    Sort order (either or asc or desc)
     * @param filter   Filter applied on the name column
     */
    public CompletionStage<PagedList<Challenge>> page(int page, int pageSize, String sortBy, String order, String filter) {
        return supplyAsync(() -> {
            return ebeanServer.find(Challenge.class).where()
                    .ilike("name", "%" + filter + "%")
                    .orderBy(sortBy + " " + order)
                    .fetch("challengeName")
                    .setFirstRow(page * pageSize)
                    .setMaxRows(pageSize)
                    .findPagedList();
        } , executionContext);
    }

    public Finder<String,Challenge> find(){
        return new Finder<>(Challenge.class);
    }

    public CompletionStage<Optional<Challenge>> lookup(String id) {
        return supplyAsync(() -> {
            return Optional.ofNullable(ebeanServer.find(Challenge.class).setId(id).findUnique());
        }, executionContext);
    }

    public CompletionStage<Optional<String>> update(String id, Challenge newChallengeData) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<String> value = Optional.empty();
            try {
                Challenge savedChallenge = ebeanServer.find(Challenge.class).setId(id).findUnique();
                if (savedChallenge != null) {
                    savedChallenge.setPrivate(newChallengeData.isPrivate());
                    savedChallenge.setUsages(newChallengeData.getUsages());
                    savedChallenge.setKeywords(newChallengeData.getKeywords());
                    savedChallenge.setCategories(newChallengeData.getCategories());
                    savedChallenge.setLastGitCommit(newChallengeData.getLastGitCommit());
                    savedChallenge.setLastUpdate(newChallengeData.getLastUpdate());
                    savedChallenge.setChallengeResources(newChallengeData.getChallengeResources());
                    savedChallenge.setGoldnuggetType(newChallengeData.getGoldnuggetType());
                    savedChallenge.setType(newChallengeData.getType());
                    savedChallenge.setMandant(newChallengeData.getMandant());
                    savedChallenge.setCategories(newChallengeData.getCategories());
                    savedChallenge.update();
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
                final Optional<Challenge> challengeOptional = Optional.ofNullable(ebeanServer.find(Challenge.class).setId(id).findUnique());
                challengeOptional.ifPresent(c -> c.delete());
                return challengeOptional.map(c -> c.getId());
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    public CompletionStage<String> insert(Challenge challenge ) {
        return supplyAsync(() -> {
             ebeanServer.insert(challenge);
             return challenge.getId();
        }, executionContext);
    }
}
