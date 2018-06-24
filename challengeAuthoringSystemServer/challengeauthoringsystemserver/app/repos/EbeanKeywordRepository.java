package repos;

import interfaces.repositories.IKeywordRepository;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Finder;
import io.ebean.Transaction;
import models.DatabaseObject.challenge.Keyword;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class EbeanKeywordRepository implements IKeywordRepository {
    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public EbeanKeywordRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<Long> insert(Keyword keyword) {
        return supplyAsync(() -> {
            ebeanServer.insert(keyword);
            return keyword.getId();
        }, executionContext);
    }

    @Override
    public CompletionStage<Optional<Long>> update(Long id, Keyword keyword) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<Long> value = Optional.empty();
            try {
                Keyword savedKeyword = ebeanServer.find(Keyword.class).setId(id).findUnique();
                if (savedKeyword != null) {
                    savedKeyword.setText(keyword.getText());
                    savedKeyword.update();
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
                final Optional<Keyword> keywordOptional = Optional.ofNullable(ebeanServer.find(Keyword.class).setId(id).findUnique());
                keywordOptional.ifPresent(c -> c.delete());
                return keywordOptional.map(c -> c.getId());
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    public Finder<Long, Keyword> find(){
        return new Finder<>(Keyword.class);
    }
}
