package com.alejotech.crediya.modelo;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrestamoTest {

    @Test
    void calculaInteresSimpleYCuotaMensual() {
        Prestamo prestamo = new Prestamo(
                null, null, new BigDecimal("1000.00"),
                new BigDecimal("10.00"), 4, LocalDate.now(), EstadoPrestamo.PENDIENTE);

        assertEquals(new BigDecimal("1100.00"), prestamo.calcularMontoTotal());
        assertEquals(new BigDecimal("275.00"), prestamo.calcularCuotaMensual());
    }

    @Test
    void redondeaElInteresYLaCuotaADosDecimales() {
        Prestamo prestamo = new Prestamo(
                null, null, new BigDecimal("100.05"),
                new BigDecimal("10.00"), 3, LocalDate.now(), EstadoPrestamo.PENDIENTE);

        assertEquals(new BigDecimal("110.06"), prestamo.calcularMontoTotal());
        assertEquals(new BigDecimal("36.69"), prestamo.calcularCuotaMensual());
    }

    @Test
    void marcaMoraCuandoLosPagosNoCubrenLasCuotasEsperadas() {
        Prestamo prestamo = new Prestamo(
                null, null, BigDecimal.TEN, BigDecimal.ZERO,
                4, LocalDate.now().minusMonths(3), EstadoPrestamo.PENDIENTE);

        assertTrue(prestamo.estaVencido(new BigDecimal("5.00"), LocalDate.now()));
        assertFalse(prestamo.estaVencido(new BigDecimal("7.50"), LocalDate.now()));
        prestamo.setEstado(EstadoPrestamo.PAGADO);
        assertFalse(prestamo.estaVencido(BigDecimal.ZERO, LocalDate.now()));
    }
}
