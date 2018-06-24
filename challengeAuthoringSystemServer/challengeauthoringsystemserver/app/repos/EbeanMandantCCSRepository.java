package repos;

import interfaces.repositories.IMandantCCSRepository;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Finder;
import io.ebean.Transaction;
import models.DatabaseObject.user.MandantCCS;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class EbeanMandantCCSRepository implements IMandantCCSRepository {
    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public EbeanMandantCCSRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<Long> insert(MandantCCS mandantCCS) {
        return supplyAsync(() -> {
            ebeanServer.insert(mandantCCS);
            return mandantCCS.getId();
        }, executionContext);
    }

    @Override
    public CompletionStage<Optional<Long>> update(Long id, MandantCCS newMandantCCS) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<Long> value = Optional.empty();
            try {
                MandantCCS savedMandantCCS = ebeanServer.find(MandantCCS.class).setId(id).findUnique();
                if (savedMandantCCS != null) {
                    savedMandantCCS.setMandantName(newMandantCCS.getMandantName());
                    savedMandantCCS.setUserMandantRoles(newMandantCCS.getUserMandantRoles());
                    savedMandantCCS.update();
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
                final Optional<MandantCCS> mandantCCSOptional = Optional.ofNullable(ebeanServer.find(MandantCCS.class).setId(id).findUnique());
                mandantCCSOptional.ifPresent(c -> c.delete());
                return mandantCCSOptional.map(c -> c.getId());
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    public Finder<Long, MandantCCS> find(){
        return new Finder<>(MandantCCS.class);
    }


}
