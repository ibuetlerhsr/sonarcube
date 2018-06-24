package interfaces.repositories;

import io.ebean.Finder;
import models.DatabaseObject.challenge.MarkdownMediaReference;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface IMarkdownMediaReferenceRepository {
    CompletionStage<Long> insert(MarkdownMediaReference newMarkdownMediaReference);
    CompletionStage<Optional<Long>> update(Long id, MarkdownMediaReference markdownMediaReference);
    CompletionStage<Optional<Long>>  delete(Long id);
    Finder<Long, MarkdownMediaReference> find();
}
