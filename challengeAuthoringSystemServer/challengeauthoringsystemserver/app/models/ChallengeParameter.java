package models;

public class ChallengeParameter {
    private String title;
    private String challengeCategories;
    private String challengeKeywords;
    private String challengeUsages;
    private String isPrivate;
    private String titleImageMediaId;
    private String goldnuggetType;
    private String staticGoldnuggetSecret;
    private String author;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChallengeCategories() {
        return challengeCategories;
    }

    public void setChallengeCategories(String challengeCategories) {
        this.challengeCategories = challengeCategories;
    }

    public String getChallengeKeywords() {
        return challengeKeywords;
    }

    public void setChallengeKeywords(String challengeKeywords) {
        this.challengeKeywords = challengeKeywords;
    }

    public String getChallengeUsages() {
        return challengeUsages;
    }

    public void setChallengeUsages(String challengeUsages) {
        this.challengeUsages = challengeUsages;
    }

    public String isPrivate() {
        return isPrivate;
    }

    public void setPrivate(String aPrivate) {
        isPrivate = aPrivate;
    }

    public String getTitleImageMediaId() {
        return titleImageMediaId;
    }

    public void setTitleImageMediaId(String titleImageMediaId) {
        this.titleImageMediaId = titleImageMediaId;
    }

    public String getGoldnuggetType() {
        return goldnuggetType;
    }

    public void setGoldnuggetType(String goldnuggetType) {
        this.goldnuggetType = goldnuggetType;
    }

    public String getStaticGoldnuggetSecret() {
        return staticGoldnuggetSecret;
    }

    public void setStaticGoldnuggetSecret(String staticGoldnuggetSecret) {
        this.staticGoldnuggetSecret = staticGoldnuggetSecret;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
