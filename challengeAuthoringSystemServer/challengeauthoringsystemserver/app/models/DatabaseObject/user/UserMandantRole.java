package models.DatabaseObject.user;

import io.ebean.Finder;
import models.DatabaseObject.BaseModel;

import javax.persistence.*;

@Entity
public class UserMandantRole extends BaseModel {
    private static final long serialVersionUID = 1L;
    private Role role;
    private MandantCCS mandant;
    private UserCAS user;


    public static Finder<Integer,UserMandantRole> find = new Finder<>(UserMandantRole.class);

    @ManyToOne
    @JoinColumn(name="userId", nullable=false)
    public UserCAS getUser() {
        return user;
    }

    public void setUser(UserCAS user) {
        this.user = user;
    }

    @ManyToOne
    @JoinColumn(name="mandantId", nullable=false)
    public MandantCCS getMandant() {
        return mandant;
    }

    public void setMandant(MandantCCS mandant) {
        this.mandant = mandant;
    }

    @ManyToOne
    @JoinColumn(name="roleId", nullable=false)
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
