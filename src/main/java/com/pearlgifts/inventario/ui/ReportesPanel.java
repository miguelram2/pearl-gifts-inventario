package com.pearlgifts.inventario.ui;

import com.pearlgifts.inventario.dao.MovimientoDAO;
import com.pearlgifts.inventario.model.Movimiento;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportesPanel extends JPanel {

    private final MovimientoDAO movimientoDAO = new MovimientoDAO();
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Fecha", "Producto", "Tipo", "Cantidad", "Usuario", "Nota"}, 0) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    public ReportesPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refrescar = new JButton("Refrescar");
        JButton exportar = new JButton("Exportar a CSV");

        refrescar.addActionListener(e -> cargar());
        exportar.addActionListener(e -> exportarCSV());

        botones.add(refrescar);
        botones.add(exportar);
        add(botones, BorderLayout.SOUTH);

        cargar();
    }

    public void cargar() {
        try {
            modelo.setRowCount(0);
            for (Movimiento m : movimientoDAO.listarHistorico()) {
                modelo.addRow(new Object[]{
                        m.getFecha().format(FORMATO_FECHA),
                        m.getProductoNombre(),
                        m.getTipo() == Movimiento.Tipo.ENTRADA ? "Entrada" : "Salida",
                        m.getCantidad(),
                        m.getUsuarioNombre(),
                        m.getNota()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportarCSV() {
        JFileChooser selector = new JFileChooser();
        selector.setSelectedFile(new java.io.File("reporte_movimientos.csv"));
        int resultado = selector.showSaveDialog(this);
        if (resultado != JFileChooser.APPROVE_OPTION) return;

        try (FileWriter escritor = new FileWriter(selector.getSelectedFile())) {
            escritor.write("Fecha,Producto,Tipo,Cantidad,Usuario,Nota\n");
            for (int fila = 0; fila < modelo.getRowCount(); fila++) {
                StringBuilder linea = new StringBuilder();
                for (int col = 0; col < modelo.getColumnCount(); col++) {
                    Object valor = modelo.getValueAt(fila, col);
                    linea.append('"').append(valor == null ? "" : valor.toString().replace("\"", "'")).append('"');
                    if (col < modelo.getColumnCount() - 1) linea.append(',');
                }
                escritor.write(linea + "\n");
            }
            JOptionPane.showMessageDialog(this, "Reporte exportado correctamente.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "No se pudo exportar el archivo: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
