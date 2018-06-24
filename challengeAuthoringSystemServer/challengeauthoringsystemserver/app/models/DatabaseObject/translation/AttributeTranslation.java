package models.DatabaseObject.translation;

import models.DatabaseObject.BaseModel;

import javax.persistence.*;

@Entity(name = "attributeTranslation")
public class AttributeTranslation extends BaseModel{
    private static final long serialVersionUID = 1L;
    private LanguageDefinition language;
    private TranslatableAttribute attribute;
    private String translation;
    private boolean userCreated;

    @ManyToOne
    @JoinColumn(name="languageId", nullable=false, insertable=true, updatable=true)
    public LanguageDefinition getLanguage() {
        return language;
    }

    public void setLanguage(LanguageDefinition language) {
        this.language = language;
    }

    @ManyToOne
    @JoinColumn(name="attributeId", nullable=false, insertable=true, updatable=true)
    public TranslatableAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(TranslatableAttribute attribute) {
        this.attribute = attribute;
    }

    @Column(name = "translation", columnDefinition = "LONGTEXT", insertable=true, updatable=true)
    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    @Column(name = "userCreated", nullable=false, insertable=true, updatable=true)
    public boolean isUserCreated() {
        return userCreated;
    }

    public void setUserCreated(boolean userCreated) {
        this.userCreated = userCreated;
    }
}
