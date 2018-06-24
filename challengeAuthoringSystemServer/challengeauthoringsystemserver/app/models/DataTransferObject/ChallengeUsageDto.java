package models.DataTransferObject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChallengeUsageDto {
    @JsonCreator
    public ChallengeUsageDto(
            @JsonProperty("id")Long id,
            @JsonProperty("text")String text){
        this.setId(id);
        this.setTypeText(text);
    }

    private Long id;
    private String typeText;

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("text")
    public String getTypeText() {
        return typeText;
    }

    @JsonProperty("text")
    public void setTypeText(String typeText) {
        this.typeText = typeText;
    }
}
