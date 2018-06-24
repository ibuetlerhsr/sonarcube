package models.DataTransferObject.ImportedChallengeDtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChallengeSectionMDDto {
    @JsonCreator
    public ChallengeSectionMDDto(
            @JsonProperty("id")String uuid,
            @JsonProperty("sectionMD")String sectionMD,
            @JsonProperty("steps")ArrayList<ChallengeStepMDDto> steps){
        this.setSectionMD(sectionMD);
        this.setSteps(steps);
        this.setId(uuid);
    }

    private String id;
    private String sectionMD;
    private ArrayList<ChallengeStepMDDto> steps;


    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("sectionMD")
    public String getSectionMD() {
        return sectionMD;
    }

    @JsonProperty("sectionMD")
    public void setSectionMD(String sectionMD) {
        this.sectionMD = sectionMD;
    }

    @JsonProperty("steps")
    public ArrayList<ChallengeStepMDDto> getSteps() {
        return steps;
    }

    @JsonProperty("steps")
    public void setSteps(ArrayList<ChallengeStepMDDto> steps) {
        this.steps = steps;
    }
}

