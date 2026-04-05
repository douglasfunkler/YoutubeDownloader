package com.example.app.util;

import java.util.Arrays;
import java.util.List;

public class FormatProvider {

    /**
     * Verfügbare Video-Format/Qualitäts-Optionen
     */
    public static final List<FormatOption> VIDEO_FORMATS = Arrays.asList(
            new FormatOption("Bestes verfügbar", "bestvideo*+bestaudio/best"),
            new FormatOption("1080p (HD)", "bestvideo[height<=1080]+bestaudio/best[height<=1080]"),
            new FormatOption("720p (HD)", "bestvideo[height<=720]+bestaudio/best[height<=720]"),
            new FormatOption("480p (SD)", "bestvideo[height<=480]+bestaudio/best[height<=480]"),
            new FormatOption("360p (SD)", "bestvideo[height<=360]+bestaudio/best[height<=360]"),
            new FormatOption("Nur Audio", "bestaudio/best")
    );

    /**
     * Verfügbare Audio-Format/Container-Optionen
     */
    public static final List<FormatOption> AUDIO_FORMATS = Arrays.asList(
            new FormatOption("MP4 (MP4 Video)", "mp4"),
            new FormatOption("WebM (VP9/Vorbis)", "webm"),
            new FormatOption("MKV (Matroska)", "mkv"),
            new FormatOption("AVI (AVI)", "avi"),
            new FormatOption("MOV (QuickTime)", "mov")
    );

    /**
     * Gibt den yt-dlp Format-String basierend auf Video- und Audio-Auswahl zurück
     */
    public static String getFormatCommand(String videoFormatValue, String audioFormatValue) {
        // videoFormatValue enthält bereits den kompletten yt-dlp Format-String
        return videoFormatValue;
    }

    /**
     * Wrapper-Klasse für Format-Optionen
     */
    public static class FormatOption {
        private final String label;
        private final String value;

        public FormatOption(String label, String value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}

