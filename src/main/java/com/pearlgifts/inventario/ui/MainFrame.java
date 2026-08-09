package com.pearlgifts.inventario.ui;

import com.pearlgifts.inventario.util.SesionActual;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        super("Pearl Gifts - Control de Inventario  |  Usuario: "
                + SesionActual.get().getNombreCompleto() + " (" + SesionActual.get().getRol() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Productos", new ProductosPanel());
        tabs.addTab("Categorias", new CategoriasPanel());
        tabs.addTab("Movimientos", new MovimientosPanel());
        tabs.addTab("Reportes", new ReportesPanel());

        // Solo el administrador (Perla) ve la pestana de gestion de usuarios.
        if (SesionActual.esAdmin()) {
            tabs.addTab("Usuarios", new UsuariosPanel());
        }

        add(tabs, BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        JMenu menuSesion = new JMenu("Sesion");
        JMenuItem cerrarSesion = new JMenuItem("Cerrar sesion");
        cerrarSesion.addActionListener(e -> {
            SesionActual.cerrar();
            new LoginFrame().setVisible(true);
            dispose();
        });
        menuSesion.add(cerrarSesion);
        menuBar.add(menuSesion);
        setJMenuBar(menuBar);
    }
}
