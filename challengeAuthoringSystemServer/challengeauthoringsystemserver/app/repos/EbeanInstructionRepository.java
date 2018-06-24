package repos;

import interfaces.repositories.IInstructionRepository;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Finder;
import io.ebean.Transaction;
import models.DatabaseObject.challenge.Instruction;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * A repository that executes database operations in a different
 * execution context.
 */
public class EbeanInstructionRepository implements IInstructionRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public EbeanInstructionRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    public Finder<String, Instruction> find(){
        return new Finder<>(Instruction.class);
    }

    public CompletionStage<Optional<Instruction>> lookup(String id) {
        return supplyAsync(() -> {
            return Optional.ofNullable(ebeanServer.find(Instruction.class).setId(id).findUnique());
        }, executionContext);
    }

    public CompletionStage<Optional<String>> update(String id, Instruction newInstruction) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<String> value = Optional.empty();
            try {
                Instruction savedInstruction = ebeanServer.find(Instruction.class).setId(id).findUnique();
                if (savedInstruction != null) {
                    savedInstruction.setText(newInstruction.getText());
                    savedInstruction.setSection(newInstruction.getSection());
                    savedInstruction.update();
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
                final Optional<Instruction> InstructionOptional = Optional.ofNullable(ebeanServer.find(Instruction.class).setId(id).findUnique());
                InstructionOptional.ifPresent(c -> c.delete());
                return InstructionOptional.map(c -> c.getId());
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    public CompletionStage<String> insert(Instruction newInstruction ) {
        return supplyAsync(() -> {
             ebeanServer.insert(newInstruction);
             return newInstruction.getId();
        }, executionContext);
    }
}
