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
                new BigDecimal("10.00"), 4, LocalDate.now(), "PENDIENTE");

        assertEquals(new BigDecimal("1100.00"), prestamo.calcularMontoTotal());
        assertEquals(new BigDecimal("275.00"), prestamo.calcularCuotaMensual());
    }

    @Test
    void redondeaElInteresYLaCuotaADosDecimales() {
        Prestamo prestamo = new Prestamo(
                null, null, new BigDecimal("100.05"),
                new BigDecimal("10.00"), 3, LocalDate.now(), "PENDIENTE");

        assertEquals(new BigDecimal("110.06"), prestamo.calcularMontoTotal());
        assertEquals(new BigDecimal("36.69"), prestamo.calcularCuotaMensual());
    }

    @Test
    void marcaVencimientoSoloParaPrestamosPendientesFueraDelPlazo() {
        Prestamo vencido = new Prestamo(
                null, null, BigDecimal.TEN, BigDecimal.ZERO,
                4, LocalDate.now().minusMonths(5), "PENDIENTE");
        Prestamo pagado = new Prestamo(
                null, null, BigDecimal.TEN, BigDecimal.ZERO,
                4, LocalDate.now().minusMonths(5), "PAGADO");

        assertTrue(vencido.estaVencido());
        assertFalse(pagado.estaVencido());
    }
}
