package com.pearlgifts.inventario;

import com.pearlgifts.inventario.ui.LoginFrame;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Si falla, se usa el look and feel por defecto de Swing.
            }
            new LoginFrame().setVisible(true);
        });
    }
}
