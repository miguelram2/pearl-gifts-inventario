package com.pearlgifts.inventario.util;

import com.pearlgifts.inventario.model.Usuario;

/** Guarda el usuario autenticado durante la ejecucion de la aplicacion. */
public class SesionActual {

    private static Usuario usuario;

    public static void iniciar(Usuario u) {
        usuario = u;
    }

    public static Usuario get() {
        return usuario;
    }

    public static boolean esAdmin() {
        return usuario != null && usuario.isAdmin();
    }

    public static void cerrar() {
        usuario = null;
    }
}
