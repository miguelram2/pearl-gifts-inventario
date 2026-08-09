package com.pearlgifts.inventario.ui;

import com.pearlgifts.inventario.dao.CategoriaDAO;
import com.pearlgifts.inventario.model.Categoria;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class CategoriasPanel extends JPanel {

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Descripcion"}, 0) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    public CategoriasPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton nuevo = new JButton("Nueva categoria");
        JButton editar = new JButton("Editar");
        JButton baja = new JButton("Dar de baja");
        JButton refrescar = new JButton("Refrescar");

        nuevo.addActionListener(e -> agregar());
        editar.addActionListener(e -> editar());
        baja.addActionListener(e -> darDeBaja());
        refrescar.addActionListener(e -> cargar());

        botones.add(nuevo);
        botones.add(editar);
        botones.add(baja);
        botones.add(refrescar);
        add(botones, BorderLayout.SOUTH);

        cargar();
    }

    public void cargar() {
        try {
            modelo.setRowCount(0);
            for (Categoria c : categoriaDAO.listarActivas()) {
                modelo.addRow(new Object[]{c.getId(), c.getNombre(), c.getDescripcion()});
            }
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void agregar() {
        JTextField nombre = new JTextField();
        JTextField descripcion = new JTextField();
        Object[] campos = {"Nombre:", nombre, "Descripcion:", descripcion};
        int opcion = JOptionPane.showConfirmDialog(this, campos, "Nueva categoria",
                JOptionPane.OK_CANCEL_OPTION);
        if (opcion != JOptionPane.OK_OPTION) return;
        if (nombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio.");
            return;
        }
        try {
            Categoria c = new Categoria();
            c.setNombre(nombre.getText().trim());
            c.setDescripcion(descripcion.getText().trim());
            categoriaDAO.agregar(c);
            cargar();
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void editar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una categoria de la tabla.");
            return;
        }
        int id = (int) modelo.getValueAt(fila, 0);
        JTextField nombre = new JTextField((String) modelo.getValueAt(fila, 1));
        JTextField descripcion = new JTextField((String) modelo.getValueAt(fila, 2));
        Object[] campos = {"Nombre:", nombre, "Descripcion:", descripcion};
        int opcion = JOptionPane.showConfirmDialog(this, campos, "Editar categoria",
                JOptionPane.OK_CANCEL_OPTION);
        if (opcion != JOptionPane.OK_OPTION) return;
        try {
            Categoria c = new Categoria();
            c.setId(id);
            c.setNombre(nombre.getText().trim());
            c.setDescripcion(descripcion.getText().trim());
            categoriaDAO.actualizar(c);
            cargar();
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void darDeBaja() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una categoria de la tabla.");
            return;
        }
        int id = (int) modelo.getValueAt(fila, 0);
        int confirmar = JOptionPane.showConfirmDialog(this,
                "\u00bfDar de baja esta categoria?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmar != JOptionPane.YES_OPTION) return;
        try {
            categoriaDAO.darDeBaja(id);
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
