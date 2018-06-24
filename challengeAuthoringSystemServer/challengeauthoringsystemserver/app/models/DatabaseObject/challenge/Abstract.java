package models.DatabaseObject.challenge;

import interfaces.ITranslatableAttribute;
import io.ebean.Finder;
import models.DatabaseObject.BaseModel;
import models.DatabaseObject.translation.TranslatableAttribute;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Abstract extends BaseModel implements ITranslatableAttribute {
    private static final long serialVersionUID = 1L;
    private TranslatableAttribute text;
    private List<MarkdownMediaReference> markdownMediaReference = new ArrayList<>();

    public static Finder<Integer,Abstract> find = new Finder<>(Abstract.class);

    @OneToOne
    @JoinColumn(name="text", nullable=true, insertable=true, updatable=true)
    public TranslatableAttribute getText() {
        return text;
    }

    public void setText(TranslatableAttribute text) {
        this.text = text;
    }

    @OneToMany(mappedBy = "anAbstract")
    public List<MarkdownMediaReference> getMarkdownMediaReferences() {
        return markdownMediaReference;
    }

    public void setMarkdownMediaReferences(List<MarkdownMediaReference> markdownMediaReference){
        this.markdownMediaReference = markdownMediaReference;
    }

    public void addMarkdownMediaReference(MarkdownMediaReference markdownMediaReference){
        this.markdownMediaReference.add(markdownMediaReference);
    }

    public MarkdownMediaReference removeMarkdownMediaReference(long markdownMediaReferenceId){
        MarkdownMediaReference markdownMediaReferenceToRemove = findMarkdownMediaReferenceById(markdownMediaReferenceId);
        markdownMediaReference.remove(markdownMediaReferenceToRemove);
        return markdownMediaReferenceToRemove;
    }

    private MarkdownMediaReference findMarkdownMediaReferenceById(long markdownMediaReferenceId){
        for (MarkdownMediaReference markdownMediaReference: markdownMediaReference) {
            if(markdownMediaReference.getId() == markdownMediaReferenceId)
                return markdownMediaReference;
        }
        return null;
    }
}
