package com.example.app.model;

/**
 * Konfiguration für einen Download
 */
public class DownloadConfig {
    private String url;
    private String downloadPath;
    private String videoFormat;
    private String audioFormat;

    public DownloadConfig(String url, String downloadPath, String videoFormat, String audioFormat) {
        this.url = url;
        this.downloadPath = downloadPath;
        this.videoFormat = videoFormat;
        this.audioFormat = audioFormat;
    }

    // Getter
    public String getUrl() {
        return url;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public String getVideoFormat() {
        return videoFormat;
    }

    public String getAudioFormat() {
        return audioFormat;
    }

    // Setter
    public void setUrl(String url) {
        this.url = url;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public void setVideoFormat(String videoFormat) {
        this.videoFormat = videoFormat;
    }

    public void setAudioFormat(String audioFormat) {
        this.audioFormat = audioFormat;
    }
}

