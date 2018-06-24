package interfaces.repositories;

import io.ebean.Finder;
import models.DatabaseObject.challenge.Section;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ISectionRepository {
    CompletionStage<Optional<Section>> lookup(String id);
    CompletionStage<Optional<String>> update(String id, Section newSection);
    CompletionStage<Optional<String>>  delete(String id);
    CompletionStage<String> insert(Section section);
    Finder<String, Section> find();
}
