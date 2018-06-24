package repos;

import interfaces.repositories.ICategoryRepository;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Finder;
import io.ebean.Transaction;
import models.DatabaseObject.challenge.Category;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class EbeanCategoryRepository implements ICategoryRepository {
    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public EbeanCategoryRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<Long> insert(Category category) {
        return supplyAsync(() -> {
            ebeanServer.insert(category);
            return category.getId();
        }, executionContext);
    }

    @Override
    public CompletionStage<Optional<Long>> update(Long id, Category newCategory) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<Long> value = Optional.empty();
            try {
                Category savedCategory = ebeanServer.find(Category.class).setId(id).findUnique();
                if (savedCategory != null) {
                    savedCategory.setCategoryName(newCategory.getCategoryName());
                    savedCategory.setChallenges(newCategory.getChallenges());
                    savedCategory.update();
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
                final Optional<Category> categoryOptional = Optional.ofNullable(ebeanServer.find(Category.class).setId(id).findUnique());
                categoryOptional.ifPresent(c -> c.delete());
                return categoryOptional.map(c -> c.getId());
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    public Finder<Long, Category> find(){
        return new Finder<>(Category.class);
    }


}
