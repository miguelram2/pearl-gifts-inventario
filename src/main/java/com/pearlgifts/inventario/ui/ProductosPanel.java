package com.pearlgifts.inventario.ui;

import com.pearlgifts.inventario.dao.CategoriaDAO;
import com.pearlgifts.inventario.dao.ProductoDAO;
import com.pearlgifts.inventario.model.Categoria;
import com.pearlgifts.inventario.model.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ProductosPanel extends JPanel {

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Categoria", "Precio", "Cantidad", "Stock minimo", "Proveedor"}, 0) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    public ProductosPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton nuevo = new JButton("Nuevo producto");
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
            for (Producto p : productoDAO.listarActivos()) {
                modelo.addRow(new Object[]{p.getId(), p.getNombre(), p.getCategoriaNombre(),
                        p.getPrecio(), p.getCantidad(), p.getStockMinimo(), p.getProveedor()});
            }
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void agregar() {
        List<Categoria> categorias;
        try {
            categorias = categoriaDAO.listarActivas();
        } catch (SQLException e) {
            mostrarError(e);
            return;
        }
        if (categorias.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero registra al menos una categoria.");
            return;
        }

        JTextField nombre = new JTextField();
        JComboBox<Categoria> categoria = new JComboBox<>(categorias.toArray(new Categoria[0]));
        JTextField precio = new JTextField("0.00");
        JTextField cantidadInicial = new JTextField("0");
        JTextField stockMinimo = new JTextField("5");
        JTextField proveedor = new JTextField();

        Object[] campos = {
                "Nombre:", nombre, "Categoria:", categoria, "Precio:", precio,
                "Cantidad inicial:", cantidadInicial, "Stock minimo:", stockMinimo, "Proveedor:", proveedor
        };
        int opcion = JOptionPane.showConfirmDialog(this, campos, "Nuevo producto",
                JOptionPane.OK_CANCEL_OPTION);
        if (opcion != JOptionPane.OK_OPTION) return;

        if (nombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio.");
            return;
        }

        try {
            Producto p = new Producto();
            p.setNombre(nombre.getText().trim());
            p.setCategoriaId(((Categoria) categoria.getSelectedItem()).getId());
            p.setPrecio(new BigDecimal(precio.getText().trim()));
            p.setCantidad(Integer.parseInt(cantidadInicial.getText().trim()));
            p.setStockMinimo(Integer.parseInt(stockMinimo.getText().trim()));
            p.setProveedor(proveedor.getText().trim());
            productoDAO.agregar(p);
            cargar();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Precio, cantidad y stock minimo deben ser numeros validos.");
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void editar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto de la tabla.");
            return;
        }
        int id = (int) modelo.getValueAt(fila, 0);

        List<Categoria> categorias;
        try {
            categorias = categoriaDAO.listarActivas();
        } catch (SQLException e) {
            mostrarError(e);
            return;
        }

        JTextField nombre = new JTextField((String) modelo.getValueAt(fila, 1));
        JComboBox<Categoria> categoria = new JComboBox<>(categorias.toArray(new Categoria[0]));
        JTextField precio = new JTextField(modelo.getValueAt(fila, 3).toString());
        JTextField stockMinimo = new JTextField(modelo.getValueAt(fila, 5).toString());
        JTextField proveedor = new JTextField(String.valueOf(modelo.getValueAt(fila, 6)));

        Object[] campos = {
                "Nombre:", nombre, "Categoria:", categoria, "Precio:", precio,
                "Stock minimo:", stockMinimo, "Proveedor:", proveedor
        };
        int opcion = JOptionPane.showConfirmDialog(this, campos, "Editar producto",
                JOptionPane.OK_CANCEL_OPTION);
        if (opcion != JOptionPane.OK_OPTION) return;

        try {
            Producto p = new Producto();
            p.setId(id);
            p.setNombre(nombre.getText().trim());
            p.setCategoriaId(((Categoria) categoria.getSelectedItem()).getId());
            p.setPrecio(new BigDecimal(precio.getText().trim()));
            p.setStockMinimo(Integer.parseInt(stockMinimo.getText().trim()));
            p.setProveedor(proveedor.getText().trim());
            productoDAO.actualizar(p);
            cargar();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Precio y stock minimo deben ser numeros validos.");
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void darDeBaja() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto de la tabla.");
            return;
        }
        int id = (int) modelo.getValueAt(fila, 0);
        int confirmar = JOptionPane.showConfirmDialog(this,
                "\u00bfDar de baja este producto?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmar != JOptionPane.YES_OPTION) return;
        try {
            productoDAO.darDeBaja(id);
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
