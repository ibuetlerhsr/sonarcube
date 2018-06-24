package models.DatabaseObject.challenge;

import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
public class ChallengeResource extends Model {
    private static final long serialVersionUID = 1L;
    private String id;
    private Integer version;
    private String type;
    private List<Challenge> challenges = new ArrayList<>();

    public static Finder<Integer,ChallengeResource> find = new Finder<>(ChallengeResource.class);

    @Id
    public String getId() {
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    @ManyToMany(mappedBy="challengeResources")
    public List<Challenge> getChallenges() {
        return challenges;
    }

    public void setChallenges(Collection<Challenge> categories) {
        this.challenges = challenges;
    }

    @OneToOne
    @JoinColumn(name="type", nullable=true, insertable=true, updatable=true)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Version
    @Column(name="version")
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
