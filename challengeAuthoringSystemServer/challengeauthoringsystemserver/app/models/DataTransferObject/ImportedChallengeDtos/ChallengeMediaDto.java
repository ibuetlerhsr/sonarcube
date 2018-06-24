package models.DataTransferObject.ImportedChallengeDtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChallengeMediaDto {
    @JsonCreator
    public ChallengeMediaDto(
            @JsonProperty("filePath")String filePath,
            @JsonProperty("base64Encoded")String base64Encoded){
        this.setFilePath(filePath);
        this.setBase64Encoded(base64Encoded);
    }

    private String base64Encoded;
    private String filePath;

    @JsonProperty("base64Encoded")
    public String getBase64Encoded() {
        return base64Encoded;
    }

    @JsonProperty("base64Encoded")
    public void setBase64Encoded(String base64Encoded) {
        this.base64Encoded = base64Encoded;
    }

    @JsonProperty("filePath")
    public String getFilePath() {
        return filePath;
    }

    @JsonProperty("filePath")
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}

