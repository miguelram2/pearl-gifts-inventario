package com.pearlgifts.inventario.dao;

import com.pearlgifts.inventario.model.Producto;
import com.pearlgifts.inventario.util.ConexionDB;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public void agregar(Producto p) throws SQLException {
        String sql = "INSERT INTO productos (nombre, categoria_id, precio, cantidad, stock_minimo, proveedor, activo) "
                + "VALUES (?, ?, ?, ?, ?, ?, TRUE)";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setInt(2, p.getCategoriaId());
            ps.setBigDecimal(3, p.getPrecio());
            ps.setInt(4, p.getCantidad());
            ps.setInt(5, p.getStockMinimo());
            ps.setString(6, p.getProveedor());
            ps.executeUpdate();
        }
    }

    public void actualizar(Producto p) throws SQLException {
        String sql = "UPDATE productos SET nombre = ?, categoria_id = ?, precio = ?, stock_minimo = ?, proveedor = ? "
                + "WHERE id = ?";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setInt(2, p.getCategoriaId());
            ps.setBigDecimal(3, p.getPrecio());
            ps.setInt(4, p.getStockMinimo());
            ps.setString(5, p.getProveedor());
            ps.setInt(6, p.getId());
            ps.executeUpdate();
        }
    }

    public void darDeBaja(int id) throws SQLException {
        String sql = "UPDATE productos SET activo = FALSE WHERE id = ?";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /** Suma o resta cantidad de un producto (usado por MovimientoDAO). */
    public void ajustarCantidad(int productoId, int delta, Connection con) throws SQLException {
        String sql = "UPDATE productos SET cantidad = cantidad + ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setInt(2, productoId);
            ps.executeUpdate();
        }
    }

    public Producto obtenerPorId(int id) throws SQLException {
        String sql = "SELECT p.id, p.nombre, p.categoria_id, c.nombre AS categoria_nombre, p.precio, "
                + "p.cantidad, p.stock_minimo, p.proveedor, p.activo "
                + "FROM productos p JOIN categorias c ON p.categoria_id = c.id WHERE p.id = ?";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public List<Producto> listarActivos() throws SQLException {
        List<Producto> resultado = new ArrayList<>();
        String sql = "SELECT p.id, p.nombre, p.categoria_id, c.nombre AS categoria_nombre, p.precio, "
                + "p.cantidad, p.stock_minimo, p.proveedor, p.activo "
                + "FROM productos p JOIN categorias c ON p.categoria_id = c.id "
                + "WHERE p.activo = TRUE ORDER BY p.nombre";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(mapear(rs));
            }
        }
        return resultado;
    }

    public List<Producto> listarConStockBajo() throws SQLException {
        List<Producto> resultado = new ArrayList<>();
        String sql = "SELECT p.id, p.nombre, p.categoria_id, c.nombre AS categoria_nombre, p.precio, "
                + "p.cantidad, p.stock_minimo, p.proveedor, p.activo "
                + "FROM productos p JOIN categorias c ON p.categoria_id = c.id "
                + "WHERE p.activo = TRUE AND p.cantidad <= p.stock_minimo ORDER BY p.nombre";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(mapear(rs));
            }
        }
        return resultado;
    }

    private Producto mapear(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setId(rs.getInt("id"));
        p.setNombre(rs.getString("nombre"));
        p.setCategoriaId(rs.getInt("categoria_id"));
        p.setCategoriaNombre(rs.getString("categoria_nombre"));
        p.setPrecio(rs.getBigDecimal("precio"));
        p.setCantidad(rs.getInt("cantidad"));
        p.setStockMinimo(rs.getInt("stock_minimo"));
        p.setProveedor(rs.getString("proveedor"));
        p.setActivo(rs.getBoolean("activo"));
        return p;
    }
}
