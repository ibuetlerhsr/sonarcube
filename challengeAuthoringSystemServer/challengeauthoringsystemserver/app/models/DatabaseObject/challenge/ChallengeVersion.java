package models.DatabaseObject.challenge;

import io.ebean.Finder;
import models.DatabaseObject.BaseModel;
import models.DatabaseObject.translation.TranslatableAttribute;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ChallengeVersion extends BaseModel {
    private static final long serialVersionUID = 1L;
    private Challenge challenge;
    private String author;
    private Timestamp created;
    private String staticGoldnuggetSecret;
    private String name;
    private TranslatableAttribute title;
    private Abstract anAbstract;
    private ChallengeLevel challengeLevel;
    private ChallengeStatus challengeStatus;
    private Solution solution;
    private int maxPoints;
    private List<Section> sections = new ArrayList<>();
    private MediaReference mediaReference;

    @ManyToOne
    @JoinColumn(name="challengeId")
    public Challenge getChallenge(){
        return challenge;
    }

    public void setChallenge(Challenge challenge){
        this.challenge = challenge;
    }

    /*@ManyToOne
    @JoinColumn(name="author")
    public UserCAS getAuthor(){
        return author;
    }

    public void setAuthor(UserCAS author){
        this.author = author;
    }*/

    @Column(name="author")
    public String getAuthor(){
        return author;
    }

    public void setAuthor(String author){
        this.author = author;
    }

    @Column(name="staticGoldnuggetSecret")
    public String getStaticGoldnuggetSecret() {
        return staticGoldnuggetSecret;
    }

    public void setStaticGoldnuggetSecret(String staticGoldnuggetSecret) {
        this.staticGoldnuggetSecret = staticGoldnuggetSecret;
    }

    @OneToOne
    @JoinColumn(name="name", nullable=true, insertable=true, updatable=true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToOne
    @JoinColumn(name="title", nullable=true, insertable=true, updatable=true)
    public TranslatableAttribute getTitle() {
        return title;
    }

    public void setTitle(TranslatableAttribute title) {
        this.title = title;
    }

    @OneToOne
    @JoinColumn(name="solutionId", nullable=true, insertable=true, updatable=true)
    public Solution getSolution() {
        return solution;
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
    }

    @OneToOne
    @JoinColumn(name="abstractId", nullable=true, insertable=true, updatable=true)
    public Abstract getAnAbstract() {
        return anAbstract;
    }

    public void setAnAbstract(Abstract anAbstract) {
        this.anAbstract = anAbstract;
    }

    @ManyToOne
    @JoinColumn(name="challengeLevelId", nullable=true, insertable=true, updatable=true)
    public ChallengeLevel getChallengeLevel() {
        return challengeLevel;
    }

    public void setChallengeLevel(ChallengeLevel challengeLevel) {
        this.challengeLevel = challengeLevel;
    }

    @ManyToOne
    @JoinColumn(name="challengeStatusId", nullable=true, insertable=true, updatable=true)
    public ChallengeStatus getChallengeStatus() {
        return challengeStatus;
    }

    public void setChallengeStatus(ChallengeStatus challengeStatus) {
        this.challengeStatus = challengeStatus;
    }

    @Column(name="maxPoints")
    public int getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(int maxPoints) {
        this.maxPoints = maxPoints;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created")
    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    @OneToMany(mappedBy="challengeVersion")
    public List<Section> getSections(){
        return sections;
    }

    public void setSections(List<Section> sections){
        this.sections = sections;
    }

    public void addSection(Section newSection) {
        this.sections.add(newSection);
    }

    public String removeSection(String sectionId) {
        Section section = this.findSectionById(sectionId);
        this.sections.remove(section);
        return section.getId();
    }

    public Section findSectionById(String sectionId){
        for (Section section: sections) {
            if(section.getId().equals(sectionId))
                return section;
        }
        return null;
    }

    @ManyToOne
    @JoinColumn(name="mediaReferenceId", nullable=true, insertable=true, updatable=true)
    public MediaReference getMediaReference() {
        return mediaReference;
    }

    public void setMediaReference(MediaReference mediaReference) {
        this.mediaReference = mediaReference;
    }
}
