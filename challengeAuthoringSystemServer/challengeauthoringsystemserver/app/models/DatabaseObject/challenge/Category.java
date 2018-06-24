package models.DatabaseObject.challenge;

import io.ebean.Finder;
import models.DatabaseObject.BaseModel;
import models.DatabaseObject.translation.TranslatableAttribute;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
public class Category extends BaseModel {
    private static final long serialVersionUID = 1L;
    private TranslatableAttribute categoryName;
    private List<Challenge> challenges = new ArrayList<>();

    public static Finder<Integer,Category> find = new Finder<>(Category.class);

    @OneToOne
    @JoinColumn(name="categoryName", nullable=true, insertable=true, updatable=true)
    public TranslatableAttribute getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(TranslatableAttribute categoryName) {
        this.categoryName = categoryName;
    }

    @ManyToMany(mappedBy="categories")
    public List<Challenge> getChallenges() {
        return challenges;
    }

    public void setChallenges(Collection<Challenge> categories) {
        this.challenges = challenges;
    }
}
