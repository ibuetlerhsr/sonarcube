package repos;

import interfaces.repositories.IRoleRepository;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Finder;
import io.ebean.Transaction;
import models.DatabaseObject.user.Role;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class EbeanRoleRepository implements IRoleRepository {
    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public EbeanRoleRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<Long> insert(Role role) {
        return supplyAsync(() -> {
            ebeanServer.insert(role);
            return role.getId();
        }, executionContext);
    }

    @Override
    public CompletionStage<Optional<Long>> update(Long id, Role newRole) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<Long> value = Optional.empty();
            try {
                Role savedRole = ebeanServer.find(Role.class).setId(id).findUnique();
                if (savedRole != null) {
                    savedRole.setRoleName(newRole.getRoleName());
                    savedRole.setUserMandantRoles(newRole.getUserMandantRoles());
                    savedRole.update();
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
                final Optional<Role> roleOptional = Optional.ofNullable(ebeanServer.find(Role.class).setId(id).findUnique());
                roleOptional.ifPresent(c -> c.delete());
                return roleOptional.map(c -> c.getId());
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    public Finder<Long, Role> find(){
        return new Finder<>(Role.class);
    }


}
