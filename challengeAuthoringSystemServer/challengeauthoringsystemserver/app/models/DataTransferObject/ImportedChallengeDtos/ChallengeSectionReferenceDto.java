package models.DataTransferObject.ImportedChallengeDtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChallengeSectionReferenceDto {
    @JsonCreator
    public ChallengeSectionReferenceDto(
            @JsonProperty("id")String uuid,
            @JsonProperty("order")int order,
            @JsonProperty("steps")ArrayList<ChallengeStepReferenceDto> steps){
        this.setOrder(order);
        this.setSteps(steps);
        this.setId(uuid);
    }

    private String id;
    private int order;
    private ArrayList<ChallengeStepReferenceDto> steps;


    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("order")
    public int getOrder() {
        return order;
    }

    @JsonProperty("order")
    public void setOrder(int order) {
        this.order = order;
    }

    @JsonProperty("steps")
    public ArrayList<ChallengeStepReferenceDto> getSteps() {
        return steps;
    }

    @JsonProperty("steps")
    public void setSteps(ArrayList<ChallengeStepReferenceDto> steps) {
        this.steps = steps;
    }
}
