package models.DatabaseObject.translation;

import io.ebean.Finder;
import models.DatabaseObject.BaseModel;
import models.DatabaseObject.challenge.Abstract;

import javax.persistence.*;

@Entity
public class CodeType extends BaseModel{
    private static final long serialVersionUID = 1L;
    private String codeName;

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }
}
