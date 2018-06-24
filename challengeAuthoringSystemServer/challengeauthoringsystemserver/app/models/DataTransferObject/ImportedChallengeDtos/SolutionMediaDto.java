package models.DataTransferObject.ImportedChallengeDtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SolutionMediaDto {
    @JsonCreator
    public SolutionMediaDto(
            @JsonProperty("text")String text,
            @JsonProperty("medias")ChallengeMediaContainerDto mediaContainer){
        this.setText(text);
        this.setMediaContainer(mediaContainer);
    }

    private String text;
    private ChallengeMediaContainerDto mediaContainer;


    @JsonProperty("text")
    public String getText() {
        return text;
    }

    @JsonProperty("text")
    public void setText(String text) {
        this.text = text;
    }

    @JsonProperty("medias")
    public ChallengeMediaContainerDto getMediaContainer() {
        return mediaContainer;
    }

    @JsonProperty("medias")
    public void setMediaContainer(ChallengeMediaContainerDto mediaContainer) {
        this.mediaContainer = mediaContainer;
    }
}

