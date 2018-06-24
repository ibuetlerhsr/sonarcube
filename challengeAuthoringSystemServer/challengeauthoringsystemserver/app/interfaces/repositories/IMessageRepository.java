package interfaces.repositories;

import io.ebean.Finder;
import models.DatabaseObject.user.MandantCCS;
import models.DatabaseObject.user.Message;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface IMessageRepository {
    CompletionStage<Long> insert(Message message);
    CompletionStage<Optional<Long>> update(Long id, Message newMessage);
    CompletionStage<Optional<Long>>  delete(Long id);
    Finder<Long, Message> find();
}
