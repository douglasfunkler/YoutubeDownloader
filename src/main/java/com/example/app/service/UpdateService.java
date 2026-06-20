package com.example.app.service;

import java.io.*;
import java.net.*;
import java.nio.file.*;

public class UpdateService {

    private static final String RELEASES_LATEST_URL = "https://github.com/yt-dlp/yt-dlp/releases/latest";
    private static final String DOWNLOAD_URL_TEMPLATE = "https://github.com/yt-dlp/yt-dlp/releases/download/%s/yt-dlp.exe";
    private static final String EXE_PATH = "yt-dlp.exe";

    public void updateYtDlp() throws Exception {
        final String version = resolveLatestVersion();
        downloadAndReplace(String.format(DOWNLOAD_URL_TEMPLATE, version));
    }

    private String resolveLatestVersion() throws Exception {
        final HttpURLConnection conn = (HttpURLConnection) new URL(RELEASES_LATEST_URL).openConnection();
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("HEAD");
        conn.setRequestProperty("User-Agent", "yt-dlp-desktop");
        conn.setConnectTimeout(10_000);
        conn.setReadTimeout(10_000);
        conn.connect();

        final int status = conn.getResponseCode();
        if (status < 300 || status > 399) {
            throw new Exception("Expected redirect from GitHub, got: " + status);
        }
        final String location = conn.getHeaderField("Location");
        if (location == null) {
            throw new Exception("No redirect location from GitHub releases");
        }
        // location: https://github.com/yt-dlp/yt-dlp/releases/tag/2026.06.09
        final String[] parts = location.split("/");
        return parts[parts.length - 1];
    }

    private void downloadAndReplace(final String downloadUrl) throws Exception {
        final Path tempFile = Files.createTempFile("yt-dlp", ".exe");
        try {
            final HttpURLConnection conn = (HttpURLConnection) new URL(downloadUrl).openConnection();
            conn.setRequestProperty("User-Agent", "yt-dlp-desktop");
            conn.setConnectTimeout(15_000);
            conn.setReadTimeout(120_000);
            try (final InputStream in = conn.getInputStream();
                 final FileOutputStream out = new FileOutputStream(tempFile.toFile())) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            Files.move(tempFile, Paths.get(EXE_PATH), StandardCopyOption.REPLACE_EXISTING);
        } catch (final Exception exception) {
            Files.deleteIfExists(tempFile);
            throw exception;
        }
    }
}
