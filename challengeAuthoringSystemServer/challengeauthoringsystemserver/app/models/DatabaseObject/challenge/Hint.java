package models.DatabaseObject.challenge;

import interfaces.IOrderableMarkdownItem;
import io.ebean.Model;
import models.DatabaseObject.translation.TranslatableAttribute;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class  Hint extends Model implements IOrderableMarkdownItem {
    private static final long serialVersionUID = 1L;
    private String id;
    private int stepOrder;
    private Integer version;
    private Section section;
    private TranslatableAttribute text;
    private List<MarkdownMediaReference> markdownMediaReference  = new ArrayList<>();

    @Id
    public String getId() {
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name="sectionId")
    public Section getSection(){
        return section;
    }

    public void setSection(Section section){
        this.section = section;
    }

    @OneToOne
    @JoinColumn(name="text", nullable=true, insertable=true, updatable=true)
    public TranslatableAttribute getText() {
        return text;
    }

    public void setText(TranslatableAttribute text) {
        this.text = text;
    }

    @Version
    @Column(name="version")
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Column(name="stepOrder")
    public int getOrder() {
        return stepOrder;
    }

    public void setOrder(int stepOrder) {
        this.stepOrder = stepOrder;
    }

    @OneToMany(mappedBy = "hint")
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
