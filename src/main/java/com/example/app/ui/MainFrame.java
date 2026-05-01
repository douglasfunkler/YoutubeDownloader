package com.example.app.ui;

import com.example.app.service.DownloaderService;
import com.example.app.service.UpdateService;
import com.example.app.util.ClipboardHelper;
import com.example.app.util.FormatProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.Desktop;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.formdev.flatlaf.extras.FlatSVGIcon;

public class MainFrame extends JFrame {

    private final JTextField urlField = new JTextField();
    private final JButton downloadButton = new JButton("Download");
    private final JButton pasteButton = new JButton("Paste");
    private final JButton browseButton = new JButton("Browse");

    private static final int PROGRESS_BAR_HEIGHT = 13;
    private static final int MAX_TAB_TITLE_LENGTH = 30;

    private final JTextField downloadPathField = new JTextField();
    private JComboBox<FormatProvider.FormatOption> videoFormatCombo;
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final Map<Integer, DownloadTabPanel> downloadTabs = new HashMap<>();
    private int tabCounter = 0;

    private Icon checkIcon;
    private Icon errorIcon;

    public MainFrame() {
        setTitle("yt-dlp Downloader");
        setSize(900, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        try {
            FlatSVGIcon icon = new FlatSVGIcon(getClass().getResource("/icon.svg"));
            setIconImage(icon.getImage());
        } catch (final Exception exception) {
            exception.printStackTrace();
        }

        try {
            checkIcon = new FlatSVGIcon(getClass().getResource("/check.svg")).derive(14, 14);
            errorIcon = new FlatSVGIcon(getClass().getResource("/error.svg")).derive(14, 14);
        } catch (final Exception exception) {
            exception.printStackTrace();
        }

        // Combined panel for URL and options
        JPanel topSectionPanel = createTopSectionPanel();

        add(topSectionPanel, BorderLayout.NORTH);
        
        // Tabbed pane for downloads
        tabbedPane.setTabPlacement(JTabbedPane.TOP);
        add(tabbedPane, BorderLayout.CENTER);
        
        // Button panel with theme, clear, update, and donate buttons
        JPanel buttonPanel = new JPanel(new BorderLayout());
        JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton themeButton = new JButton("Switch Theme");
        leftButtonPanel.add(themeButton);
        buttonPanel.add(leftButtonPanel, BorderLayout.WEST);
        
        JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton clearAllButton = new JButton("Clear All Tabs");
        rightButtonPanel.add(clearAllButton);
        buttonPanel.add(rightButtonPanel, BorderLayout.EAST);
        
        JPanel centerButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton updateButton = new JButton("Update yt-dlp");
        centerButtonPanel.add(updateButton);
        JButton donateButton = new JButton("Donate");
        centerButtonPanel.add(donateButton);
        buttonPanel.add(centerButtonPanel, BorderLayout.CENTER);
        
        add(buttonPanel, BorderLayout.SOUTH);

        downloadButton.addActionListener(e -> startDownload());
        pasteButton.addActionListener(e -> pasteFromClipboard());
        browseButton.addActionListener(e -> browsePath());

        clearAllButton.addActionListener(e -> {
            tabbedPane.removeAll();
            downloadTabs.clear();
        });

        themeButton.addActionListener(e -> switchTheme());

        updateButton.addActionListener(e -> {
            updateButton.setEnabled(false);
            new Thread(() -> {
                try {
                    UpdateService service = new UpdateService();
                    service.updateYtDlp();
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "yt-dlp updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        updateButton.setEnabled(true);
                    });
                } catch (final Exception exception) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Update failed: " + exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        updateButton.setEnabled(true);
                    });
                }
            }).start();
        });

        donateButton.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new java.net.URI("https://github.com/yt-dlp/yt-dlp#contributing"));
            } catch (final Exception exception) {
                JOptionPane.showMessageDialog(this, "Could not open donation link: " + exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
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
        
        JPanel urlPanel = new JPanel(new BorderLayout(10, 0));
        JLabel urlLabel = new JLabel("URL:");
        urlPanel.add(urlLabel, BorderLayout.WEST);
        urlField.setPreferredSize(new Dimension(0, 30));
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
         JPanel downloadPathPanel = new JPanel(new BorderLayout(5, 0));
         downloadPathField.setText(System.getProperty("user.home") + File.separator + "Desktop");
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
        
        // Create a new tab for this download
        DownloadTabPanel downloadTab = new DownloadTabPanel();
        int tabIndex = tabCounter++;
        downloadTabs.put(tabIndex, downloadTab);
        tabbedPane.addTab("Downloading...", downloadTab);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
        
        DownloaderService service = new DownloaderService();

        new Thread(() -> {
            try {
                String fetchedTitle = service.getTitle(url);
                if (!fetchedTitle.isEmpty()) {
                    SwingUtilities.invokeLater(() -> updateTabTitle(tabIndex, truncateTitle(fetchedTitle)));
                }

                service.download(
                    url,
                    downloadPath,
                    videoFormat,
                    progress -> SwingUtilities.invokeLater(() -> downloadTab.setProgress(progress)),
                    log -> SwingUtilities.invokeLater(() -> downloadTab.appendLog(log))
                );

                SwingUtilities.invokeLater(() -> {
                    String displayTitle = fetchedTitle.isEmpty() ? "Download " + tabIndex : fetchedTitle;
                    updateTabTitle(tabIndex, truncateTitle(displayTitle));
                    setTabIcon(tabIndex, checkIcon);
                    JOptionPane.showMessageDialog(this, "Download completed!", "Success", JOptionPane.INFORMATION_MESSAGE);
                });
            } catch (final Exception exception) {
                SwingUtilities.invokeLater(() -> {
                    updateTabTitle(tabIndex, "Error - Download " + tabIndex);
                    setTabIcon(tabIndex, errorIcon);
                    downloadTab.appendLog("\n[ERROR] " + exception.getMessage());
                    JOptionPane.showMessageDialog(this, "Error: " + exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private String truncateTitle(String title) {
        if (title.length() > MAX_TAB_TITLE_LENGTH) {
            return title.substring(0, MAX_TAB_TITLE_LENGTH - 3) + "...";
        }
        return title;
    }

    private void updateTabTitle(int tabIndex, String title) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (downloadTabs.get(tabIndex) == tabbedPane.getComponentAt(i)) {
                tabbedPane.setTitleAt(i, title);
                break;
            }
        }
    }

    private void setTabIcon(int tabIndex, Icon icon) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (downloadTabs.get(tabIndex) == tabbedPane.getComponentAt(i)) {
                tabbedPane.setIconAt(i, icon);
                break;
            }
        }
    }

    private void switchTheme() {
        // Determine current theme and switch to the opposite
        try {
            String currentLaf = UIManager.getLookAndFeel().getClass().getName();
            if (currentLaf.contains("FlatDarkLaf")) {
                ThemeManager.setLightTheme();
            } else {
                ThemeManager.setDarkTheme();
            }
            // Update all components to reflect the new theme
            SwingUtilities.updateComponentTreeUI(this);
        } catch (final Exception exception) {
            exception.printStackTrace();
        }
    }
}
