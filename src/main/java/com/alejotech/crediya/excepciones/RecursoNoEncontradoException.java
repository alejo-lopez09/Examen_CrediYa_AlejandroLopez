package com.alejotech.crediya.excepciones;

public class RecursoNoEncontradoException extends CrediYaException {
    private static final long serialVersionUID = 1L;

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
