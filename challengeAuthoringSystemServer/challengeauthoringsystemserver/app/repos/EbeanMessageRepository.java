package repos;

import interfaces.repositories.IMessageRepository;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Finder;
import io.ebean.Transaction;
import models.DatabaseObject.user.Message;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class EbeanMessageRepository implements IMessageRepository {
    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public EbeanMessageRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<Long> insert(Message message) {
        return supplyAsync(() -> {
            ebeanServer.insert(message);
            return message.getId();
        }, executionContext);
    }

    @Override
    public CompletionStage<Optional<Long>> update(Long id, Message newMessage) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<Long> value = Optional.empty();
            try {
                Message savedMessage = ebeanServer.find(Message.class).setId(id).findUnique();
                if (savedMessage != null) {
                    savedMessage.setDateCreated(newMessage.getDateCreated());
                    savedMessage.setText(newMessage.getText());
                    savedMessage.setUser(newMessage.getUser());
                    savedMessage.update();
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
                final Optional<Message> messageOptional = Optional.ofNullable(ebeanServer.find(Message.class).setId(id).findUnique());
                messageOptional.ifPresent(c -> c.delete());
                return messageOptional.map(c -> c.getId());
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    public Finder<Long, Message> find(){
        return new Finder<>(Message.class);
    }


}
