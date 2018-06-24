package models.DatabaseObject.user;

import io.ebean.Finder;
import models.DatabaseObject.BaseModel;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Entity
public class MandantCCS extends BaseModel{
    private static final long serialVersionUID = 1L;
    private String mandantName;
    private List<UserMandantRole> userMandantRoles;


    public static Finder<Integer,MandantCCS> find = new Finder<>(MandantCCS.class);

    @Column(name="name")
    public String getMandantName() {
        return mandantName;
    }

    public void setMandantName(String ownerName) {
        this.mandantName = ownerName;
    }

    @OneToMany(mappedBy = "mandant")
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
}
