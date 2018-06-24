package models.DatabaseObject.challenge;

import io.ebean.Finder;
import models.DatabaseObject.BaseModel;
import models.DatabaseObject.user.UserCAS;

import javax.persistence.*;

@Entity
public class ChallengeUserExecution extends BaseModel{
    private static final long serialVersionUID = 1L;
    private String goldNugget;
    private ChallengeVersion challengeVersion;
    private UserCAS user;

    public static Finder<Integer,ChallengeStatus> find = new Finder<>(ChallengeStatus.class);

    @Column(name="goldNugget")
    public String getGoldNugget() {
        return goldNugget;
    }

    public void setGoldNugget(String goldNugget) {
        this.goldNugget = goldNugget;
    }

    @ManyToOne
    @JoinColumn(name="challengeVersionId", nullable=true, insertable=true, updatable=true)
    public ChallengeVersion getChallengeVersion() {
        return challengeVersion;
    }

    public void setChallengeVersion(ChallengeVersion version) {
        this.challengeVersion = version;
    }

    @ManyToOne
    @JoinColumn(name="userId", nullable=true, insertable=true, updatable=true)
    public UserCAS getUser() {
        return user;
    }

    public void setUser(UserCAS user) {
        this.user = user;
    }
}
