package models.DatabaseObject.challenge;

import interfaces.ITranslatableAttribute;
import io.ebean.Finder;
import models.DatabaseObject.BaseModel;
import models.DatabaseObject.translation.TranslatableAttribute;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ChallengeType extends BaseModel implements ITranslatableAttribute {
    private static final long serialVersionUID = 1L;
    private TranslatableAttribute text;

    public static Finder<Integer,ChallengeType> find = new Finder<>(ChallengeType.class);

    @OneToOne
    @JoinColumn(name="text", nullable=true, insertable=true, updatable=true)
    public TranslatableAttribute getText() {
        return text;
    }

    public void setText(TranslatableAttribute text) {
        this.text = text;
    }
}
