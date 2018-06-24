package models.DataTransferObject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsumerDto {

    @JsonCreator
    public ConsumerDto(
            @JsonProperty("_id")String uuid,
            @JsonProperty("ConsumerName")String consumerName,
            @JsonProperty("ConsumerCountry")String consumerCountry,
            @JsonProperty("ConsumerCity")String consumerCity,
            @JsonProperty("ConsumerURL")String consumerURL,
            @JsonProperty("ConsumerAPI")String consumerAPI,
            @JsonProperty("ConsumerPublicKey")String consumerPublicKey) {
        setConsumerName(consumerName);
        setConsumerCountry(consumerCountry);
        setConsumerCity(consumerCity);
        setConsumerURL(consumerURL);
        setConsumerAPI(consumerAPI);
        setConsumerPublicKey(consumerPublicKey);
    }

    private String _id;
    private String consumerName;
    private String consumerCountry;
    private String consumerCity;
    private String consumerURL;
    private String consumerAPI;
    private String consumerPublicKey;


    @JsonProperty("_id")
    public String get_id(){
        return _id;
    }

    @JsonProperty("_id")
    public void set_id(String id){
        this._id  = id;
    }

    @JsonProperty("ConsumerName")
    public String getConsumerName() {
        return consumerName;
    }

    @JsonProperty("ConsumerName")
    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }

    @JsonProperty("ConsumerCountry")
    public String getConsumerCountry() {
        return consumerCountry;
    }

    @JsonProperty("ConsumerCountry")
    public void setConsumerCountry(String consumerCountry) {
        this.consumerCountry = consumerCountry;
    }

    @JsonProperty("ConsumerCity")
    public String getConsumerCity() {
        return consumerCity;
    }

    @JsonProperty("ConsumerCity")
    public void setConsumerCity(String consumerCity) {
        this.consumerCity = consumerCity;
    }

    @JsonProperty("ConsumerURL")
    public String getConsumerURL() {
        return consumerURL;
    }

    @JsonProperty("ConsumerURL")
    public void setConsumerURL(String consumerURL) {
        this.consumerURL = consumerURL;
    }

    @JsonProperty("ConsumerAPI")
    public String getConsumerAPI() {
        return consumerAPI;
    }

    @JsonProperty("ConsumerAPI")
    public void setConsumerAPI(String consumerAPI) {
        this.consumerAPI = consumerAPI;
    }

    @JsonProperty("ConsumerPublicKey")
    public String getConsumerPublicKey() {
        return consumerPublicKey;
    }

    @JsonProperty("ConsumerPublicKey")
    public void setConsumerPublicKey(String consumerPublicKey) {
        this.consumerPublicKey = consumerPublicKey;
    }
}
