package com.alejotech.crediya.modelo;

public enum EstadoPrestamo {
    PENDIENTE,
    PAGADO;

    public static EstadoPrestamo desdeTexto(String valor) {
        if (valor == null || valor.isBlank()) {
            return PENDIENTE;
        }
        try {
            return valueOf(valor.trim().toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado de préstamo inválido: " + valor);
        }
    }
}
