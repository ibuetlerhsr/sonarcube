package interfaces.repositories;

import io.ebean.Finder;
import models.DatabaseObject.translation.CodeType;

import java.util.concurrent.CompletionStage;

public interface ICodeTypeRepository {
    CompletionStage<Long> insert(CodeType codeType);
    Finder<Long, CodeType> find();
}
