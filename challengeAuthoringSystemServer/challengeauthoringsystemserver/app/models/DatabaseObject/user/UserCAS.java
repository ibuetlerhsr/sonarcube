package models.DatabaseObject.user;

import javax.persistence.*;

import io.ebean.*;
import models.DatabaseObject.BaseModel;
import play.data.validation.Constraints;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Entity
public class UserCAS extends BaseModel{
    private static final long serialVersionUID = 1L;
    private String name;
    private String firstName;
    private String languageIsoCode;
    private double salary;
    private List<Message> messages;
    private List<UserMandantRole> userMandantRoles;


    @Constraints.Required
    private String username;
    private String authToken;

    @Column(name = "username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "firstname")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "lanugageIsoCode")
    public String getLanguageIsoCode() {
        return languageIsoCode;
    }

    public void setLanguageIsoCode(String languageIsoCode) {
        this.languageIsoCode = languageIsoCode;
    }

    @Column(name = "salary")
    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }


    @Column(name = "authToken")
    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    @OneToMany(mappedBy = "user")
    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages){
        this.messages = messages;
    }

    public void addMessage(Message message){
        messages.add(message);
    }

    public Message removeMessage(long messageId){
        Message messageToRemove = findMessageById(messageId);
        messages.remove(messageToRemove);
        return messageToRemove;
    }

    private Message findMessageById(long messageId){
        for (Message message: messages) {
            if(message.getId() == messageId)
                return message;
        }
        return null;
    }

    @OneToMany(mappedBy = "user")
    public List<UserMandantRole> getUserMandantRoles() {
        return userMandantRoles;
    }

    public void setUserMandantRoles(List<UserMandantRole> userMandantRoles){
        this.userMandantRoles = userMandantRoles;
    }

    public void addUserMandantRole(UserMandantRole userMandantRole){
        userMandantRoles.add(userMandantRole);
    }

    public UserMandantRole removeUserMandantRole(long userMandantRoleId){
        UserMandantRole userToRemove = findUserMandantRoleById(userMandantRoleId);
        userMandantRoles.remove(userToRemove);
        return userToRemove;
    }

    private UserMandantRole findUserMandantRoleById(long userMandantRoleId){
        for (UserMandantRole userMandantRole: userMandantRoles) {
            if(userMandantRole.getId() == userMandantRoleId)
                return userMandantRole;
        }
        return null;
    }

    public void incrementSalary(double value){
        this.salary += value;
    }

    public String getConnectedName(){
        return getFirstName()+ " " + getName();
    }
}
