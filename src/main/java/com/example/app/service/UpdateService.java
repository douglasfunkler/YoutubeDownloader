package com.example.app.service;

import com.google.gson.Gson;

import java.io.*;
import java.net.*;
import java.nio.file.*;

public class UpdateService {

    private static final String API_URL = "https://api.github.com/repos/yt-dlp/yt-dlp/releases/latest";
    private static final String EXE_NAME = "yt-dlp.exe";
    private static final String EXE_PATH = "yt-dlp.exe"; // relative to project root

    public void updateYtDlp() throws Exception {
        // Fetch JSON
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/vnd.github.v3+json");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();
            Gson gson = new Gson();
            Release release = gson.fromJson(json, Release.class);
            String downloadUrl = null;
            for (Asset asset : release.assets) {
                if (EXE_NAME.equals(asset.name)) {
                    downloadUrl = asset.browser_download_url;
                    break;
                }
            }
            if (downloadUrl == null) {
                throw new Exception("yt-dlp.exe not found in latest release");
            }
            // Download
            URL download = new URL(downloadUrl);
            Path tempFile = Files.createTempFile("yt-dlp", ".exe");
            try (InputStream in = download.openStream();
                 FileOutputStream out = new FileOutputStream(tempFile.toFile())) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            // Replace
            Path exePath = Paths.get(EXE_PATH);
            Files.move(tempFile, exePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    static class Release {
        Asset[] assets;
    }

    static class Asset {
        String name;
        String browser_download_url;
    }
}
