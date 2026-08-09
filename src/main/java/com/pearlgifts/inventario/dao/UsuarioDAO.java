package com.pearlgifts.inventario.dao;

import com.pearlgifts.inventario.model.Usuario;
import com.pearlgifts.inventario.util.ConexionDB;
import com.pearlgifts.inventario.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    /** Valida usuario/contrasena. Regresa el Usuario si es correcto, null si no. */
    public Usuario autenticar(String usuario, String passwordPlano) throws SQLException {
        String sql = "SELECT id, usuario, password_hash, nombre_completo, rol, activo "
                + "FROM usuarios WHERE usuario = ? AND activo = TRUE";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashGuardado = rs.getString("password_hash");
                    if (PasswordUtil.verificar(passwordPlano, hashGuardado)) {
                        return mapear(rs);
                    }
                }
            }
        }
        return null;
    }

    public void agregar(Usuario u, String passwordPlano) throws SQLException {
        String sql = "INSERT INTO usuarios (usuario, password_hash, nombre_completo, rol, activo) "
                + "VALUES (?, ?, ?, ?, TRUE)";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getUsuario());
            ps.setString(2, PasswordUtil.hash(passwordPlano));
            ps.setString(3, u.getNombreCompleto());
            ps.setString(4, u.getRol().name());
            ps.executeUpdate();
        }
    }

    public void darDeBaja(int id) throws SQLException {
        String sql = "UPDATE usuarios SET activo = FALSE WHERE id = ?";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Usuario> listarActivos() throws SQLException {
        List<Usuario> resultado = new ArrayList<>();
        String sql = "SELECT id, usuario, password_hash, nombre_completo, rol, activo "
                + "FROM usuarios WHERE activo = TRUE ORDER BY nombre_completo";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(mapear(rs));
            }
        }
        return resultado;
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setUsuario(rs.getString("usuario"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setNombreCompleto(rs.getString("nombre_completo"));
        u.setRol(Usuario.Rol.valueOf(rs.getString("rol")));
        u.setActivo(rs.getBoolean("activo"));
        return u;
    }
}
