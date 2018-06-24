package models.DataTransferObject.ImportedChallengeDtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TranslatedChallengeDto {
    @JsonCreator
    public TranslatedChallengeDto(
            @JsonProperty("challengeId")String challengeId,
            @JsonProperty("languageIsoCode")String languageIsoCode,
            @JsonProperty("translatedTitle")TranslatedTitleDto translatedTitle,
            @JsonProperty("translatedAbstract")TranslatedAbstractDto translatedAbstract,
            @JsonProperty("translatedSolution")TranslatedSolutionDto translatedSolution,
            @JsonProperty("translatedSections")List<TranslatedSectionDto> translatedSections){
        this.setChallengeId(challengeId);
        this.setLanguageIsoCode(languageIsoCode);
        this.setTranslatedTitle(translatedTitle);
        this.setTranslatedAbstract(translatedAbstract);
        this.setTranslatedSolution(translatedSolution);
        this.setTranslatedSections(translatedSections);
    }

    private String challengeId;
    private String languageIsoCode;
    private TranslatedTitleDto translatedTitle;
    private TranslatedAbstractDto translatedAbstract;
    private TranslatedSolutionDto translatedSolution;
    private List<TranslatedSectionDto> translatedSections;

    @JsonProperty("challengeId")
    public String getChallengeId() {
        return challengeId;
    }

    @JsonProperty("challengeId")
    public void setChallengeId(String challengeId) {
        this.challengeId = challengeId;
    }

    @JsonProperty("languageIsoCode")
    public String getLanguageIsoCode() {
        return languageIsoCode;
    }

    @JsonProperty("languageIsoCode")
    public void setLanguageIsoCode(String languageIsoCode) {
        this.languageIsoCode = languageIsoCode;
    }

    @JsonProperty("translatedTitle")
    public TranslatedTitleDto getTranslatedTitle() {
        return translatedTitle;
    }

    @JsonProperty("translatedTitle")
    public void setTranslatedTitle(TranslatedTitleDto translatedTitle) {
        this.translatedTitle = translatedTitle;
    }

    @JsonProperty("translatedAbstract")
    public TranslatedAbstractDto getTranslatedAbstract() {
        return translatedAbstract;
    }

    @JsonProperty("translatedAbstract")
    public void setTranslatedAbstract(TranslatedAbstractDto translatedAbstract) {
        this.translatedAbstract = translatedAbstract;
    }

    @JsonProperty("translatedSolution")
    public TranslatedSolutionDto getTranslatedSolution() {
        return translatedSolution;
    }

    @JsonProperty("translatedSolution")
    public void setTranslatedSolution(TranslatedSolutionDto translatedSolution) {
        this.translatedSolution = translatedSolution;
    }

    @JsonProperty("translatedSections")
    public List<TranslatedSectionDto> getTranslatedSections() {
        return translatedSections;
    }

    @JsonProperty("translatedSections")
    public void setTranslatedSections(List<TranslatedSectionDto> translatedSections) {
        this.translatedSections = translatedSections;
    }
}
