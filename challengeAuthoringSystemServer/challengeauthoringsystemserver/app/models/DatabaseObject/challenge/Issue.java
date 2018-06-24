package models.DatabaseObject.challenge;

import io.ebean.Finder;
import models.DatabaseObject.BaseModel;
import models.DatabaseObject.user.UserCAS;

import javax.persistence.*;

@Entity
public class Issue extends BaseModel {
    private static final long serialVersionUID = 1L;
    private String gitId;
    private Challenge challenge;
    private UserCAS createdBy;

    public static Finder<Integer,Issue> find = new Finder<>(Issue.class);

    @Column(name="gitId")
    public String getGitId() {
        return gitId;
    }

    public void setGitId() {
        this.gitId = gitId;
    }

    @ManyToOne
    @JoinColumn(name="challengeId", nullable=true, insertable=true, updatable=true)
    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    @OneToOne
    @JoinColumn(name="createdBy", nullable=true, insertable=true, updatable=true)
    public UserCAS getCreatedBy() {
        return createdBy;
    }
}
