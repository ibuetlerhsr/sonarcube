package models.DataTransferObject.ImportedChallengeDtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TranslatedSectionDto {
    @JsonCreator
    public TranslatedSectionDto(
            @JsonProperty("id")String uuid,
            @JsonProperty("sectionMD")String sectionMD,
            @JsonProperty("order")int order,
            @JsonProperty("isUserCreated") boolean isUserCreated,
            @JsonProperty("steps")ArrayList<TranslatedStepDto> steps){
        this.setSectionMD(sectionMD);
        this.setSteps(steps);
        this.setId(uuid);
        this.setOrder(order);
        this.setIsUserCreated(isUserCreated);
    }

    private String id;
    private boolean isUserCreated;
    private String sectionMD;
    private int order;
    private ArrayList<TranslatedStepDto> steps;


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

    @JsonProperty("isUserCreated")
    public boolean getIsUserCreated() {
        return isUserCreated;
    }

    @JsonProperty("isUserCreated")
    public void setIsUserCreated(boolean isUserCreated) {
        this.isUserCreated = isUserCreated;
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
    public ArrayList<TranslatedStepDto> getSteps() {
        return steps;
    }

    @JsonProperty("steps")
    public void setSteps(ArrayList<TranslatedStepDto> steps) {
        this.steps = steps;
    }
}

