package models.DatabaseObject.challenge;

import io.ebean.Finder;
import models.DatabaseObject.BaseModel;
import models.DatabaseObject.translation.TranslatableAttribute;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
public class ChallengeUsage extends BaseModel {
    private static final long serialVersionUID = 1L;
    private TranslatableAttribute text;
    private List<Challenge> challenges = new ArrayList<>();

    public static Finder<Integer,ChallengeUsage> find = new Finder<>(ChallengeUsage.class);

    @OneToOne
    @JoinColumn(name="text", nullable=true, insertable=true, updatable=true)
    public TranslatableAttribute getText() {
        return text;
    }

    public void setText(TranslatableAttribute text) {
        this.text = text;
    }

    @ManyToMany(mappedBy="usages")
    public List<Challenge> getChallenges() {
        return challenges;
    }

    public void setChallenges(List<Challenge> challenges) {
        this.challenges = challenges;
    }
}
