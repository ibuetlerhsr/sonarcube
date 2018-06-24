package repos;

import interfaces.repositories.ILanguageDefinitionRepository;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Finder;
import io.ebean.Transaction;
import models.DatabaseObject.translation.LanguageDefinition;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class EbeanLanguageDefinitionRepository implements ILanguageDefinitionRepository {
    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public EbeanLanguageDefinitionRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    public CompletionStage<Optional<Long>> update(Long id, LanguageDefinition languageDefinition) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<Long> value = Optional.empty();
            try {
                LanguageDefinition savedLanguageDefinition = ebeanServer.find(LanguageDefinition.class).setId(id).findUnique();
                if (savedLanguageDefinition != null) {
                    savedLanguageDefinition.setLanguageName(languageDefinition.getLanguageName());
                    savedLanguageDefinition.setIsoLangCode(languageDefinition.getIsoLangCode());
                    savedLanguageDefinition.update();
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
    public CompletionStage<Long> insert(LanguageDefinition languageDefinition) {
        return supplyAsync(() -> {
            ebeanServer.insert(languageDefinition);
            return languageDefinition.getId();
        }, executionContext);
    }

    public Finder<Long, LanguageDefinition> find(){
        return new Finder<>(LanguageDefinition.class);
    }
}
