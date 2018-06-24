package models.DatabaseObject.user;

import javax.persistence.*;

import io.ebean.*;
import models.DatabaseObject.BaseModel;

import java.util.Collection;
import java.util.List;

@Entity
public class Role extends BaseModel{
    private static final long serialVersionUID = 1L;
    private String roleName;
    private List<UserMandantRole> userMandantRoles;

    public static Finder<Integer,Role> find = new Finder<>(Role.class);

    @Column(name="roleName")
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @OneToMany(mappedBy = "role")
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
