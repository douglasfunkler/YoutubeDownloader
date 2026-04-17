package com.example.app.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class ThemeManager {

    private static final String CONFIG_FILE = "theme_config.properties";
    private static final String THEME_KEY = "current_theme";
    private static final String LIGHT_THEME = "light";
    private static final String DARK_THEME = "dark";

    /**
     * Initialize the application theme and GUI settings
     */
    public static void initializeTheme() {
        loadSavedTheme();
        applyGUITricks();
    }

    /**
     * Apply additional GUI customizations and tricks
     */
    private static void applyGUITricks() {
        UIManager.put("defaultFont", new FontUIResource("Segoe UI", 0, 13));
        UIManager.put("ScrollPane.smoothScrolling", true);
        UIManager.put("Button.arc", 15);
        UIManager.put("TextComponent.arc", 15);
        UIManager.put("Component.arc", 15);
    }

    /**
     * Switch to dark theme (can be extended if needed)
     */
    public static void setDarkTheme() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            saveThemePreference(DARK_THEME);
        } catch (final UnsupportedLookAndFeelException unsupportedLookAndFeelException) {
            System.err.println("Failed to set dark theme: " + unsupportedLookAndFeelException.getMessage());
            unsupportedLookAndFeelException.printStackTrace();
        }
    }

    /**
     * Switch to light theme
     */
    public static void setLightTheme() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            saveThemePreference(LIGHT_THEME);
        } catch (final UnsupportedLookAndFeelException unsupportedLookAndFeelException) {
            System.err.println("Failed to set light theme: " + unsupportedLookAndFeelException.getMessage());
            unsupportedLookAndFeelException.printStackTrace();
        }
    }

    /**
     * Load the saved theme from the configuration file
     */
    private static void loadSavedTheme() {
        final Properties properties = new Properties();
        try (final InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
            String theme = properties.getProperty(THEME_KEY, LIGHT_THEME);
            if (DARK_THEME.equals(theme)) {
                setDarkTheme();
            } else {
                setLightTheme();
            }
        } catch (final IOException ioException) {
            System.err.println("Failed to load theme configuration: " + ioException.getMessage());
            ioException.printStackTrace();
            // Fallback to light theme if loading fails
            setLightTheme();
        }
    }

    /**
     * Save the current theme preference to the configuration file
     */
    private static void saveThemePreference(final String theme) {
        final Properties properties = new Properties();
        properties.setProperty(THEME_KEY, theme);
        try (final OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, "Theme configuration");
        } catch (final IOException ioException) {
            System.err.println("Failed to save theme configuration: " + ioException.getMessage());
            ioException.printStackTrace();
        }
    }
}
