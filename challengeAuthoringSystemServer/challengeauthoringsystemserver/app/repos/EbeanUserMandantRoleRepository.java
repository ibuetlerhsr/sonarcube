package repos;

import interfaces.repositories.IUserMandantRoleRepository;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Finder;
import io.ebean.Transaction;
import models.DatabaseObject.user.UserMandantRole;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class EbeanUserMandantRoleRepository implements IUserMandantRoleRepository {
    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public EbeanUserMandantRoleRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<Long> insert(UserMandantRole userMandantRole) {
        return supplyAsync(() -> {
            ebeanServer.insert(userMandantRole);
            return userMandantRole.getId();
        }, executionContext);
    }

    @Override
    public CompletionStage<Optional<Long>> update(Long id, UserMandantRole newUserMandantRole) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<Long> value = Optional.empty();
            try {
                UserMandantRole savedUserMandantRole = ebeanServer.find(UserMandantRole.class).setId(id).findUnique();
                if (savedUserMandantRole != null) {
                    savedUserMandantRole.setMandant(newUserMandantRole.getMandant());
                    savedUserMandantRole.setRole(newUserMandantRole.getRole());
                    savedUserMandantRole.setUser(newUserMandantRole.getUser());
                    savedUserMandantRole.update();
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
                final Optional<UserMandantRole> userMandantRoleOptional = Optional.ofNullable(ebeanServer.find(UserMandantRole.class).setId(id).findUnique());
                userMandantRoleOptional.ifPresent(c -> c.delete());
                return userMandantRoleOptional.map(c -> c.getId());
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    public Finder<Long, UserMandantRole> find(){
        return new Finder<>(UserMandantRole.class);
    }


}
