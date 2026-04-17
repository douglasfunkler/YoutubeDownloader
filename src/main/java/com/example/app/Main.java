package com.example.app;

import com.example.app.ui.MainFrame;
import com.example.app.ui.ThemeManager;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        ThemeManager.initializeTheme();
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
