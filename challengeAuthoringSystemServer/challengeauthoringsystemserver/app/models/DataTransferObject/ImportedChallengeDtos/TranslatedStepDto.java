package models.DataTransferObject.ImportedChallengeDtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TranslatedStepDto {
    @JsonCreator
    public TranslatedStepDto(
            @JsonProperty("id")String uuid,
            @JsonProperty("type")String type,
            @JsonProperty("order")int order,
            @JsonProperty("isUserCreated") boolean isUserCreated,
            @JsonProperty("stepMD")String stepMD){
        this.setStepMD(stepMD);
        this.setId(uuid);
        this.setOrder(order);
        this.setType(type);
        this.setIsUserCreated(isUserCreated);
    }

    private String id;
    private boolean isUserCreated;
    private int order;
    private String type;
    private String stepMD;


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

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("isUserCreated")
    public boolean getIsUserCreated() {
        return isUserCreated;
    }

    @JsonProperty("isUserCreated")
    public void setIsUserCreated(boolean isUserCreated) {
        this.isUserCreated = isUserCreated;
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

