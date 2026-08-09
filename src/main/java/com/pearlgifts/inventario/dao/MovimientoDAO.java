package com.pearlgifts.inventario.dao;

import com.pearlgifts.inventario.model.Movimiento;
import com.pearlgifts.inventario.util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MovimientoDAO {

    private final ProductoDAO productoDAO = new ProductoDAO();

    /**
     * Registra un movimiento (entrada o salida), ajusta el stock del producto
     * y deja constancia en la bitacora de auditoria. Todo dentro de una sola
     * transaccion para evitar inconsistencias.
     */
    public void registrar(Movimiento m) throws SQLException {
        String sqlMovimiento = "INSERT INTO movimientos (producto_id, tipo, cantidad, usuario_id, nota) "
                + "VALUES (?, ?, ?, ?, ?)";
        String sqlAuditoria = "INSERT INTO auditoria (usuario_id, accion) VALUES (?, ?)";

        try (Connection con = ConexionDB.obtenerConexion()) {
            con.setAutoCommit(false);
            try {
                if (m.getTipo() == Movimiento.Tipo.SALIDA) {
                    validarStockSuficiente(con, m.getProductoId(), m.getCantidad());
                }

                try (PreparedStatement ps = con.prepareStatement(sqlMovimiento)) {
                    ps.setInt(1, m.getProductoId());
                    ps.setString(2, m.getTipo().name());
                    ps.setInt(3, m.getCantidad());
                    ps.setInt(4, m.getUsuarioId());
                    ps.setString(5, m.getNota());
                    ps.executeUpdate();
                }

                int delta = (m.getTipo() == Movimiento.Tipo.ENTRADA) ? m.getCantidad() : -m.getCantidad();
                productoDAO.ajustarCantidad(m.getProductoId(), delta, con);

                try (PreparedStatement ps = con.prepareStatement(sqlAuditoria)) {
                    ps.setInt(1, m.getUsuarioId());
                    String accion = String.format("%s de %d unidad(es) del producto #%d",
                            m.getTipo() == Movimiento.Tipo.ENTRADA ? "Entrada" : "Salida",
                            m.getCantidad(), m.getProductoId());
                    ps.setString(2, accion);
                    ps.executeUpdate();
                }

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    private void validarStockSuficiente(Connection con, int productoId, int cantidadSolicitada) throws SQLException {
        String sql = "SELECT cantidad FROM productos WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int disponible = rs.getInt("cantidad");
                    if (disponible < cantidadSolicitada) {
                        throw new SQLException("Stock insuficiente: disponible " + disponible
                                + ", solicitado " + cantidadSolicitada);
                    }
                }
            }
        }
    }

    public List<Movimiento> listarPorProducto(int productoId) throws SQLException {
        List<Movimiento> resultado = new ArrayList<>();
        String sql = "SELECT m.id, m.producto_id, p.nombre AS producto_nombre, m.tipo, m.cantidad, "
                + "m.fecha, m.usuario_id, u.nombre_completo AS usuario_nombre, m.nota "
                + "FROM movimientos m "
                + "JOIN productos p ON m.producto_id = p.id "
                + "JOIN usuarios u ON m.usuario_id = u.id "
                + "WHERE m.producto_id = ? ORDER BY m.fecha DESC";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapear(rs));
                }
            }
        }
        return resultado;
    }

    public List<Movimiento> listarHistorico() throws SQLException {
        List<Movimiento> resultado = new ArrayList<>();
        String sql = "SELECT m.id, m.producto_id, p.nombre AS producto_nombre, m.tipo, m.cantidad, "
                + "m.fecha, m.usuario_id, u.nombre_completo AS usuario_nombre, m.nota "
                + "FROM movimientos m "
                + "JOIN productos p ON m.producto_id = p.id "
                + "JOIN usuarios u ON m.usuario_id = u.id "
                + "ORDER BY m.fecha DESC LIMIT 500";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(mapear(rs));
            }
        }
        return resultado;
    }

    private Movimiento mapear(ResultSet rs) throws SQLException {
        Movimiento m = new Movimiento();
        m.setId(rs.getInt("id"));
        m.setProductoId(rs.getInt("producto_id"));
        m.setProductoNombre(rs.getString("producto_nombre"));
        m.setTipo(Movimiento.Tipo.valueOf(rs.getString("tipo")));
        m.setCantidad(rs.getInt("cantidad"));
        Timestamp ts = rs.getTimestamp("fecha");
        m.setFecha(ts != null ? ts.toLocalDateTime() : LocalDateTime.now());
        m.setUsuarioId(rs.getInt("usuario_id"));
        m.setUsuarioNombre(rs.getString("usuario_nombre"));
        m.setNota(rs.getString("nota"));
        return m;
    }
}
