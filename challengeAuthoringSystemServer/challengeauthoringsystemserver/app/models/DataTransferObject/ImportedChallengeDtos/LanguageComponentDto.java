package models.DataTransferObject.ImportedChallengeDtos;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LanguageComponentDto {
    @JsonCreator
    public LanguageComponentDto(
            @JsonProperty("isoCode")String isoCode,
            @JsonProperty("titleMD")String titleMD,
            @JsonProperty("titlePNG")String titlePNG,
            @JsonProperty("solutionMD")String solutionMD,
            @JsonProperty("abstractMD")String abstractMD,
            @JsonProperty("sections")ArrayList<ChallengeSectionMDDto> sections,
            @JsonProperty("medias")ChallengeMediaContainerDto mediaContainer){
        this.setIsoCode(isoCode);
        this.setTitlePNG(titlePNG);
        this.setTitleMD(titleMD);
        this.setSolutionMD(solutionMD);
        this.setAbstractMD(abstractMD);
        this.setSections(sections);
        this.setMediaContainer(mediaContainer);
    }

    private String isoCode;
    private String titleMD;
    private String titlePNG;
    private String solutionMD;
    private String abstractMD;
    private ArrayList<ChallengeSectionMDDto> sections;
    private ChallengeMediaContainerDto mediaContainer;

    @JsonProperty("isoCode")
    public String getIsoCode() {
        return isoCode;
    }

    @JsonProperty("isoCode")
    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    @JsonProperty("titleMD")
    public String getTitleMD() {
        return titleMD;
    }

    @JsonProperty("titleMD")
    public void setTitleMD(String titleMD) {
        this.titleMD = titleMD;
    }

    @JsonProperty("titlePNG")
    public String getTitlePNG() {
        return titlePNG;
    }

    @JsonProperty("titlePNG")
    public void setTitlePNG(String titlePNG) {
        this.titlePNG = titlePNG;
    }

    @JsonProperty("solutionMD")
    public String getSolutionMD() {
        return solutionMD;
    }

    @JsonProperty("solutionMD")
    public void setSolutionMD(String solutionMD) {
        this.solutionMD = solutionMD;
    }

    @JsonProperty("abstractMD")
    public String getAbstractMD() {
        return abstractMD;
    }

    @JsonProperty("abstractMD")
    public void setAbstractMD(String abstractMD) {
        this.abstractMD = abstractMD;
    }

    @JsonProperty("sections")
    public ArrayList<ChallengeSectionMDDto> getSections() {
        return sections;
    }

    @JsonProperty("sections")
    public void setSections(ArrayList<ChallengeSectionMDDto> sections) {
        this.sections = sections;
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

