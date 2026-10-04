package com.alejotech.crediya.excepciones;

public class CrediYaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CrediYaException(String mensaje) {
        super(mensaje);
    }

    public CrediYaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
