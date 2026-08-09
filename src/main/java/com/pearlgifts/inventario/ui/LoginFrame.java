package com.pearlgifts.inventario.ui;

import com.pearlgifts.inventario.dao.UsuarioDAO;
import com.pearlgifts.inventario.model.Usuario;
import com.pearlgifts.inventario.util.SesionActual;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    private final JTextField campoUsuario = new JTextField(15);
    private final JPasswordField campoPassword = new JPasswordField(15);
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public LoginFrame() {
        super("Pearl Gifts - Iniciar sesion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("Pearl Gifts - Control de Inventario");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 16f));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titulo, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        panel.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        panel.add(campoUsuario, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        panel.add(new JLabel("Contrasena:"), gbc);
        gbc.gridx = 1;
        panel.add(campoPassword, gbc);

        JButton botonEntrar = new JButton("Iniciar sesion");
        botonEntrar.addActionListener(e -> intentarLogin());
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        panel.add(botonEntrar, gbc);

        getRootPane().setDefaultButton(botonEntrar);
        add(panel);
        pack();
        setLocationRelativeTo(null);
    }

    private void intentarLogin() {
        String usuario = campoUsuario.getText().trim();
        String password = new String(campoPassword.getPassword());

        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa usuario y contrasena.",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Usuario u = usuarioDAO.autenticar(usuario, password);
            if (u == null) {
                JOptionPane.showMessageDialog(this, "Usuario o contrasena incorrectos.",
                        "Acceso denegado", JOptionPane.ERROR_MESSAGE);
                return;
            }
            SesionActual.iniciar(u);
            new MainFrame().setVisible(true);
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo conectar a la base de datos:\n" + e.getMessage(),
                    "Error de conexion", JOptionPane.ERROR_MESSAGE);
        }
    }
}
