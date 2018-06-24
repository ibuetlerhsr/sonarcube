package models.DataTransferObject.ImportedChallengeDtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import models.DataTransferObject.ChallengeDto;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FatChallengeDto {
    @JsonCreator
    public FatChallengeDto(
            @JsonProperty("challengeJson")ChallengeReferenceDto challenge,
            @JsonProperty("languageComponents")List<LanguageComponentDto> languageComponents){
        this.setChallenge(challenge);
        this.setLanguageComponents(languageComponents);
    }

    private ChallengeReferenceDto challenge;
    private List<LanguageComponentDto> languageComponents;

    @JsonProperty("challengeJson")
    public ChallengeReferenceDto getChallenge() {
        return challenge;
    }

    @JsonProperty("challengeJson")
    public void setChallenge(ChallengeReferenceDto challenge) {
        this.challenge = challenge;
    }

    @JsonProperty("languageComponents")
    public List<LanguageComponentDto> getLanguageComponents() {
        return languageComponents;
    }

    @JsonProperty("languageComponents")
    public void setLanguageComponents(List<LanguageComponentDto> languageComponents) {
        this.languageComponents = languageComponents;
    }
}
