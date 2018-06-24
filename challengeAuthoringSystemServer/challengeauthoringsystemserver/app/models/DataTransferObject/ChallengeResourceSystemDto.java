package models.DataTransferObject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChallengeResourceSystemDto {

    private String _id;
    private String resourceID;
    private String resourceName;
    private String resourceLocation;
    private String resourceRegion;
    private String resourceURL;
    private String resourceAPI;
    private String _created;

    @JsonCreator
    public ChallengeResourceSystemDto(
            @JsonProperty("_id")String uuid,
            @JsonProperty("ResourceID")String resourceID,
            @JsonProperty("ResourceName")String resourceName,
            @JsonProperty("ResourceLocation")String resourceLocation,
            @JsonProperty("ResourceRegion")String resourceRegion,
            @JsonProperty("ResourceURL")String resourceURL,
            @JsonProperty("ResourceAPI")String resourceAPI,
            @JsonProperty("_created")String createdAt) {
        this.set_id(uuid);
        this.setResourceID(resourceID);
        this.setResourceName(resourceName);
        this.setResourceLocation(resourceLocation);
        this.setResourceRegion(resourceRegion);
        this.setResourceURL(resourceURL);
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

    @JsonProperty("ResourceID")
    public String getResourceID() {
        return resourceID;
    }

    @JsonProperty("ResourceID")
    public void setResourceID(String resourceID) {
        this.resourceID = resourceID;
    }

    @JsonProperty("ResourceName")
    public String getResourceName() {
        return resourceName;
    }

    @JsonProperty("ResourceName")
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    @JsonProperty("ResourceLocation")
    public String getResourceLocation() {
        return resourceLocation;
    }

    @JsonProperty("ResourceLocation")
    public void setResourceLocation(String resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    @JsonProperty("ResourceRegion")
    public String getResourceRegion() {
        return resourceRegion;
    }

    @JsonProperty("ResourceRegion")
    public void setResourceRegion(String resourceRegion) {
        this.resourceRegion = resourceRegion;
    }

    @JsonProperty("ResourceURL")
    public String getResourceURL() {
        return resourceURL;
    }

    @JsonProperty("ResourceURL")
    public void setResourceURL(String resourceURL) {
        this.resourceURL = resourceURL;
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
