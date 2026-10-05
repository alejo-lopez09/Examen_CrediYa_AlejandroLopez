package com.alejotech.crediya.excepciones;

public class RecursoEnUsoException extends CrediYaException {
    private static final long serialVersionUID = 1L;

    public RecursoEnUsoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
