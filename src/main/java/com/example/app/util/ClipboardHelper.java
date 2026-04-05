package com.example.app.util;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class ClipboardHelper {

    /**
     * Liest Text aus der Zwischenablage
     * @return Der Text aus der Zwischenablage, oder null wenn keine Daten verfügbar sind
     */
    public static String getClipboardText() {
        try {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            java.awt.datatransfer.Clipboard clipboard = toolkit.getSystemClipboard();
            java.awt.datatransfer.Transferable transferable = clipboard.getContents(null);

            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String) transferable.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Überprüft, ob die Zwischenablage einen Text enthält
     * @return true wenn Text vorhanden ist
     */
    public static boolean hasClipboardText() {
        try {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            java.awt.datatransfer.Clipboard clipboard = toolkit.getSystemClipboard();
            java.awt.datatransfer.Transferable transferable = clipboard.getContents(null);
            return transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor);
        } catch (Exception e) {
            return false;
        }
    }
}

