package models.DatabaseObject.user;

import io.ebean.Finder;
import models.DatabaseObject.BaseModel;

import javax.persistence.*;
import java.sql.Date;

@Entity
public class Message extends BaseModel {
    private static final long serialVersionUID = 1L;
    private String text;
    private Date dateCreated;
    private UserCAS user;

    public static Finder<Integer,Message> find = new Finder<>(Message.class);

    @Column(name="text")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Column(name="dateCreated")
    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date date) {
        this.dateCreated = date;
    }

    @ManyToOne
    @JoinColumn(name="userId", nullable=false, insertable=true, updatable=true)
    public UserCAS getUser() {
        return user;
    }

    public void setUser(UserCAS user) {
        this.user = user;
    }
}
