package interfaces.repositories;

import io.ebean.Finder;
import models.DatabaseObject.translation.CodeType;
import models.DatabaseObject.translation.LanguageDefinition;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ILanguageDefinitionRepository {
    CompletionStage<Optional<Long>> update(Long id, LanguageDefinition languageDefinition);
    CompletionStage<Long> insert(LanguageDefinition languageDefinition);
    Finder<Long, LanguageDefinition> find();
}
