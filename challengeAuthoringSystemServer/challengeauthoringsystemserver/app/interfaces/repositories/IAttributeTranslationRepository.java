package interfaces.repositories;

import io.ebean.Finder;
import models.DatabaseObject.translation.AttributeTranslation;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface IAttributeTranslationRepository {
    CompletionStage<Long> insert(AttributeTranslation attributeTranslation);
    CompletionStage<Optional<Long>> update(Long id, AttributeTranslation newAttributeTranslation);
    CompletionStage<Optional<Long>>  delete(Long id);
    Finder<Long, AttributeTranslation> find();
}
