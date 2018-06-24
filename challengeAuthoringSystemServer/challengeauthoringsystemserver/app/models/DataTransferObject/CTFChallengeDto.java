package models.DataTransferObject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CTFChallengeDto {
    @JsonCreator
    public CTFChallengeDto(
            @JsonProperty("id")int challengeId,
            @JsonProperty("name")String challengeName,
            @JsonProperty("description") String challengeDescription,
            @JsonProperty("max_attempts") int maxAttempts,
            @JsonProperty("value") int value,
            @JsonProperty("category") String challengeCategory,
            @JsonProperty("type") String challengeType,
            @JsonProperty("hidden") boolean isChallengeHidden){
    	this.setId(challengeId);
    	this.setChallengeName(challengeName);
    	this.setChallengeDescription(challengeDescription);
    	this.setMaxAttempts(maxAttempts);
    	this.setValue(value);
        this.setChallengeCategory(challengeCategory);
        this.setChallengeType(challengeType);
        this.setChallengeHidden(isChallengeHidden);
    }

    private int id;
    private String challengeName;
    private String challengeDescription;
    private int maxAttempts;
    private int value;
    private String challengeCategory;
    private String challengeType;
    private boolean isChallengeHidden;

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty("name")
	public String getChallengeName() {
		return challengeName;
	}

    @JsonProperty("name")
	public void setChallengeName(String challengeName) {
		this.challengeName = challengeName;
	}

    @JsonProperty("description")
	public String getChallengeDescription() {
		return challengeDescription;
	}

    @JsonProperty("description")
	public void setChallengeDescription(String challengeDescription) {
		this.challengeDescription = challengeDescription;
	}

    @JsonProperty("max_attempts")
	public int getMaxAttempts() {
		return maxAttempts;
	}

    @JsonProperty("max_attempts")
	public void setMaxAttempts(int maxAttempts) {
		this.maxAttempts = maxAttempts;
	}

    @JsonProperty("category")
	public String getChallengeCategory() {
		return challengeCategory;
	}

    @JsonProperty("category")
	public void setChallengeCategory(String challengeCategory) {
		this.challengeCategory = challengeCategory;
	}

    @JsonProperty("type")
	public String getChallengeType() {
		return challengeType;
	}

    @JsonProperty("type")
	public void setChallengeType(String challengeType) {
		this.challengeType = challengeType;
	}

    @JsonProperty("hidden")
	public boolean isChallengeHidden() {
		return isChallengeHidden;
	}

    @JsonProperty("hidden")
	public void setChallengeHidden(boolean isChallengeHidden) {
		this.isChallengeHidden = isChallengeHidden;
	}

    @JsonProperty("value")
	public int getValue() {
		return value;
	}

    @JsonProperty("value")
	public void setValue(int value) {
		this.value = value;
	}
}
