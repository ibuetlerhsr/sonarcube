package models.DataTransferObject.ImportedChallengeDtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChallengeMediaContainerDto {
    @JsonCreator
    public ChallengeMediaContainerDto(
            @JsonProperty("png")ArrayList<ChallengeMediaDto> pngFiles,
            @JsonProperty("jpg")ArrayList<ChallengeMediaDto> jpgFiles,
            @JsonProperty("jpeg")ArrayList<ChallengeMediaDto> jpegFiles,
            @JsonProperty("gif")ArrayList<ChallengeMediaDto> gifFiles,
            @JsonProperty("svg")ArrayList<ChallengeMediaDto> svgFiles,
            @JsonProperty("mpeg")ArrayList<ChallengeMediaDto> mpegFiles,
            @JsonProperty("wav")ArrayList<ChallengeMediaDto> wavFiles,
            @JsonProperty("mp3")ArrayList<ChallengeMediaDto> mp3Files,
            @JsonProperty("mp4")ArrayList<ChallengeMediaDto> mp4Files){
        this.setPngFiles(pngFiles);
        this.setJpgFiles(jpgFiles);
        this.setJpegFiles(jpegFiles);
        this.setGifFiles(gifFiles);
        this.setSvgFiles(svgFiles);
        this.setMpegFiles(mpegFiles);
        this.setMp3Files(mp3Files);
        this.setMp4Files(mp4Files);
    }

    private ArrayList<ChallengeMediaDto> pngFiles;
    private ArrayList<ChallengeMediaDto> jpgFiles;
    private ArrayList<ChallengeMediaDto> jpegFiles;
    private ArrayList<ChallengeMediaDto> gifFiles;
    private ArrayList<ChallengeMediaDto> svgFiles;
    private ArrayList<ChallengeMediaDto> mpegFiles;
    private ArrayList<ChallengeMediaDto> wavFiles;
    private ArrayList<ChallengeMediaDto> mp3Files;
    private ArrayList<ChallengeMediaDto> mp4Files;

    @JsonProperty("png")
    public ArrayList<ChallengeMediaDto> getPngFiles() {
        return pngFiles;
    }

    @JsonProperty("png")
    public void setPngFiles(ArrayList<ChallengeMediaDto> pngFiles) {
        this.pngFiles = pngFiles;
    }

    @JsonProperty("jpg")
    public ArrayList<ChallengeMediaDto> getJpgFiles() {
        return jpgFiles;
    }

    @JsonProperty("jpg")
    public void setJpgFiles(ArrayList<ChallengeMediaDto> jpgFiles) {
        this.jpgFiles = jpgFiles;
    }

    @JsonProperty("jpeg")
    public ArrayList<ChallengeMediaDto> getJpegFiles() {
        return jpegFiles;
    }

    @JsonProperty("jpeg")
    public void setJpegFiles(ArrayList<ChallengeMediaDto> jpegFiles) {
        this.jpegFiles = jpegFiles;
    }

    @JsonProperty("gif")
    public ArrayList<ChallengeMediaDto> getGifFiles() {
        return gifFiles;
    }

    @JsonProperty("gif")
    public void setGifFiles(ArrayList<ChallengeMediaDto> gifFiles) {
        this.gifFiles = gifFiles;
    }

    @JsonProperty("svg")
    public ArrayList<ChallengeMediaDto> getSvgFiles() {
        return svgFiles;
    }

    @JsonProperty("svg")
    public void setSvgFiles(ArrayList<ChallengeMediaDto> svgFiles) {
        this.svgFiles = svgFiles;
    }

    @JsonProperty("mpeg")
    public ArrayList<ChallengeMediaDto> getMpegFiles() {
        return mpegFiles;
    }

    @JsonProperty("mpeg")
    public void setMpegFiles(ArrayList<ChallengeMediaDto> mpegFiles) {
        this.mpegFiles = mpegFiles;
    }

    @JsonProperty("wav")
    public ArrayList<ChallengeMediaDto> getWavFiles() {
        return wavFiles;
    }

    @JsonProperty("wav")
    public void setWavFiles(ArrayList<ChallengeMediaDto> wavFiles) {
        this.wavFiles = wavFiles;
    }

    @JsonProperty("mp3")
    public ArrayList<ChallengeMediaDto> getMp3Files() {
        return mp3Files;
    }

    @JsonProperty("mp3")
    public void setMp3Files(ArrayList<ChallengeMediaDto> mp3Files) {
        this.mp3Files = mp3Files;
    }

    @JsonProperty("mp4")
    public ArrayList<ChallengeMediaDto> getMp4Files() {
        return mp4Files;
    }

    @JsonProperty("mp4")
    public void setMp4Files(ArrayList<ChallengeMediaDto> mp4Files) {
        this.mp4Files = mp4Files;
    }
}

