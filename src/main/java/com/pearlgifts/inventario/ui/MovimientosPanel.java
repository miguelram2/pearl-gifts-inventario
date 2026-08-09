package com.pearlgifts.inventario.ui;

import com.pearlgifts.inventario.dao.MovimientoDAO;
import com.pearlgifts.inventario.dao.ProductoDAO;
import com.pearlgifts.inventario.model.Movimiento;
import com.pearlgifts.inventario.model.Producto;
import com.pearlgifts.inventario.util.SesionActual;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MovimientosPanel extends JPanel {

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final MovimientoDAO movimientoDAO = new MovimientoDAO();
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final DefaultTableModel modeloHistorico = new DefaultTableModel(
            new Object[]{"Fecha", "Producto", "Tipo", "Cantidad", "Usuario", "Nota"}, 0) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };

    private final DefaultTableModel modeloAlertas = new DefaultTableModel(
            new Object[]{"Producto", "Cantidad actual", "Stock minimo"}, 0) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };

    public MovimientosPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton entrada = new JButton("Registrar entrada");
        JButton salida = new JButton("Registrar salida");
        JButton refrescar = new JButton("Refrescar");

        entrada.addActionListener(e -> registrarMovimiento(Movimiento.Tipo.ENTRADA));
        salida.addActionListener(e -> registrarMovimiento(Movimiento.Tipo.SALIDA));
        refrescar.addActionListener(e -> cargar());

        botones.add(entrada);
        botones.add(salida);
        botones.add(refrescar);
        add(botones, BorderLayout.NORTH);

        JTable tablaAlertas = new JTable(modeloAlertas);
        tablaAlertas.setForeground(new Color(153, 0, 0));
        JPanel panelAlertas = new JPanel(new BorderLayout());
        panelAlertas.setBorder(BorderFactory.createTitledBorder("Alertas de stock bajo"));
        panelAlertas.add(new JScrollPane(tablaAlertas), BorderLayout.CENTER);
        panelAlertas.setPreferredSize(new Dimension(0, 150));

        JTable tablaHistorico = new JTable(modeloHistorico);
        JPanel panelHistorico = new JPanel(new BorderLayout());
        panelHistorico.setBorder(BorderFactory.createTitledBorder("Historico de movimientos"));
        panelHistorico.add(new JScrollPane(tablaHistorico), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelAlertas, panelHistorico);
        split.setResizeWeight(0.25);
        add(split, BorderLayout.CENTER);

        cargar();
    }

    public void cargar() {
        cargarAlertas();
        cargarHistorico();
    }

    private void cargarAlertas() {
        try {
            modeloAlertas.setRowCount(0);
            for (Producto p : productoDAO.listarConStockBajo()) {
                modeloAlertas.addRow(new Object[]{p.getNombre(), p.getCantidad(), p.getStockMinimo()});
            }
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void cargarHistorico() {
        try {
            modeloHistorico.setRowCount(0);
            for (Movimiento m : movimientoDAO.listarHistorico()) {
                modeloHistorico.addRow(new Object[]{
                        m.getFecha().format(FORMATO_FECHA),
                        m.getProductoNombre(),
                        m.getTipo() == Movimiento.Tipo.ENTRADA ? "Entrada" : "Salida",
                        m.getCantidad(),
                        m.getUsuarioNombre(),
                        m.getNota()
                });
            }
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void registrarMovimiento(Movimiento.Tipo tipo) {
        List<Producto> productos;
        try {
            productos = productoDAO.listarActivos();
        } catch (SQLException e) {
            mostrarError(e);
            return;
        }
        if (productos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero registra al menos un producto.");
            return;
        }

        JComboBox<Producto> productoCombo = new JComboBox<>(productos.toArray(new Producto[0]));
        JTextField cantidad = new JTextField("1");
        JTextField nota = new JTextField();
        String tituloTipo = tipo == Movimiento.Tipo.ENTRADA ? "Registrar entrada" : "Registrar salida";

        Object[] campos = {"Producto:", productoCombo, "Cantidad:", cantidad, "Nota:", nota};
        int opcion = JOptionPane.showConfirmDialog(this, campos, tituloTipo, JOptionPane.OK_CANCEL_OPTION);
        if (opcion != JOptionPane.OK_OPTION) return;

        try {
            int cant = Integer.parseInt(cantidad.getText().trim());
            if (cant <= 0) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a cero.");
                return;
            }
            Movimiento m = new Movimiento();
            m.setProductoId(((Producto) productoCombo.getSelectedItem()).getId());
            m.setTipo(tipo);
            m.setCantidad(cant);
            m.setUsuarioId(SesionActual.get().getId());
            m.setNota(nota.getText().trim());
            movimientoDAO.registrar(m);
            cargar();
            JOptionPane.showMessageDialog(this, "Movimiento registrado correctamente.");
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un numero valido.");
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void mostrarError(SQLException e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                "Error de base de datos", JOptionPane.ERROR_MESSAGE);
    }
}
