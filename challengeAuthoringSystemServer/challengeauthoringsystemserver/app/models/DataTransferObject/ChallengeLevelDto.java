package models.DataTransferObject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChallengeLevelDto {
    @JsonCreator
    public ChallengeLevelDto(
            @JsonProperty("id")Long id,
            @JsonProperty("text")String text,
            @JsonProperty("maxPoints")int maxPoints){
        this.setId(id);
        this.setLevelText(text);
        this.setMaxPoints(maxPoints);
    }

    private Long id;
    private String levelText;
    private int maxPoints;

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("text")
    public String getLevelText() {
        return levelText;
    }

    @JsonProperty("text")
    public void setLevelText(String levelText) {
        this.levelText = levelText;
    }

    @JsonProperty("maxPoints")
    public int getMaxPoints() {
        return maxPoints;
    }

    @JsonProperty("maxPoints")
    public void setMaxPoints(int maxPoints) {
        this.maxPoints = maxPoints;
    }
}
