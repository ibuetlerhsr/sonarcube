package models.DataTransferObject.ImportedChallengeDtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChallengeStepMDDto {
    @JsonCreator
    public ChallengeStepMDDto(
            @JsonProperty("id")String uuid,
            @JsonProperty("stepMD")String stepMD){
        this.setStepMD(stepMD);
        this.setId(uuid);
    }

    private String id;
    private String stepMD;


    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("stepMD")
    public String getStepMD() {
        return stepMD;
    }

    @JsonProperty("stepMD")
    public void setStepMD(String stepMD) {
        this.stepMD = stepMD;
    }
}

