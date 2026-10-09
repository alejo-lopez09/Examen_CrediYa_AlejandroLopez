package com.alejotech.crediya.service;

import com.alejotech.crediya.excepciones.ValidacionException;
import com.alejotech.crediya.modelo.Pago;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GestorPagos {

    private final List<Pago> pagos = new ArrayList<>();

    public void registrarPago(Pago pago) {
        validar(pago);
        pagos.add(pago);
    }

    public void reemplazar(List<Pago> origen) {
        pagos.clear();
        if (origen != null) {
            pagos.addAll(origen);
        }
    }

    public void descartar(Pago pago) {
        pagos.remove(pago);
    }

    public void descartarPorId(int id) {
        pagos.removeIf(pago -> pago.getId() == id);
    }

    public void listarPagos() {
        if (pagos.isEmpty()) {
            System.out.println("No hay pagos registrados.");
            return;
        }
        System.out.println("--- Pagos registrados ---");
        pagos.forEach(pago -> System.out.println(pago));
    }

    public void mostrarPagosMayoresA(double valor) {
        if (!Double.isFinite(valor) || valor < 0) {
            throw new ValidacionException(
                    "El valor de comparación debe ser finito y no puede ser negativo.");
        }
        if (pagos.isEmpty()) {
            System.out.println("No hay pagos registrados.");
            return;
        }
        BigDecimal limite = BigDecimal.valueOf(valor);
        List<Pago> mayores = pagos.stream()
                .filter(pago -> pago.getMonto().compareTo(limite) > 0)
                .toList();
        if (mayores.isEmpty()) {
            System.out.println("No hay pagos con monto mayor a " + valor + ".");
            return;
        }
        mayores.forEach(pago -> System.out.println(
                "ID: " + pago.getId() + " | Monto: " + pago.getMonto()));
    }

    public List<Pago> getPagos() {
        return Collections.unmodifiableList(pagos);
    }

    private void validar(Pago pago) {
        if (pago == null) {
            throw new ValidacionException("El pago no puede ser nulo.");
        }
        if (pago.getMonto() == null) {
            throw new ValidacionException("El monto del pago es obligatorio.");
        }
        if (pago.getMonto().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacionException("El monto del pago no puede ser negativo.");
        }
        if (pago.getFechaPago() == null) {
            throw new ValidacionException("La fecha del pago no puede ser nula.");
        }
    }
}
