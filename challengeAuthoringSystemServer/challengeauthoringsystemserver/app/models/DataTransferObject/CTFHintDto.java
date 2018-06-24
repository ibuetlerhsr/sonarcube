package models.DataTransferObject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CTFHintDto {
    @JsonCreator
    public CTFHintDto(
            @JsonProperty("id")int hintId,
            @JsonProperty("type") int hintType,
            @JsonProperty("chal") int challengeId,
            @JsonProperty("hint") String hintText,
            @JsonProperty("cost") int hintCost){
    	this.setHintId(hintId);
    	this.setHintType(hintType);
    	this.setChallengeId(challengeId);
    	this.setHintText(hintText);
    	this.setHintCost(hintCost);
    }

    private int hintId;
    private int hintType;
    private int challengeId;
    private String hintText;
    private int hintCost;
    
    @JsonProperty("id")
    public int getHintId() {
        return hintId;
    }

    @JsonProperty("id")
    public void setHintId(int id) {
        this.hintId = id;
    }

    @JsonProperty("type")
	public int getHintType() {
		return hintType;
	}

    @JsonProperty("type")
	public void setHintType(int hintType) {
		this.hintType = hintType;
	}

    @JsonProperty("chal")
	public int getChallengeId() {
		return challengeId;
	}

    @JsonProperty("chal")
	public void setChallengeId(int challengeId) {
		this.challengeId = challengeId;
	}

    @JsonProperty("hint")
	public String getHintText() {
		return hintText;
	}

    @JsonProperty("hint")
	public void setHintText(String hintText) {
		this.hintText = hintText;
	}

    @JsonProperty("cost")
	public int getHintCost() {
		return hintCost;
	}

    @JsonProperty("cost")
	public void setHintCost(int hintCost) {
		this.hintCost = hintCost;
	}   
}