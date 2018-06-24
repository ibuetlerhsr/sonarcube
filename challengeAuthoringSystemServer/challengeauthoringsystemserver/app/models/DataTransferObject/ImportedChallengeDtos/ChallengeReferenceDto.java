package models.DataTransferObject.ImportedChallengeDtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChallengeReferenceDto {
    @JsonCreator
    public ChallengeReferenceDto(
            @JsonProperty("id")String uuid,
            @JsonProperty("name")String name,
            @JsonProperty("level")Long level,
            @JsonProperty("type")String type,
            @JsonProperty("usages")ArrayList<String> usages,
            @JsonProperty("keywords")ArrayList<String> keywords,
            @JsonProperty("categories")ArrayList<String> categories,
            @JsonProperty("created")String created,
            @JsonProperty("author")String author,
            @JsonProperty("last update")String lastUpdate,
            @JsonProperty("last git commit")String lastGitCommit,
            @JsonProperty("resources")ArrayList<ChallengeResourceDto> resources,
            @JsonProperty("goldnugget-type")String goldnuggetType,
            @JsonProperty("goldnugget-secret-static")String staticGoldnuggetSecret,
            @JsonProperty("public")boolean isPublic,
            @JsonProperty("sections")ArrayList<ChallengeSectionReferenceDto> sections){
        this.setId(uuid);
        this.setName(name);
        this.setLevel(level);
        this.setType(type);
        this.setUsages(usages);
        this.setKeywords(keywords);
        this.setCategories(categories);
        this.setCreated(created);
        this.setAuthor(author);
        this.setLastUpdate(lastUpdate);
        this.setLastGitCommit(lastGitCommit);
        this.setResources(resources);
        this.setGoldnuggetType(goldnuggetType);
        this.setStaticGoldnuggetSecret(staticGoldnuggetSecret);
        this.setSections(sections);
        this.setPublic(isPublic);
    }

    private String id;
    private String name;
    private Long level;
    private String type;
    private ArrayList<String> usages;
    private ArrayList<String> keywords;
    private ArrayList<String> categories;
    private String created;
    private String author;
    private String lastUpdate;
    private String lastGitCommit;
    private ArrayList<ChallengeResourceDto> resources;
    private String goldnuggetType;
    private String staticGoldnuggetSecret;
    private ArrayList<ChallengeSectionReferenceDto> sections;
    private boolean isPublic;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("level")
    public Long getLevel() {
        return level;
    }

    @JsonProperty("level")
    public void setLevel(Long level) {
        this.level = level;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("usages")
    public ArrayList<String> getUsages() {
        return usages;
    }

    @JsonProperty("usages")
    public void setUsages(ArrayList<String> usages) {
        this.usages = usages;
    }

    @JsonProperty("keywords")
    public ArrayList<String> getKeywords() {
        return keywords;
    }

    @JsonProperty("keywords")
    public void setKeywords(ArrayList<String> keywords) {
        this.keywords = keywords;
    }

    @JsonProperty("categories")
    public ArrayList<String> getCategories() {
        return categories;
    }

    @JsonProperty("categories")
    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    @JsonProperty("created")
    public String getCreated() {
        return created;
    }

    @JsonProperty("created")
    public void setCreated(String created) {
        this.created = created;
    }

    @JsonProperty("author")
    public String getAuthor() {
        return author;
    }

    @JsonProperty("author")
    public void setAuthor(String author) {
        this.author = author;
    }

    @JsonProperty("last update")
    public String getLastUpdate() {
        return lastUpdate;
    }

    @JsonProperty("last update")
    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @JsonProperty("last git commit")
    public String getLastGitCommit() {
        return lastGitCommit;
    }

    @JsonProperty("last git commit")
    public void setLastGitCommit(String lastGitCommit) {
        this.lastGitCommit = lastGitCommit;
    }

    @JsonProperty("resources")
    public ArrayList<ChallengeResourceDto> getResources() {
        return resources;
    }

    @JsonProperty("resources")
    public void setResources(ArrayList<ChallengeResourceDto> resources) {
        this.resources = resources;
    }

    @JsonProperty("goldnugget-type")
    public String getGoldnuggetType() {
        return goldnuggetType;
    }

    @JsonProperty("goldnugget-type")
    public void setGoldnuggetType(String goldnuggetType) {
        this.goldnuggetType = goldnuggetType;
    }

    @JsonProperty("goldnugget-secret-static")
    public String getStaticGoldnuggetSecret() {
        return staticGoldnuggetSecret;
    }

    @JsonProperty("goldnugget-secret-static")
    public void setStaticGoldnuggetSecret(String staticGoldnuggetSecret) {
        this.staticGoldnuggetSecret = staticGoldnuggetSecret;
    }

    @JsonProperty("sections")
    public ArrayList<ChallengeSectionReferenceDto> getSections() {
        return sections;
    }

    @JsonProperty("sections")
    public void setSections(ArrayList<ChallengeSectionReferenceDto> sections) {
        this.sections = sections;
    }

    @JsonProperty("public")
    public boolean isPublic() {
        return isPublic;
    }

    @JsonProperty("public")
    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }
}

