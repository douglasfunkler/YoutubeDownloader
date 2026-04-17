package com.example.app.util;

import java.util.Arrays;
import java.util.List;

public class FormatProvider {

    /**
     * Available video format/quality options
     */
    public static final List<FormatOption> VIDEO_FORMATS = Arrays.asList(
            new FormatOption("Best available", "bestvideo*+bestaudio/best"),
            new FormatOption("1080p (HD)", "bestvideo[height<=1080]+bestaudio/best[height<=1080]"),
            new FormatOption("720p (HD)", "bestvideo[height<=720]+bestaudio/best[height<=720]"),
            new FormatOption("480p (SD)", "bestvideo[height<=480]+bestaudio/best[height<=480]"),
            new FormatOption("360p (SD)", "bestvideo[height<=360]+bestaudio/best[height<=360]"),
            new FormatOption("Audio only", "bestaudio/best")
    );

    /**
     * Available audio format/container options
     */
    public static final List<FormatOption> AUDIO_FORMATS = Arrays.asList(
            new FormatOption("MP4 (MP4 Video)", "mp4"),
            new FormatOption("WebM (VP9/Vorbis)", "webm"),
            new FormatOption("MKV (Matroska)", "mkv"),
            new FormatOption("AVI (AVI)", "avi"),
            new FormatOption("MOV (QuickTime)", "mov")
    );

    /**
     * Returns the yt-dlp format string based on video and audio selection
     */
    public static String getFormatCommand(String videoFormatValue, String audioFormatValue) {
        // videoFormatValue already contains the complete yt-dlp format string
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

