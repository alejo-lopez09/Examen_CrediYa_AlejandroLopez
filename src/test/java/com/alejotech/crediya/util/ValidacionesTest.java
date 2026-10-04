package com.alejotech.crediya.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidacionesTest {

    @Test
    void aceptaDatosConFormatoValido() {
        assertDoesNotThrow(() -> Validaciones.documento("123456789"));
        assertDoesNotThrow(() -> Validaciones.correo("cliente@example.com"));
        assertDoesNotThrow(() -> Validaciones.telefono("3001234567"));
        assertDoesNotThrow(() -> Validaciones.positivo(new BigDecimal("12.50"), "Monto"));
    }

    @Test
    void rechazaDatosInvalidosYMasDeDosDecimales() {
        assertThrows(IllegalArgumentException.class,
                () -> Validaciones.documento("doc-123"));
        assertThrows(IllegalArgumentException.class,
                () -> Validaciones.correo("no-es-correo"));
        assertThrows(IllegalArgumentException.class,
                () -> Validaciones.telefono("123"));
        assertThrows(IllegalArgumentException.class,
                () -> Validaciones.positivo(new BigDecimal("1.001"), "Monto"));
    }
}
