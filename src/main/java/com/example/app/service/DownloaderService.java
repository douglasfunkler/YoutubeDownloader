package com.example.app.service;

import com.example.app.util.ProcessExecutor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownloaderService {

    public String getTitle(String url) {
        try {
            ProcessBuilder pb = new ProcessBuilder("yt-dlp.exe", "--get-title", "--no-playlist", url);
            // Do NOT redirect stderr — only stdout contains the title; warnings go to stderr
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            process.waitFor();
            return line != null ? line.trim() : "";
        } catch (Exception e) {
            return "";
        }
    }

    public void download(String url, String downloadPath, String videoFormat, ProgressListener progressListener, LogListener logListener) {
        // Build command dynamically
        List<String> commandList = new ArrayList<>();
        commandList.add("yt-dlp.exe");
        commandList.add("-f");
        commandList.add(videoFormat);
        commandList.add("--ffmpeg-location");
        commandList.add("ffmpeg/bin");
        commandList.add("--no-playlist");
        commandList.add("-o");
        commandList.add(downloadPath + "/%(title)s.%(ext)s");
        commandList.add(url);

        String[] command = commandList.toArray(new String[0]);

        Pattern pattern = Pattern.compile("(\\d{1,3}\\.\\d+)%");

        ProcessExecutor.execute(command, line -> {
            logListener.onLog(line);

            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                int progress = (int) Double.parseDouble(matcher.group(1));
                progressListener.onProgress(progress);
            }
        });
    }
}
