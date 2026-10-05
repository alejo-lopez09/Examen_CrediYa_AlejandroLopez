package com.alejotech.crediya.service;

import com.alejotech.crediya.dao.PagoRepository;
import com.alejotech.crediya.dao.PrestamoRepository;
import com.alejotech.crediya.excepciones.PagoExcedeSaldoException;
import com.alejotech.crediya.excepciones.ValidacionException;
import com.alejotech.crediya.modelo.EstadoPrestamo;
import com.alejotech.crediya.modelo.Pago;
import com.alejotech.crediya.modelo.Prestamo;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PagoServiceTest {
    @Test
    void rechazaPagoSuperiorAlSaldoSinGuardar() {
        Prestamo prestamo = new Prestamo(1, null, null, BigDecimal.TEN,
                BigDecimal.ZERO, 1, LocalDate.now(), EstadoPrestamo.PENDIENTE);
        FakePagoRepository pagos = new FakePagoRepository();
        PagoService service = new PagoService(pagos, new FakePrestamoRepository(prestamo));
        PagoExcedeSaldoException error = assertThrows(PagoExcedeSaldoException.class,
                () -> service.registrar(new Pago(prestamo, LocalDate.now(),
                        new BigDecimal("150.00"))));

        assertEquals(new BigDecimal("100.00"), error.getSaldoPendiente());
        assertFalse(pagos.guardado);
    }

    @Test
    void rechazaFechaDePagoFuturaAntesDeConsultarSaldo() {
        Prestamo prestamo = new Prestamo(1, null, null, BigDecimal.TEN,
                BigDecimal.ZERO, 1, LocalDate.now(), EstadoPrestamo.PENDIENTE);
        FakePagoRepository pagos = new FakePagoRepository();
        PagoService service = new PagoService(pagos, new FakePrestamoRepository(prestamo));

        assertThrows(ValidacionException.class,
                () -> service.registrar(new Pago(prestamo, LocalDate.now().plusDays(1), BigDecimal.ONE)));
        assertFalse(pagos.guardado);
    }

    private static final class FakePagoRepository implements PagoRepository {
        private boolean guardado;

        @Override public boolean guardar(Pago pago) { guardado = true; return true; }
        @Override public List<Pago> listar() { return List.of(); }
        @Override public List<Pago> buscarPorPrestamo(int prestamoId) { return List.of(); }
        @Override public BigDecimal totalPagado(int prestamoId) { return BigDecimal.ZERO; }
        @Override public Map<Integer, BigDecimal> totalesPagadosPorPrestamo() { return Map.of(); }
        @Override public BigDecimal calcularSaldoPendiente(int prestamoId) {
            return new BigDecimal("100.00");
        }
        @Override public boolean eliminar(int id) { return false; }
    }

    private static final class FakePrestamoRepository implements PrestamoRepository {
        private final Prestamo prestamo;

        private FakePrestamoRepository(Prestamo prestamo) {
            this.prestamo = prestamo;
        }

        @Override public boolean guardar(Prestamo value) { return true; }
        @Override public List<Prestamo> listar() { return List.of(prestamo); }
        @Override public Prestamo buscarPorId(int id) { return id == prestamo.getId() ? prestamo : null; }
        @Override public boolean cambiarEstado(int id, EstadoPrestamo estado) { return true; }
        @Override public boolean eliminar(int id) { return true; }
        @Override public List<Prestamo> buscarPorCliente(int clienteId) { return List.of(); }
    }
}
