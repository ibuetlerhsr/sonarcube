package interfaces.repositories;

import io.ebean.Finder;
import models.DatabaseObject.translation.LanguageDefinition;
import models.DatabaseObject.translation.TranslatableAttribute;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ITranslatableAttributeRepository {
    CompletionStage<Long> insert(TranslatableAttribute translatableAttribute);
    CompletionStage<Optional<Long>>  delete(Long id);
    Finder<Long, TranslatableAttribute> find();
}
