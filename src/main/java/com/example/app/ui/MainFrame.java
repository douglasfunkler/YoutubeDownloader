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
    private JButton pasteButton = new JButton("Einfügen");
    private JButton browseButton = new JButton("Durchsuchen");
    private JTextArea logArea = new JTextArea();
    private JProgressBar progressBar = new JProgressBar(0, 100);
    
    private JTextField downloadPathField = new JTextField();
    private JComboBox<FormatProvider.FormatOption> videoFormatCombo;
    private JComboBox<FormatProvider.FormatOption> audioFormatCombo;

    public MainFrame() {
        setTitle("yt-dlp Downloader");
        setSize(800, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Oberes Panel mit URL-Eingabe
        JPanel topPanel = createTopPanel();
        
        // Optionen-Panel mit Formaten und Verzeichnis
        JPanel optionsPanel = createOptionsPanel();

        add(topPanel, BorderLayout.NORTH);
        add(optionsPanel, BorderLayout.PAGE_START);
        add(new JScrollPane(logArea), BorderLayout.CENTER);
        add(progressBar, BorderLayout.SOUTH);

        downloadButton.addActionListener(e -> startDownload());
        pasteButton.addActionListener(e -> pasteFromClipboard());
        browseButton.addActionListener(e -> browsePath());
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
        panel.setLayout(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Download-Optionen"));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        // Video-Format
        panel.add(new JLabel("Video-Format:"));
        videoFormatCombo = new JComboBox<>(FormatProvider.VIDEO_FORMATS.toArray(new FormatProvider.FormatOption[0]));
        videoFormatCombo.setSelectedIndex(0);
        panel.add(videoFormatCombo);
        
        // Audio-Format
        panel.add(new JLabel("Audio-Format/Container:"));
        audioFormatCombo = new JComboBox<>(FormatProvider.AUDIO_FORMATS.toArray(new FormatProvider.FormatOption[0]));
        audioFormatCombo.setSelectedIndex(0);
        panel.add(audioFormatCombo);
        
        // Download-Verzeichnis
        panel.add(new JLabel("Download-Verzeichnis:"));
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
            JOptionPane.showMessageDialog(this, "Zwischenablage ist leer oder enthält keinen Text.", "Fehler", JOptionPane.WARNING_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Bitte URL eingeben.", "Fehler", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String downloadPath = downloadPathField.getText();
        String videoFormat = ((FormatProvider.FormatOption) videoFormatCombo.getSelectedItem()).getValue();
        String audioFormat = ((FormatProvider.FormatOption) audioFormatCombo.getSelectedItem()).getValue();
        
        logArea.setText(""); // Log leeren
        downloadButton.setEnabled(false);
        
        DownloaderService service = new DownloaderService();

        new Thread(() -> {
            try {
                service.download(
                    url,
                    downloadPath,
                    videoFormat,
                    audioFormat,
                    progress -> SwingUtilities.invokeLater(() -> progressBar.setValue(progress)),
                    log -> SwingUtilities.invokeLater(() -> logArea.append(log + "\n"))
                );
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Download abgeschlossen!", "Erfolg", JOptionPane.INFORMATION_MESSAGE));
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Fehler: " + e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE));
            } finally {
                SwingUtilities.invokeLater(() -> downloadButton.setEnabled(true));
            }
        }).start();
    }
}
