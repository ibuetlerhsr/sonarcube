package repos;

import interfaces.repositories.ICodeTypeRepository;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Finder;
import models.DatabaseObject.translation.CodeType;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class EbeanCodeTypeRepository implements ICodeTypeRepository {
    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public EbeanCodeTypeRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<Long> insert(CodeType codeType) {
        return supplyAsync(() -> {
            ebeanServer.insert(codeType);
            return codeType.getId();
        }, executionContext);
    }

    public Finder<Long, CodeType> find(){
        return new Finder<>(CodeType.class);
    }
}
