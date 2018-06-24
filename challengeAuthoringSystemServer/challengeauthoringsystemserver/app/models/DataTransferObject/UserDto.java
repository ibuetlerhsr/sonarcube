package models.DataTransferObject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {
    @JsonCreator
    public UserDto(
            @JsonProperty("username")String username,
            @JsonProperty("password")String password){
        this.setUserName(username);
        this.setPassword(password);
    }

    private String userName;
    private String password;

    @JsonProperty("username")
    public String getUserName() {
        return userName;
    }

    @JsonProperty("username")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
    }
}
