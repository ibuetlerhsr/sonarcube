package models.DatabaseObject.translation;

import models.DatabaseObject.BaseModel;

import javax.persistence.*;

@Entity
public class TranslatableAttribute extends BaseModel {
    private static final long serialVersionUID = 1L;
    private CodeType codeType;

    @ManyToOne
    @JoinColumn(name="codeTypeId", nullable=false, insertable=true, updatable=true)
    public CodeType getCodeType() {
        return codeType;
    }

    public void setCodeType(CodeType codeType) {
        this.codeType = codeType;
    }
}
