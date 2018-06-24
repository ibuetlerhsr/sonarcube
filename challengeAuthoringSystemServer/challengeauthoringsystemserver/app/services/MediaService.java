package services;

import interfaces.repositories.*;
import models.DatabaseObject.challenge.MarkdownMediaReference;
import models.DatabaseObject.challenge.MediaObject;
import models.DatabaseObject.challenge.MediaReference;
import utils.ControllerUtil;

import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class MediaService {

    private final IMediaReferenceRepository mediaReferenceRepository;
    private final IMediaObjectRepository mediaObjectRepository;
    private final IMarkdownMediaReferenceRepository markdownMediaReferenceRepository;

    @Inject
    public MediaService(IMediaReferenceRepository mediaReferenceRepository,
                        IMediaObjectRepository mediaObjectRepository,
                        IMarkdownMediaReferenceRepository markdownMediaReferenceRepository) {
        this.mediaReferenceRepository = mediaReferenceRepository;
        this.mediaObjectRepository = mediaObjectRepository;
        this.markdownMediaReferenceRepository = markdownMediaReferenceRepository;
    }


    public Long operateOnTempFile(File file, String languageCode, String markdownType, String markdownId) throws IOException {
        Long mediaId = 0L;
        Base64.Encoder base64Encoder = Base64.getEncoder();

        String encodedfile = new String(base64Encoder.encode(Files.readAllBytes(file.toPath())), "UTF-8");
        if(markdownType == null)
            mediaId = this.createMediaEntry(encodedfile, file.getPath(), languageCode);
        else
            mediaId = this.createMarkdownMediaEntry(encodedfile, file.getPath(), languageCode, markdownType, markdownId);
        Files.deleteIfExists(file.toPath());
        return mediaId;
    }

    public Long createMediaFromBase64(String filePath, String base64Image, String languageCode, String markdownType, String markdownId) {
        Long mediaId = 0L;
        if(markdownType == null)
            mediaId = this.createMediaEntry(base64Image, filePath, languageCode);
        else
            mediaId = this.createMarkdownMediaEntry(base64Image, filePath, languageCode, markdownType, markdownId);
        return mediaId;
    }

    private String extractFileName(String path) {
        if(path == null)
            return null;
        if(path.contains("/")) {
            return path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf('.'));
        } else if(path.contains("\\")) {
            return path.substring(path.lastIndexOf('\\') + 1, path.lastIndexOf('.'));
        }
        return null;
    }

    private String extractFileExtension(String path) {
        if(path == null)
            return null;
        return path.substring(path.lastIndexOf('.') + 1);
    }

    public File writeMediaObjectToFile(String path, String content) {
        File newFile = null;
        String fileName = extractFileName(path);

        try {
            newFile = File.createTempFile(fileName, extractFileExtension(path));
            FileOutputStream imageOutFile = new FileOutputStream(newFile);
            byte[] imageByteArray = Base64.getDecoder().decode(content);
            imageOutFile.write(imageByteArray);
            imageOutFile.close();
        }  catch (FileNotFoundException e) {
            System.out.println("Image not found" + e);
        } catch (IOException ioe) {
            System.out.println("Exception while reading the Image " + ioe);
        }
        return newFile;
    }

    public Long createMarkdownMediaEntryByMediaReferenceAndKeys(String markdownId, String markdownType, MediaReference mediaReference) {
        MarkdownMediaReference markdownMediaReference = new MarkdownMediaReference();
        markdownMediaReference.setMarkdownId(markdownId);
        markdownMediaReference.setMarkdownType(markdownType);
        markdownMediaReference.setMediaReference(mediaReference);
        return insertMarkdownMediaReference(markdownMediaReference);
    }

    public Long createMarkdownMediaEntry(String content, String url, String isoLangCode, String markdownType, String markdownId) {
        Long markdownMediaReferenceId = 0L;
        Long mediaReferenceId = createMediaEntry(content, url, isoLangCode);
        MediaReference mediaReference = this.getMediaReferenceById(mediaReferenceId);
        MarkdownMediaReference markdownMediaReference = getMarkdownMediaReferenceByUrlAndKeys(markdownId, markdownType, url);
        if(markdownMediaReference == null) {
            markdownMediaReferenceId = createMarkdownMediaEntryByMediaReferenceAndKeys(markdownId, markdownType, mediaReference);
        }
        return markdownMediaReferenceId != 0L ? markdownMediaReferenceId : markdownMediaReference.getId();
    }

    public Long createMediaEntry(String content, String url, String isoLangCode) {
        Long mediaReferenceId = 0L;
        MediaObject mediaObject = this.getMediaObjectByUrlAndLanguage(url, isoLangCode);
        if(mediaObject == null) {
            MediaReference mediaReference = getMediaReferenceByUrl(url);
            if(mediaReference == null) {
                mediaReference = new MediaReference();
                mediaReference.setUrl(url);
                mediaReferenceId = this.insertMediaReference(mediaReference);
                mediaReference = this.getMediaReferenceById(mediaReferenceId);
            } else {
                mediaReferenceId = mediaReference.getId();
            }
            mediaObject = new MediaObject();
            mediaObject.setIsoLanguageCode(isoLangCode);
            mediaObject.setContent(content);
            mediaObject.setMediaReference(mediaReference);
            this.insertMediaObject(mediaObject);
        } else {
            mediaReferenceId = mediaObject.getMediaReference().getId();
            mediaObject.setContent(content);
            this.updateMediaObject(mediaObject);
        }

        return mediaReferenceId;
    }

    private Long insertMarkdownMediaReference(MarkdownMediaReference newMarkdownMediaReference) {
        CompletionStage<Long> id = this.markdownMediaReferenceRepository.insert(newMarkdownMediaReference);
        return ControllerUtil.handleCompletionStageLong(id);
    }

    MarkdownMediaReference getMarkdownMediaReferenceByUrlAndKeys(String markdownId, String markdownType, String url) {
        MediaReference mediaReference = getMediaReferenceByUrl(url);
        return this.markdownMediaReferenceRepository.find().query().where().eq("markdownId", markdownId).eq("markdownType", markdownType).eq("mediareferenceid", mediaReference.getId()).findUnique();
    }

    private Long insertMediaObject(MediaObject newMediaObject) {
        CompletionStage<Long> id = this.mediaObjectRepository.insert(newMediaObject);
        return ControllerUtil.handleCompletionStageLong(id);
    }

    private Long insertMediaReference(MediaReference newMediaReference) {
        CompletionStage<Long> id = this.mediaReferenceRepository.insert(newMediaReference);
        return ControllerUtil.handleCompletionStageLong(id);
    }

    private Long updateMediaObject(MediaObject mediaObject) {
        CompletionStage<Optional<Long>> id = this.mediaObjectRepository.update(mediaObject.getId(), mediaObject);
        return ControllerUtil.handleCompletionStageOptionalLong(id);
    }

    private Long updateMediaReference(MediaReference mediaReference) {
        CompletionStage<Optional<Long>> id = this.mediaReferenceRepository.update(mediaReference.getId(), mediaReference);
        return ControllerUtil.handleCompletionStageOptionalLong(id);
    }

    public MediaObject getMediaObjectById(Long mediaObjectId) {
        return this.mediaObjectRepository.find().query().where().eq("id", mediaObjectId).findUnique();
    }

    public MediaObject getMediaObjectByUrlAndLanguage(String url, String isoLanguageCode) {
        MediaReference mediaReference = null;
        if(url.contains("media"))
            mediaReference = getMediaReferenceByUrl(url);
        else
            mediaReference = getMediaReferenceByUrl("\\media\\" + url);
        if(mediaReference == null)
            return null;
        return this.mediaObjectRepository.find().query().where().eq("isolanguagecode", isoLanguageCode).eq("mediareferenceid", mediaReference.getId()).findUnique();
    }

    public MediaReference getMediaReferenceByUrl(String url) {
        if(url != null && url.contains("/")) {
            url = url.replace("/", "\\");
        }
        return this.mediaReferenceRepository.find().query().where().eq("url", url).findUnique();
    }

    public MediaReference getMediaReferenceById(Long id) {
        return this.mediaReferenceRepository.find().query().where().eq("id", id).findUnique();
    }

    public List<MediaReference> getMediaReferencesByMarkdownKeys(String markdownType, String markdownId) {
        List<MediaReference> mediaReferences = new ArrayList<>();
        List<MarkdownMediaReference> markdownMediaReferences = this.markdownMediaReferenceRepository.find().query().where().
                eq("markdownType", markdownType).
                eq("markdownId", markdownId).findList();
        if(markdownMediaReferences == null || markdownMediaReferences.size() == 0)
            return null;
        for(MarkdownMediaReference markdownMediaReference : markdownMediaReferences) {
            mediaReferences.add(markdownMediaReference.getMediaReference());
        }
        return mediaReferences;
    }

    public MarkdownMediaReference getSpecificMarkdownMediaReference(String markdownType, String markdownId, Long mediaReferenceId) {
        MediaReference mediaReference = this.getMediaReferenceById(mediaReferenceId);
        if(mediaReference == null)
            return null;
        return this.markdownMediaReferenceRepository.find().query().where().
                eq("markdownType", markdownType).
                eq("markdownId", markdownId).
                eq("mediareferenceid", mediaReference.getId()).findUnique();
    }

    public void removeMediaObjectByReference(MediaReference mediaReference) {
        for(MediaObject mediaObject : this.mediaObjectRepository.find().query().where().eq("mediareferenceid", mediaReference.getId()).findList()) {
            this.mediaObjectRepository.delete(mediaObject.getId());
        }
    }

    public void removeMediaReferenceById(Long id) {
        this.mediaReferenceRepository.delete(id);
    }
}
