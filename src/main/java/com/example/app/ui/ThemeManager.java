package com.example.app.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;

public class ThemeManager {

    /**
     * Initialize the application theme and GUI settings
     */
    public static void initializeTheme() {
        setFlatLafTheme();
        applyGUITricks();
    }

    /**
     * Set FlatLaf as the look and feel
     */
    private static void setFlatLafTheme() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Failed to set FlatLaf theme: " + e.getMessage());
            e.printStackTrace();
        }
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
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Failed to set dark theme: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Switch to light theme
     */
    public static void setLightTheme() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Failed to set light theme: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


