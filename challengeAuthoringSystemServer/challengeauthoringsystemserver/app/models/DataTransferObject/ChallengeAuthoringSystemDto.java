package models.DataTransferObject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChallengeAuthoringSystemDto {

    private String _id;
    private String casID;
    private String casName;
    private String casLocation;
    private String casURL;
    private String resourceAPI;
    private String _created;

    @JsonCreator
    public ChallengeAuthoringSystemDto(
            @JsonProperty("_id")String uuid,
            @JsonProperty("CasID")String casID,
            @JsonProperty("CasName")String casName,
            @JsonProperty("CasLocation")String casLocation,
            @JsonProperty("CasURL")String casURL,
            @JsonProperty("ResourceAPI")String resourceAPI,
            @JsonProperty("_created")String createdAt) {
        this.set_id(uuid);
        this.setCasID(casID);
        this.setCasName(casName);
        this.setCasLocation(casLocation);
        this.setCasURL(casURL);
        this.setResourceAPI(resourceAPI);
        this.set_created(createdAt);
    }

    @JsonProperty("_id")
    public String get_id() {
        return _id;
    }

    @JsonProperty("_id")
    public void set_id(String _id) {
        this._id = _id;
    }

    @JsonProperty("CasID")
    public String getCasID() {
        return casID;
    }

    @JsonProperty("CasID")
    public void setCasID(String casID) {
        this.casID = casID;
    }

    @JsonProperty("CasName")
    public String getCasName() {
        return casName;
    }

    @JsonProperty("CasName")
    public void setCasName(String casName) {
        this.casName = casName;
    }

    @JsonProperty("CasLocation")
    public String getCasLocation() {
        return casLocation;
    }

    @JsonProperty("CasLocation")
    public void setCasLocation(String casLocation) {
        this.casLocation = casLocation;
    }

    @JsonProperty("CasURL")
    public String getCasURL() {
        return casURL;
    }

    @JsonProperty("CasURL")
    public void setCasURL (String casURL){
        this.casURL = casURL;
    }

    @JsonProperty("ResourceAPI")
    public String getResourceAPI() {
        return resourceAPI;
    }

    @JsonProperty("ResourceAPI")
    public void setResourceAPI(String resourceAPI) {
        this.resourceAPI = resourceAPI;
    }

    @JsonProperty("_created")
    public String get_created() {
        return _created;
    }
    @JsonProperty("_created")
    public void set_created(String _created) {
        this._created = _created;
    }
}
