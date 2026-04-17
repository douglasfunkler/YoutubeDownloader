package com.example.app.ui;

import com.example.app.service.DownloaderService;
import com.example.app.util.ClipboardHelper;
import com.example.app.util.FormatProvider;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MainFrame extends JFrame {

    private JTextField urlField = new JTextField();
    private JButton downloadButton = new JButton("Download");
    private JButton pasteButton = new JButton("Paste");
    private JButton browseButton = new JButton("Browse");
    private JTextArea logArea = new JTextArea();
    private JProgressBar progressBar = new JProgressBar(0, 100);
    
    private JTextField downloadPathField = new JTextField();
    private JComboBox<FormatProvider.FormatOption> videoFormatCombo;

    public MainFrame() {
        setTitle("yt-dlp Downloader");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Combined panel for URL and options
        JPanel topSectionPanel = createTopSectionPanel();

        add(topSectionPanel, BorderLayout.NORTH);
        add(new JScrollPane(logArea), BorderLayout.CENTER);
        add(progressBar, BorderLayout.SOUTH);

        downloadButton.addActionListener(e -> startDownload());
        pasteButton.addActionListener(e -> pasteFromClipboard());
        browseButton.addActionListener(e -> browsePath());
    }

    private JPanel createTopSectionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel topPanel = createTopPanel();
        JPanel optionsPanel = createOptionsPanel();
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(optionsPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JPanel urlPanel = new JPanel(new BorderLayout());
        urlPanel.add(new JLabel("URL:"), BorderLayout.WEST);
        urlPanel.add(urlField, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(pasteButton);
        buttonPanel.add(downloadButton);
        
        panel.add(urlPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.EAST);
        
        return panel;
    }

    private JPanel createOptionsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Download Options"));
        panel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 80));
        
        // Video Format
        panel.add(new JLabel("Video Format:"));
        videoFormatCombo = new JComboBox<>(FormatProvider.VIDEO_FORMATS.toArray(new FormatProvider.FormatOption[0]));
        videoFormatCombo.setSelectedIndex(0);
        panel.add(videoFormatCombo);
        
        // Download Directory
        panel.add(new JLabel("Download Directory:"));
        JPanel downloadPathPanel = new JPanel(new BorderLayout());
        downloadPathField.setText(System.getProperty("user.home") + "/Downloads");
        downloadPathPanel.add(downloadPathField, BorderLayout.CENTER);
        downloadPathPanel.add(browseButton, BorderLayout.EAST);
        panel.add(downloadPathPanel);
        
        return panel;
    }

    private void pasteFromClipboard() {
        String clipboardText = ClipboardHelper.getClipboardText();
        if (clipboardText != null && !clipboardText.isEmpty()) {
            urlField.setText(clipboardText);
        } else {
            JOptionPane.showMessageDialog(this, "Clipboard is empty or contains no text.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void browsePath() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setCurrentDirectory(new File(downloadPathField.getText()));
        
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            downloadPathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void startDownload() {
        String url = urlField.getText();
        if (url.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a URL.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String downloadPath = downloadPathField.getText();
        String videoFormat = ((FormatProvider.FormatOption) videoFormatCombo.getSelectedItem()).getValue();
        
        logArea.setText(""); // Clear log
        downloadButton.setEnabled(false);
        
        DownloaderService service = new DownloaderService();

        new Thread(() -> {
            try {
                service.download(
                    url,
                    downloadPath,
                    videoFormat,
                    progress -> SwingUtilities.invokeLater(() -> progressBar.setValue(progress)),
                    log -> SwingUtilities.invokeLater(() -> logArea.append(log + "\n"))
                );
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Download completed!", "Success", JOptionPane.INFORMATION_MESSAGE));
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            } finally {
                SwingUtilities.invokeLater(() -> downloadButton.setEnabled(true));
            }
        }).start();
    }
}
