package com.pearlgifts.inventario.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Maneja la conexion a la base de datos MySQL local, leyendo los
 * parametros desde config.properties (ubicado en la raiz del proyecto).
 */
public class ConexionDB {

    private static Properties propiedades;

    private static Properties cargarPropiedades() throws IOException {
        if (propiedades != null) {
            return propiedades;
        }
        propiedades = new Properties();
        try (InputStream is = new FileInputStream("config.properties")) {
            propiedades.load(is);
        }
        return propiedades;
    }

    public static Connection obtenerConexion() throws SQLException {
        try {
            Properties props = cargarPropiedades();
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");
            return DriverManager.getConnection(url, user, password);
        } catch (IOException e) {
            throw new SQLException("No se pudo leer config.properties: " + e.getMessage(), e);
        }
    }
}
