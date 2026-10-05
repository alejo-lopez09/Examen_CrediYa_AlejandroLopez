package com.alejotech.crediya.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;
import com.alejotech.crediya.excepciones.ValidacionException;

public final class Validaciones {

    private static final Pattern CORREO =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern DOCUMENTO =
            Pattern.compile("^\\d{5,20}$");
    private static final Pattern TELEFONO =
            Pattern.compile("^\\d{7,15}$");

    private Validaciones() {}

    public static void requerido(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new ValidacionException(campo + " es obligatorio.");
        }
    }

    public static String texto(String valor, String campo, int longitudMaxima) {
        requerido(valor, campo);
        String normalizado = valor.trim();
        if (normalizado.length() > longitudMaxima) {
            throw new ValidacionException(campo + " no puede superar "
                    + longitudMaxima + " caracteres.");
        }
        return normalizado;
    }

    public static void documento(String valor) {
        requerido(valor, "El documento");
        if (!DOCUMENTO.matcher(valor.trim()).matches()) {
            throw new ValidacionException(
                    "El documento debe contener entre 5 y 20 dígitos.");
        }
    }

    public static void correo(String valor) {
        requerido(valor, "El correo");
        if (!CORREO.matcher(valor.trim()).matches()) {
            throw new ValidacionException("El correo no tiene un formato válido.");
        }
    }

    public static void telefono(String valor) {
        requerido(valor, "El teléfono");
        if (!TELEFONO.matcher(valor.trim()).matches()) {
            throw new ValidacionException(
                    "El teléfono debe contener entre 7 y 15 dígitos.");
        }
    }

    public static void positivo(BigDecimal valor, String campo) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacionException(campo + " debe ser mayor que cero.");
        }
        maximoDosDecimales(valor, campo);
    }

    public static void maximoDigitosEnteros(BigDecimal valor, int digitos, String campo) {
        if (valor != null && valor.precision() - valor.scale() > digitos) {
            throw new ValidacionException(campo + " supera el máximo permitido.");
        }
    }

    public static void noNegativo(BigDecimal valor, String campo) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacionException(campo + " no puede ser negativo.");
        }
        maximoDosDecimales(valor, campo);
    }

    private static void maximoDosDecimales(BigDecimal valor, String campo) {
        if (valor.stripTrailingZeros().scale() > 2) {
            throw new ValidacionException(campo + " admite máximo dos decimales.");
        }
    }

    public static void interes(BigDecimal valor) {
        noNegativo(valor, "El interés");
        if (valor.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new ValidacionException("El interés no puede ser mayor al 100%.");
        }
    }

    public static void cuotas(int valor) {
        if (valor <= 0) {
            throw new ValidacionException("Las cuotas deben ser mayores que cero.");
        }
    }
}
