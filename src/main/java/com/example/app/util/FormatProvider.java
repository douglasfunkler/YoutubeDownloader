package com.example.app.util;

import java.util.Arrays;
import java.util.List;

public class FormatProvider {

    /**
     * Available video/audio format/quality options
     */
    public static final List<FormatOption> VIDEO_FORMATS = Arrays.asList(
            new FormatOption("Best available", "bestvideo*+bestaudio/best"),
            new FormatOption("1080p (HD)", "bestvideo[height<=1080]+bestaudio/best[height<=1080]"),
            new FormatOption("720p (HD)", "bestvideo[height<=720]+bestaudio/best[height<=720]"),
            new FormatOption("480p (SD)", "bestvideo[height<=480]+bestaudio/best[height<=480]"),
            new FormatOption("360p (SD)", "bestvideo[height<=360]+bestaudio/best[height<=360]"),
            new FormatOption("Audio only", "bestaudio/best")
    );

    public static class FormatOption {
        private final String label;
        private final String value;

        public FormatOption(String label, String value) {
            this.label = label;
            this.value = value;
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

