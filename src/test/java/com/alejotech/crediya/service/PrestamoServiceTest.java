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
import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Test
    void rechazaPrestamoSinClienteAntesDePersistir() {
        FakePrestamoRepository prestamos = new FakePrestamoRepository(
                new Prestamo(1, null, null, BigDecimal.TEN, BigDecimal.ZERO,
                        1, LocalDate.now(), EstadoPrestamo.PENDIENTE));
        PrestamoService service = new PrestamoService(prestamos,
                new FakeClienteRepository(), new FakeEmpleadoRepository(), new FakePagoRepository());

        assertThrows(ValidacionException.class, () -> service.crear(null));
    }

    @Test
    void nuevosPrestamosSiempreInicianPendientes() {
        Prestamo nuevo = new Prestamo(new Cliente(1, "Cliente Demo", "123456",
                "cliente@example.com", "3001234567"),
                new Empleado(1, "Empleado Demo", "654321", "empleado@example.com",
                        "Asesor", BigDecimal.ONE),
                new BigDecimal("100.00"), BigDecimal.ZERO, 2, LocalDate.now(),
                EstadoPrestamo.PAGADO);
        FakePrestamoRepository repositorio = new FakePrestamoRepository(nuevo);
        PrestamoService service = new PrestamoService(repositorio,
                new FakeClienteRepository(), new FakeEmpleadoRepository(), new FakePagoRepository()) {
            @Override
            public void sincronizarRespaldo() {
            }
        };

        service.crear(nuevo);

        assertEquals(EstadoPrestamo.PENDIENTE, repositorio.prestamoGuardado.getEstado());
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
        private Prestamo prestamoGuardado;
        private FakePrestamoRepository(Prestamo prestamo) { this.prestamo = prestamo; }
        @Override public boolean guardar(Prestamo value) { prestamoGuardado = value; return true; }
        @Override public List<Prestamo> listar() { return List.of(prestamo); }
        @Override public Prestamo buscarPorId(int id) { return id == prestamo.getId() ? prestamo : null; }
        @Override public boolean cambiarEstado(int id, EstadoPrestamo estado) { return true; }
        @Override public boolean eliminar(int id) { return true; }
        @Override public List<Prestamo> buscarPorCliente(int clienteId) { return List.of(); }
    }

    private static final class FakeClienteRepository implements ClienteRepository {
        @Override public boolean guardar(Cliente cliente) { return true; }
        @Override public List<Cliente> listar() { return List.of(); }
        @Override public Cliente buscarPorId(int id) {
            return id == 1 ? new Cliente(1, "Cliente Demo", "123456",
                    "cliente@example.com", "3001234567") : null;
        }
        @Override public boolean actualizar(Cliente cliente) { return false; }
        @Override public boolean eliminar(int id) { return false; }
    }

    private static final class FakeEmpleadoRepository implements EmpleadoRepository {
        @Override public boolean guardar(Empleado empleado) { return true; }
        @Override public List<Empleado> listar() { return List.of(); }
        @Override public Empleado buscarPorId(int id) {
            return id == 1 ? new Empleado(1, "Empleado Demo", "654321",
                    "empleado@example.com", "Asesor", BigDecimal.ONE) : null;
        }
        @Override public boolean actualizar(Empleado empleado) { return false; }
        @Override public boolean eliminar(int id) { return false; }
    }
}
