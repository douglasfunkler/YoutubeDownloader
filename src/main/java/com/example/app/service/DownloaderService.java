package com.example.app.service;

import com.example.app.util.ProcessExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownloaderService {

    /**
     * Download with custom options
     */
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
