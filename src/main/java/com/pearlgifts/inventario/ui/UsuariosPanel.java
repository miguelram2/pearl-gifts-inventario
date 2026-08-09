package com.pearlgifts.inventario.ui;

import com.pearlgifts.inventario.dao.UsuarioDAO;
import com.pearlgifts.inventario.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class UsuariosPanel extends JPanel {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Usuario", "Nombre completo", "Rol"}, 0) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    public UsuariosPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton nuevo = new JButton("Nuevo usuario");
        JButton baja = new JButton("Dar de baja");
        JButton refrescar = new JButton("Refrescar");

        nuevo.addActionListener(e -> agregar());
        baja.addActionListener(e -> darDeBaja());
        refrescar.addActionListener(e -> cargar());

        botones.add(nuevo);
        botones.add(baja);
        botones.add(refrescar);
        add(botones, BorderLayout.SOUTH);

        cargar();
    }

    public void cargar() {
        try {
            modelo.setRowCount(0);
            for (Usuario u : usuarioDAO.listarActivos()) {
                modelo.addRow(new Object[]{u.getId(), u.getUsuario(), u.getNombreCompleto(), u.getRol()});
            }
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void agregar() {
        JTextField usuario = new JTextField();
        JTextField nombreCompleto = new JTextField();
        JPasswordField password = new JPasswordField();
        JComboBox<Usuario.Rol> rol = new JComboBox<>(Usuario.Rol.values());

        Object[] campos = {
                "Usuario:", usuario, "Nombre completo:", nombreCompleto,
                "Contrasena:", password, "Rol:", rol
        };
        int opcion = JOptionPane.showConfirmDialog(this, campos, "Nuevo usuario",
                JOptionPane.OK_CANCEL_OPTION);
        if (opcion != JOptionPane.OK_OPTION) return;

        if (usuario.getText().trim().isEmpty() || password.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Usuario y contrasena son obligatorios.");
            return;
        }

        try {
            Usuario u = new Usuario();
            u.setUsuario(usuario.getText().trim());
            u.setNombreCompleto(nombreCompleto.getText().trim());
            u.setRol((Usuario.Rol) rol.getSelectedItem());
            usuarioDAO.agregar(u, new String(password.getPassword()));
            cargar();
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void darDeBaja() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un usuario de la tabla.");
            return;
        }
        int id = (int) modelo.getValueAt(fila, 0);
        int confirmar = JOptionPane.showConfirmDialog(this,
                "\u00bfDar de baja este usuario?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmar != JOptionPane.YES_OPTION) return;
        try {
            usuarioDAO.darDeBaja(id);
            cargar();
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void mostrarError(SQLException e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                "Error de base de datos", JOptionPane.ERROR_MESSAGE);
    }
}
