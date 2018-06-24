package interfaces.repositories;

import io.ebean.Finder;
import models.DatabaseObject.challenge.Instruction;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface IInstructionRepository {
    CompletionStage<Optional<Instruction>> lookup(String id);
    CompletionStage<Optional<String>> update(String id, Instruction newInstruction);
    CompletionStage<Optional<String>>  delete(String id);
    CompletionStage<String> insert(Instruction instruction);
    Finder<String, Instruction> find();
}
