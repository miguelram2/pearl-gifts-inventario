package com.pearlgifts.inventario.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class PasswordUtilTest {

    @Test
    public void hashGeneraSiempreElMismoResultado() {
        String hash1 = PasswordUtil.hash("admin123");
        String hash2 = PasswordUtil.hash("admin123");
        assertEquals(hash1, hash2);
    }

    @Test
    public void hashDeContrasenasDistintasEsDistinto() {
        String hash1 = PasswordUtil.hash("admin123");
        String hash2 = PasswordUtil.hash("otraClave");
        assertNotEquals(hash1, hash2);
    }

    @Test
    public void verificarRegresaTrueConContrasenaCorrecta() {
        String hash = PasswordUtil.hash("miClaveSegura");
        assertTrue(PasswordUtil.verificar("miClaveSegura", hash));
    }

    @Test
    public void verificarRegresaFalseConContrasenaIncorrecta() {
        String hash = PasswordUtil.hash("miClaveSegura");
        assertFalse(PasswordUtil.verificar("claveEquivocada", hash));
    }
}
