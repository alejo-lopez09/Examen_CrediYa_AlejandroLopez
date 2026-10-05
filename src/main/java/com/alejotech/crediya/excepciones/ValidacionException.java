package com.alejotech.crediya.excepciones;

public class ValidacionException extends CrediYaException {
    private static final long serialVersionUID = 1L;

    public ValidacionException(String mensaje) {
        super(mensaje);
    }
}
