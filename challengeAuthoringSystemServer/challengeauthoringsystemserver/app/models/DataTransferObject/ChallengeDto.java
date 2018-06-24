package models.DataTransferObject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChallengeDto {
    @JsonCreator
    public ChallengeDto(
            @JsonProperty("_id")String uuid,
            @JsonProperty("ChallengeName")String challengeName,
            @JsonProperty("ChallengeLevel")String challengeLevel,
            @JsonProperty("ChallengeType")String challengeType,
            @JsonProperty("LinkCAS")List<ChallengeAuthoringSystemDto> linkCAS,
            @JsonProperty("LinkCRS")List<ChallengeResourceSystemDto> linkCRS,
            @JsonProperty("AvailableLanguages") String languages){
        this.setChallengeName(challengeName);
        this.setChallengeLevel(challengeLevel);
        this.setChallengeType(challengeType);
        this.setLinkCAS(linkCAS);
        this.setLinkCRS(linkCRS);
        this.setLanguages(languages);
        this.set_id(uuid);
    }

    private String _id;
    private String challengeName;
    private String challengeLevel;
    private String challengeType;
    private String languages;
    private List<ChallengeAuthoringSystemDto> linkCAS;
    private List<ChallengeResourceSystemDto> linkCRS;

    @JsonProperty("_id")
    public String get_id() {
        return _id;
    }

    @JsonProperty("_id")
    public void set_id(String _id) {
        this._id = _id;
    }

    @JsonProperty("ChallengeName")
    public String getChallengeName() {
        return challengeName;
    }

    @JsonProperty("ChallengeName")
    public void setChallengeName(String challengeName) {
        this.challengeName = challengeName;
    }

    @JsonProperty("ChallengeLevel")
    public String getChallengeLevel() {
        return challengeLevel;
    }

    @JsonProperty("ChallengeLevel")
    public void setChallengeLevel(String challengeLevel) {
        this.challengeLevel = challengeLevel;
    }

    @JsonProperty("ChallengeType")
    public String getChallengeType() {
        return challengeType;
    }

    @JsonProperty("ChallengeType")
    public void setChallengeType(String challengeType) {
        this.challengeType = challengeType;
    }

    @JsonProperty("LinkCAS")
    public List<ChallengeAuthoringSystemDto> getLinkCAS() {
        return linkCAS;
    }

    @JsonProperty("LinkCAS")
    public void setLinkCAS(List<ChallengeAuthoringSystemDto> linkCAS) {
        this.linkCAS = linkCAS;
    }

    @JsonProperty("LinkCRS")
    public List<ChallengeResourceSystemDto> getLinkCRS() {
        return linkCRS;
    }

    @JsonProperty("LinkCRS")
    public void setLinkCRS(List<ChallengeResourceSystemDto> linkCRS) {
        this.linkCRS = linkCRS;
    }

    @JsonProperty("AvailableLanguages")
    public String getLanguages() {
        return languages;
    }

    @JsonProperty("AvailableLanguages")
    public void setLanguages(String languages) {
        this.languages = languages;
    }
}
