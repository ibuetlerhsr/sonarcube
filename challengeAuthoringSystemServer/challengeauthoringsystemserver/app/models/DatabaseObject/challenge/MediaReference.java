package models.DatabaseObject.challenge;

import io.ebean.Finder;
import models.DatabaseObject.BaseModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class MediaReference extends BaseModel {
    private static final long serialVersionUID = 1L;
    private String url;
    private List<MediaObject> mediaObjects;
    private List<MarkdownMediaReference> markdownMediaReferences;
    private List<ChallengeVersion> challengeVersions;


    public static Finder<Integer,MediaReference> find = new Finder<>(MediaReference.class);

    @Column(name="url")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url.replace("/", "\\").toLowerCase();
    }

    @OneToMany(mappedBy="mediaReference")
    public List<MediaObject> getMediaObjects(){
        return mediaObjects;
    }

    public void setMediaObjects(List<MediaObject> mediaObjects){
        this.mediaObjects = mediaObjects;
    }

    public void addMediaObject(MediaObject newMediaObject) {
        this.mediaObjects.add(newMediaObject);
    }

    public Long removeMediaObject(Long mediaObjectId) {
        MediaObject mediaObject = this.findMediaObjectById(mediaObjectId);
        this.mediaObjects.remove(mediaObject);
        return mediaObject.getId();
    }

    private MediaObject findMediaObjectById(Long mediaObjectId){
        for (MediaObject mediaObject: mediaObjects) {
            if(mediaObject.getId().equals(mediaObjectId))
                return mediaObject;
        }
        return null;
    }

    @OneToMany(mappedBy="mediaReference")
    public List<MarkdownMediaReference> getMarkdownMediaReferences(){
        return markdownMediaReferences;
    }

    public void setMarkdownMediaReferences(List<MarkdownMediaReference> markdownMediaReferences){
        this.markdownMediaReferences = markdownMediaReferences;
    }

    public void addMarkdownMediaReference(MarkdownMediaReference newMarkdownMediaReference) {
        this.markdownMediaReferences.add(newMarkdownMediaReference);
    }

    public Long removeMarkdownMediaReference(Long markdownMediaReferenceId) {
        MarkdownMediaReference markdownMediaReference = this.findMarkdownMediaReferenceById(markdownMediaReferenceId);
        this.markdownMediaReferences.remove(markdownMediaReference);
        return markdownMediaReference.getId();
    }

    private MarkdownMediaReference findMarkdownMediaReferenceById(Long markdownMediaReferenceId){
        for (MarkdownMediaReference markdownMediaReference: markdownMediaReferences) {
            if(markdownMediaReference.getId().equals(markdownMediaReferenceId))
                return markdownMediaReference;
        }
        return null;
    }

    @OneToMany(mappedBy="mediaReference")
    public List<ChallengeVersion> getChallengeVersions(){
        return challengeVersions;
    }

    public void setChallengeVersions(List<ChallengeVersion> challengeVersions){
        this.challengeVersions = challengeVersions;
    }

    public void addChallengeVersion(ChallengeVersion newChallengeVersion) {
        this.challengeVersions.add(newChallengeVersion);
    }

    public Long removeChallengeVersion(Long challengeVersionId) {
        ChallengeVersion challengeVersion = this.findChallengeVersionById(challengeVersionId);
        this.challengeVersions.remove(challengeVersion);
        return challengeVersion.getId();
    }

    private ChallengeVersion findChallengeVersionById(Long challengeVersionId){
        for (ChallengeVersion challengeVersion: challengeVersions) {
            if(challengeVersion.getId().equals(challengeVersionId))
                return challengeVersion;
        }
        return null;
    }
}
