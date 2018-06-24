package repos;

import interfaces.repositories.ISectionRepository;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Finder;
import io.ebean.Transaction;
import models.DatabaseObject.challenge.Section;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * A repository that executes database operations in a different
 * execution context.
 */
public class EbeanSectionRepository implements ISectionRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public EbeanSectionRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    public Finder<String, Section> find(){
        return new Finder<>(Section.class);
    }

    public CompletionStage<Optional<Section>> lookup(String id) {
        return supplyAsync(() -> {
            return Optional.ofNullable(ebeanServer.find(Section.class).setId(id).findUnique());
        }, executionContext);
    }

    public CompletionStage<Optional<String>> update(String id, Section newSection) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<String> value = Optional.empty();
            try {
                Section savedSection = ebeanServer.find(Section.class).setId(id).findUnique();
                if (savedSection != null) {
                    savedSection.setChallengeVersion(newSection.getChallengeVersion());
                    savedSection.setText(newSection.getText());
                    savedSection.update();
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
                final Optional<Section> sectionOptional = Optional.ofNullable(ebeanServer.find(Section.class).setId(id).findUnique());
                sectionOptional.ifPresent(c -> c.delete());
                return sectionOptional.map(c -> c.getId());
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    public CompletionStage<String> insert(Section newSection) {
        return supplyAsync(() -> {
             ebeanServer.insert(newSection);
             return newSection.getId();
        }, executionContext);
    }
}
