package models.DataTransferObject.ImportedChallengeDtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TranslatedTitleDto {
    @JsonCreator
    public TranslatedTitleDto(
            @JsonProperty("isUserCreated") boolean isUserCreated,
            @JsonProperty("text")String text){
        this.setText(text);
        this.setIsUserCreated(isUserCreated);
    }

    private String text;
    private boolean isUserCreated;


    @JsonProperty("text")
    public String getText() {
        return text;
    }

    @JsonProperty("text")
    public void setText(String text) {
        this.text = text;
    }

    @JsonProperty("isUserCreated")
    public boolean getIsUserCreated() {
        return isUserCreated;
    }

    @JsonProperty("isUserCreated")
    public void setIsUserCreated(boolean isUserCreated) {
        this.isUserCreated = isUserCreated;
    }
}

