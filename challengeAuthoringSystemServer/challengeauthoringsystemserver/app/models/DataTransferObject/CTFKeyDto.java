package models.DataTransferObject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CTFKeyDto {
    @JsonCreator
    public CTFKeyDto(
            @JsonProperty("id")int keyId,
            @JsonProperty("chal") int challengeId,
            @JsonProperty("type") String keyType,
            @JsonProperty("flag") String keyFlag,
            @JsonProperty("data") String keyData){
    	this.setKeyId(keyId);
    	this.setChallengeId(challengeId);
    	this.setKeyType(keyType);    	
    	this.setKeyFlag(keyFlag);
    	this.setKeyData(keyData);
    }

    private int keyId;
    private String keyType;
    private int challengeId;
    private String keyFlag;
    private String keyData;
    
    @JsonProperty("id")
    public int getKeyId() {
        return keyId;
    }

    @JsonProperty("id")
    public void setKeyId(int id) {
        this.keyId = id;
    }

    @JsonProperty("type")
	private String getKeyType() {
		return keyType;
	}

    @JsonProperty("type")
	private void setKeyType(String keyType) {
		this.keyType = keyType;
	}

    @JsonProperty("chal")
	private int getChallengeId() {
		return challengeId;
	}

    @JsonProperty("chal")
	private void setChallengeId(int challengeId) {
		this.challengeId = challengeId;
	}

    @JsonProperty("flag")
	private String getKeyFlag() {
		return keyFlag;
	}

    @JsonProperty("flag")
	private void setKeyFlag(String keyFlag) {
		this.keyFlag = keyFlag;
	}

    @JsonProperty("data")
	private String getKeyData() {
		return keyData;
	}

    @JsonProperty("data")
	private void setKeyData(String keyData) {
		this.keyData = keyData;
	}   
}