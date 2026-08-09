package com.pearlgifts.inventario.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Genera y valida hashes SHA-256 de contrasenas.
 * No se guarda ninguna contrasena en texto plano en la base de datos.
 */
public class PasswordUtil {

    public static String hash(String textoPlano) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytesHash = digest.digest(textoPlano.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytesHash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("Error generando hash de contrasena", e);
        }
    }

    public static boolean verificar(String textoPlano, String hashGuardado) {
        return hash(textoPlano).equals(hashGuardado);
    }
}
