package repos;

import interfaces.repositories.IChallengeVersionRepository;
import io.ebean.*;
import models.DatabaseObject.challenge.ChallengeVersion;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class EbeanChallengeVersionRepository implements IChallengeVersionRepository{
    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public EbeanChallengeVersionRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
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
    public CompletionStage<PagedList<ChallengeVersion>> page(int page, int pageSize, String sortBy, String order, String filter) {
        return supplyAsync(() -> {
            return ebeanServer.find(ChallengeVersion.class).where()
                    .ilike("name", "%" + filter + "%")
                    .orderBy(sortBy + " " + order)
                    .fetch("challengeTitel")
                    .setFirstRow(page * pageSize)
                    .setMaxRows(pageSize)
                    .findPagedList();
        } , executionContext);
    }

    public Finder<Long,ChallengeVersion> find(){
        return new Finder<>(ChallengeVersion.class);
    }

    public CompletionStage<Optional<ChallengeVersion>> lookup(Long id) {
        return supplyAsync(() -> {
            return Optional.ofNullable(ebeanServer.find(ChallengeVersion.class).setId(id).findUnique());
        }, executionContext);
    }

    public CompletionStage<Optional<Long>> update(Long id, ChallengeVersion newChallengeVersionData) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<Long> value = Optional.empty();
            try {
                ChallengeVersion savedChallengeVersion = ebeanServer.find(ChallengeVersion.class).setId(id).findUnique();
                if (savedChallengeVersion != null) {
                    savedChallengeVersion.setName(newChallengeVersionData.getName());
                    savedChallengeVersion.setTitle(newChallengeVersionData.getTitle());
                    savedChallengeVersion.setAnAbstract(newChallengeVersionData.getAnAbstract());
                    savedChallengeVersion.setSolution(newChallengeVersionData.getSolution());
                    savedChallengeVersion.setAuthor(newChallengeVersionData.getAuthor());
                    if(newChallengeVersionData.getCreated() != null)
                        savedChallengeVersion.setCreated(newChallengeVersionData.getCreated());
                    else
                        savedChallengeVersion.setCreated(new Timestamp(new Date().getTime()));
                    savedChallengeVersion.setStaticGoldnuggetSecret(newChallengeVersionData.getStaticGoldnuggetSecret());
                    savedChallengeVersion.setChallenge(newChallengeVersionData.getChallenge());
                    savedChallengeVersion.setChallengeStatus(newChallengeVersionData.getChallengeStatus());
                    savedChallengeVersion.setChallengeLevel(newChallengeVersionData.getChallengeLevel());
                    savedChallengeVersion.setMaxPoints(newChallengeVersionData.getMaxPoints());
                    savedChallengeVersion.setMediaReference(newChallengeVersionData.getMediaReference());
                    savedChallengeVersion.update();
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
                final Optional<ChallengeVersion> challengeVersionOptional = Optional.ofNullable(ebeanServer.find(ChallengeVersion.class).setId(id).findUnique());
                challengeVersionOptional.ifPresent(c -> c.delete());
                return challengeVersionOptional.map(c -> c.getId());
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    public CompletionStage<Long> insert(ChallengeVersion challengeVersion ) {
        return supplyAsync(() -> {
            ebeanServer.insert(challengeVersion);
            return challengeVersion.getId();
        }, executionContext);
    }
}
