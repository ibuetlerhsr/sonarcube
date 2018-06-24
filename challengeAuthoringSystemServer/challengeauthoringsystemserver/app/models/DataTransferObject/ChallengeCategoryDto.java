package models.DataTransferObject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ChallengeCategoryDto {

    @JsonCreator
    public ChallengeCategoryDto(
            @JsonProperty("id")Long id,
            @JsonProperty("categoryName")String categoryName){
        this.setId(id);
        this.setCategoryName(categoryName);
    }

    private Long id;
    private String categoryName;

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("categoryName")
    public String getCategoryName() {
        return categoryName;
    }

    @JsonProperty("categoryName")
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
