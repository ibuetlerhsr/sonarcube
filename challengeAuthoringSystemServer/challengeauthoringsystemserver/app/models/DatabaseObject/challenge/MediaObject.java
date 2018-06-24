package models.DatabaseObject.challenge;

import io.ebean.Finder;
import models.DatabaseObject.BaseModel;

import javax.persistence.*;
import java.util.List;

@Entity
public class MediaObject extends BaseModel {
    private static final long serialVersionUID = 1L;
    private String content;
    private String isoLanguageCode;
    private MediaReference mediaReference;


    public static Finder<Integer,MediaObject> find = new Finder<>(MediaObject.class);


    @Column(name="isoLanguageCode")
    public String getIsoLanguageCode() {
        return isoLanguageCode;
    }

    public void setIsoLanguageCode(String isoLanguageCode) {
        this.isoLanguageCode = isoLanguageCode;
    }


    @Column(name = "content", columnDefinition = "LONGTEXT")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
