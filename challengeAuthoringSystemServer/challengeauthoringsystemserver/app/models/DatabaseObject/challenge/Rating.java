package models.DatabaseObject.challenge;

import io.ebean.Finder;
import models.DatabaseObject.BaseModel;
import models.DatabaseObject.user.UserCAS;

import javax.persistence.*;

@Entity
public class Rating extends BaseModel{
    private static final long serialVersionUID = 1L;
    private int value;
    private String text;
    private ChallengeVersion challengeVersion;
    private UserCAS createdBy;

    public static Finder<Integer,Section> find = new Finder<>(Section.class);

    public Rating(){
    }

    @Column(name="text")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
    @JoinColumn(name="createdBy", nullable=true, insertable=true, updatable=true)
    public UserCAS getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserCAS userCAS){ this.createdBy = userCAS; }

    @Column(name="value")
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
