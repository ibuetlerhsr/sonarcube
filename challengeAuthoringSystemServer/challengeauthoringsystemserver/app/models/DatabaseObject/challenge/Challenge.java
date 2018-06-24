package models.DatabaseObject.challenge;

import io.ebean.Model;
import io.ebean.annotation.CreatedTimestamp;
import models.DatabaseObject.user.MandantCCS;
import models.DatabaseObject.translation.TranslatableAttribute;
import org.joda.time.DateTime;
import play.data.format.Formats;
import utils.TimestampUtil;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Challenge extends Model {
    private static final long serialVersionUID = 1L;
    private String id;
    private int importId;
    private MandantCCS mandant;
    private List<ChallengeResource> challengeResources = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private List<Keyword> keywords = new ArrayList<>();
    private List<ChallengeUsage> usages = new ArrayList<>();
    private boolean isPrivate;
    private String goldnuggetType;
    private Timestamp lastUpdate;
    private String lastGitCommit;
    private TranslatableAttribute type;
    private List<ChallengeVersion> challengeVersions = new ArrayList<>();
    private Integer version;


    @Id
    public String getId() {
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    @ManyToMany
    @JoinTable(name="challenge_resources",
            joinColumns = @JoinColumn(name="challengeRef", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name="resourceRef", referencedColumnName = "id"))
    public List<ChallengeResource> getChallengeResources() {
        return challengeResources;
    }

    public void setChallengeResources(List<ChallengeResource> challengeResources) {
        this.challengeResources = challengeResources;
    }

    public void addChallengeResource(ChallengeResource challengeResource){
        challengeResources.add(challengeResource);
    }

    public String removeChallengeResource(String resourceId){
        ChallengeResource challengeResourceToRemove = findChallengeResourceById(resourceId);
        challengeResources.remove(challengeResourceToRemove);
        return challengeResourceToRemove.getId();
    }

    private ChallengeResource findChallengeResourceById(String resourceId){
        for (ChallengeResource challengeResource : challengeResources) {
            if(challengeResource.getId().equals(resourceId))
                return challengeResource;
        }
        return null;
    }

    @Column(name="importId")
    public int getImportId() {
        return this.importId;
    }

    public void setImportId(int importId) {
        this.importId = importId;
    }

    @OneToMany(mappedBy="challenge")
    public List<ChallengeVersion> getChallengeVersions(){
        return challengeVersions;
    }

    public void setChallengeVersions(List<ChallengeVersion> challengeVersions){
        this.challengeVersions = challengeVersions;
    }

    private ChallengeVersion getChallengeVersionById(Long challengeId) {
        for(ChallengeVersion version : this.challengeVersions) {
            if(version.getId().equals(challengeId)) {
                return version;
            }
        }

        return null;
    }

    public ChallengeVersion getNewestChallengeVersion() {
        Timestamp newestCreated = TimestampUtil.getTimestamp("01. January 1900");
        Long challengeVersionId = 0L;
        for(ChallengeVersion version : this.challengeVersions) {
            if(version.getCreated() != null) {
                if(version.getCreated().compareTo(newestCreated) > 0) {
                    newestCreated = version.getCreated();
                    challengeVersionId = version.getId();
                }
            }
        }

        return getChallengeVersionById(challengeVersionId);
    }

    @ManyToMany
    @JoinTable(name="challenge_categories",
            joinColumns = @JoinColumn(name="challengeRef", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name="categoryRef", referencedColumnName = "id"))
    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public void addCategory(Category category){
        categories.add(category);
    }

    public Long removeCategory(long categoryId){
        Category categoryToRemove = findCategoryById(categoryId);
        categories.remove(categoryToRemove);
        return categoryToRemove.getId();
    }

    private Category findCategoryById(long categoryId){
        for (Category category: categories) {
            if(category.getId() == categoryId)
                return category;
        }
        return null;
    }

    @Column(name="isPrivate")
    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    @ManyToOne
    @JoinColumn(name="mandantId", nullable=true, insertable=true, updatable=true)
    public MandantCCS getMandant() {
        return mandant;
    }

    public void setMandant(MandantCCS mandant) {
        this.mandant = mandant;
    }

    @ManyToOne
    @JoinColumn(name="type", nullable=true, insertable=true, updatable=true)
    public TranslatableAttribute getType() {
        return type;
    }

    public void setType(TranslatableAttribute type) {
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

    @ManyToMany
    @JoinTable(name="challenge_keywords",
            joinColumns = @JoinColumn(name="challengeRef", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name="keywordRef", referencedColumnName = "id"))
    public List<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }

    public void addKeyword(Keyword keyword){
        keywords.add(keyword);
    }

    public Long removeKeyword(long keywordId){
        Keyword keywordToRemove = findKeywordById(keywordId);
        keywords.remove(keywordToRemove);
        return keywordToRemove.getId();
    }

    private Keyword findKeywordById(long keywordId){
        for (Keyword keyword: keywords) {
            if(keyword.getId() == keywordId)
                return keyword;
        }
        return null;
    }

    @ManyToMany
    @JoinTable(name="challenge_usages",
            joinColumns = @JoinColumn(name="challengeRef", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name="usageRef", referencedColumnName = "id"))
    public List<ChallengeUsage> getUsages() {
        return usages;
    }

    public void setUsages(List<ChallengeUsage> usages) {
        this.usages = usages;
    }

    public void addUsage(ChallengeUsage challengeUsage){
        usages.add(challengeUsage);
    }

    public Long removeChallengeUsage(long challengeUsageId){
        ChallengeUsage challengeUsageToRemove = findChallengeUsageById(challengeUsageId);
        usages.remove(challengeUsageToRemove);
        return challengeUsageToRemove.getId();
    }

    private ChallengeUsage findChallengeUsageById(long challengeUsageId){
        for (ChallengeUsage challengeUsage: usages) {
            if(challengeUsage.getId() == challengeUsageId)
                return challengeUsage;
        }
        return null;
    }

    @Column(name="goldnuggetType")
    public String getGoldnuggetType() {
        return goldnuggetType;
    }

    public void setGoldnuggetType(String goldnuggetType) {
        this.goldnuggetType = goldnuggetType;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="lastUpdate")
    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Column(name="lastGitCommit")
    public String getLastGitCommit() {
        return lastGitCommit;
    }

    public void setLastGitCommit(String lastGitCommit) {
        this.lastGitCommit = lastGitCommit;
    }
}
