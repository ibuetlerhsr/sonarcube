package models.DatabaseObject.translation;

import io.ebean.Finder;
import models.DatabaseObject.BaseModel;

import javax.persistence.*;

@Entity
public class LanguageDefinition extends BaseModel{
    private static final long serialVersionUID = 1L;
    private TranslatableAttribute languageName;
    private String isoLangCode;

    @OneToOne
    @JoinColumn(name="languageName")
    public TranslatableAttribute getLanguageName() {
        return languageName;
    }

    public void setLanguageName(TranslatableAttribute languageName) {
        this.languageName = languageName;
    }

    @Column(name = "isoLangCode")
    public String getIsoLangCode() {
        return isoLangCode;
    }

    public void setIsoLangCode(String isoLangCode) {
        this.isoLangCode = isoLangCode;
    }
}
