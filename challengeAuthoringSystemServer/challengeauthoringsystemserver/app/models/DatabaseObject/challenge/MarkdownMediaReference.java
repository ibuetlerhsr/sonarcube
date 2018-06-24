package models.DatabaseObject.challenge;

import io.ebean.Finder;
import models.DatabaseObject.BaseModel;

import javax.persistence.*;
import java.util.List;

@Entity
public class MarkdownMediaReference extends BaseModel {
    private static final long serialVersionUID = 1L;
    private String markdownId;
    private String markdownType;
    private MediaReference mediaReference;


    public static Finder<Integer,MarkdownMediaReference> find = new Finder<>(MarkdownMediaReference.class);

    @Column(name="markdownId")
    public String getMarkdownId() {
        return markdownId;
    }

    public void setMarkdownId(String markdownId) {
        this.markdownId = markdownId;
    }

    @Column(name="markdownType")
    public String getMarkdownType() {
        return markdownType;
    }

    public void setMarkdownType(String markdownType) {
        this.markdownType = markdownType;
    }


    @ManyToOne
    @JoinColumn(name="mediaReferenceId", nullable=true, insertable=true, updatable=true)
    public MediaReference getMediaReference() {
        return mediaReference;
    }

    public void setMediaReference(MediaReference mediaReference) {
        this.mediaReference = mediaReference;
    }
}
