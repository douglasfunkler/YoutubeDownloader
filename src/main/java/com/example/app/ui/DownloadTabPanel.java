package com.example.app.ui;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class DownloadTabPanel extends JPanel {

    private final JTextArea logArea;
    private final JProgressBar progressBar;
    private String extractedTitle = "";
    private boolean downloading = true;
    private static final int PROGRESS_BAR_HEIGHT = 20;

    public DownloadTabPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        final JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane, BorderLayout.CENTER);

        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, PROGRESS_BAR_HEIGHT));
        progressBar.setStringPainted(true);
        add(progressBar, BorderLayout.SOUTH);
    }

    public void appendLog(final @NotNull String log) {
        logArea.append(log + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
        
        // Try to extract video title from log
        if (log.contains("Downloading") && log.contains("\"")) {
            final int start = log.indexOf("\"") + 1;
            final int end = log.lastIndexOf("\"");
            if (end > start && extractedTitle.isEmpty()) {
                extractedTitle = log.substring(start, end);
            }
        }
    }

    public void setProgress(final int progress) {
        progressBar.setValue(progress);
    }

    public boolean isDownloading() {
        return downloading;
    }

    public void setDownloading(final boolean downloading) {
        this.downloading = downloading;
    }

}

