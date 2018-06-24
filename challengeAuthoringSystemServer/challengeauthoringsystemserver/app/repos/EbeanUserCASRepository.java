package repos;

import interfaces.repositories.IUserCASRepository;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Finder;
import io.ebean.Transaction;
import models.DatabaseObject.user.UserCAS;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class EbeanUserCASRepository implements IUserCASRepository {
    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public EbeanUserCASRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<Long> insert(UserCAS userCAS) {
        return supplyAsync(() -> {
            ebeanServer.insert(userCAS);
            return userCAS.getId();
        }, executionContext);
    }

    @Override
    public CompletionStage<Optional<Long>> update(Long id, UserCAS newUserCAS) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<Long> value = Optional.empty();
            try {
                UserCAS savedUserCAS = ebeanServer.find(UserCAS.class).setId(id).findUnique();
                if (savedUserCAS != null) {
                    savedUserCAS.setName(newUserCAS.getName());
                    savedUserCAS.setFirstName(newUserCAS.getFirstName());
                    savedUserCAS.setAuthToken(newUserCAS.getAuthToken());
                    savedUserCAS.setLanguageIsoCode(newUserCAS.getFirstName());
                    savedUserCAS.setMessages(newUserCAS.getMessages());
                    savedUserCAS.setSalary(newUserCAS.getSalary());
                    savedUserCAS.setUserMandantRoles(newUserCAS.getUserMandantRoles());
                    savedUserCAS.update();
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
                final Optional<UserCAS> userCASOptional = Optional.ofNullable(ebeanServer.find(UserCAS.class).setId(id).findUnique());
                userCASOptional.ifPresent(c -> c.delete());
                return userCASOptional.map(c -> c.getId());
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    public Finder<Long, UserCAS> find(){
        return new Finder<>(UserCAS.class);
    }


}
