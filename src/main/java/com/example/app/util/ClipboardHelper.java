package com.example.app.util;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class ClipboardHelper {

    public static String getClipboardText() {
        try {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            java.awt.datatransfer.Clipboard clipboard = toolkit.getSystemClipboard();
            java.awt.datatransfer.Transferable transferable = clipboard.getContents(null);

            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String) transferable.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (final UnsupportedFlavorException | IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

}

