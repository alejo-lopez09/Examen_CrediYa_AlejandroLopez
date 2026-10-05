package com.alejotech.crediya.service;

import com.alejotech.crediya.dao.ClienteRepository;
import com.alejotech.crediya.dao.EmpleadoRepository;
import com.alejotech.crediya.dao.PagoRepository;
import com.alejotech.crediya.dao.PrestamoRepository;
import com.alejotech.crediya.excepciones.ValidacionException;
import com.alejotech.crediya.modelo.Cliente;
import com.alejotech.crediya.modelo.Empleado;
import com.alejotech.crediya.modelo.EstadoPrestamo;
import com.alejotech.crediya.modelo.Pago;
import com.alejotech.crediya.modelo.Prestamo;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PrestamoServiceTest {
    @Test
    void noPermiteMarcarPagadoConSaldoPendiente() {
        Prestamo prestamo = new Prestamo(1, null, null, BigDecimal.TEN,
                BigDecimal.ZERO, 1, LocalDate.now(), EstadoPrestamo.PENDIENTE);
        PrestamoRepository prestamos = new FakePrestamoRepository(prestamo);
        PagoRepository pagos = new FakePagoRepository();
        PrestamoService service = new PrestamoService(prestamos,
                new FakeClienteRepository(), new FakeEmpleadoRepository(), pagos);

        assertThrows(ValidacionException.class,
                () -> service.cambiarEstado(1, EstadoPrestamo.PAGADO));
    }

    private static final class FakePagoRepository implements PagoRepository {
        @Override public boolean guardar(Pago pago) { return true; }
        @Override public List<Pago> listar() { return List.of(); }
        @Override public List<Pago> buscarPorPrestamo(int prestamoId) { return List.of(); }
        @Override public BigDecimal totalPagado(int prestamoId) { return BigDecimal.ZERO; }
        @Override public Map<Integer, BigDecimal> totalesPagadosPorPrestamo() { return Map.of(); }
        @Override public BigDecimal calcularSaldoPendiente(int prestamoId) { return BigDecimal.ONE; }
        @Override public boolean eliminar(int id) { return false; }
    }

    private static final class FakePrestamoRepository implements PrestamoRepository {
        private final Prestamo prestamo;
        private FakePrestamoRepository(Prestamo prestamo) { this.prestamo = prestamo; }
        @Override public boolean guardar(Prestamo value) { return true; }
        @Override public List<Prestamo> listar() { return List.of(prestamo); }
        @Override public Prestamo buscarPorId(int id) { return id == prestamo.getId() ? prestamo : null; }
        @Override public boolean cambiarEstado(int id, EstadoPrestamo estado) { return true; }
        @Override public boolean eliminar(int id) { return true; }
        @Override public List<Prestamo> buscarPorCliente(int clienteId) { return List.of(); }
    }

    private static final class FakeClienteRepository implements ClienteRepository {
        @Override public boolean guardar(Cliente cliente) { return true; }
        @Override public List<Cliente> listar() { return List.of(); }
        @Override public Cliente buscarPorId(int id) { return null; }
        @Override public boolean actualizar(Cliente cliente) { return false; }
        @Override public boolean eliminar(int id) { return false; }
    }

    private static final class FakeEmpleadoRepository implements EmpleadoRepository {
        @Override public boolean guardar(Empleado empleado) { return true; }
        @Override public List<Empleado> listar() { return List.of(); }
        @Override public Empleado buscarPorId(int id) { return null; }
        @Override public boolean actualizar(Empleado empleado) { return false; }
        @Override public boolean eliminar(int id) { return false; }
    }
}
