package models.DatabaseObject.challenge;

import interfaces.ITranslatableAttribute;
import io.ebean.Finder;
import models.DatabaseObject.BaseModel;
import models.DatabaseObject.translation.TranslatableAttribute;

import javax.persistence.*;

@Entity
public class ChallengeStatus extends BaseModel implements ITranslatableAttribute {
    private static final long serialVersionUID = 1L;
    private TranslatableAttribute text;
    private int statusOrder;

    public static Finder<Integer,ChallengeStatus> find = new Finder<>(ChallengeStatus.class);

    @OneToOne
    @JoinColumn(name="text", nullable=true, insertable=true, updatable=true)
    public TranslatableAttribute getText() {
        return text;
    }

    public void setText(TranslatableAttribute text) {
        this.text = text;
    }

    @Column(name="statusOrder")
    public int getStatusOrder() {
        return statusOrder;
    }

    public void setStatusOrder(int order) {
        this.statusOrder = order;
    }
}
