package com.alejotech.crediya.service;

import com.alejotech.crediya.dao.ClienteDAO;
import com.alejotech.crediya.dao.ClienteRepository;
import com.alejotech.crediya.dao.EmpleadoDAO;
import com.alejotech.crediya.dao.EmpleadoRepository;
import com.alejotech.crediya.dao.PagoDAO;
import com.alejotech.crediya.dao.PagoRepository;
import com.alejotech.crediya.dao.PrestamoDAO;
import com.alejotech.crediya.dao.PrestamoRepository;
import com.alejotech.crediya.excepciones.RecursoNoEncontradoException;
import com.alejotech.crediya.excepciones.ValidacionException;
import com.alejotech.crediya.modelo.Cliente;
import com.alejotech.crediya.modelo.Empleado;
import com.alejotech.crediya.modelo.EstadoPrestamo;
import com.alejotech.crediya.modelo.Prestamo;
import com.alejotech.crediya.util.ArchivoUtil;
import com.alejotech.crediya.util.Validaciones;

import java.math.BigDecimal;
import java.util.List;

public class PrestamoService {
    private static final String ARCHIVO = "prestamos.txt";
    private final PrestamoRepository prestamoRepository;
    private final ClienteRepository clienteRepository;
    private final EmpleadoRepository empleadoRepository;
    private final PagoRepository pagoRepository;

    public PrestamoService() {
        this(new PrestamoDAO(), new ClienteDAO(), new EmpleadoDAO(), new PagoDAO());
    }

    public PrestamoService(PrestamoRepository prestamoRepository, ClienteRepository clienteRepository,
                           EmpleadoRepository empleadoRepository, PagoRepository pagoRepository) {
        this.prestamoRepository = prestamoRepository;
        this.clienteRepository = clienteRepository;
        this.empleadoRepository = empleadoRepository;
        this.pagoRepository = pagoRepository;
    }

    public void crear(Prestamo prestamo) {
        if (prestamo == null || prestamo.getCliente() == null || prestamo.getCliente().getId() <= 0) {
            throw new ValidacionException("Debe especificar un cliente válido.");
        }
        Cliente cliente = clienteRepository.buscarPorId(prestamo.getCliente().getId());
        if (cliente == null) {
            throw new RecursoNoEncontradoException("El cliente no existe.");
        }
        if (prestamo.getEmpleado() == null || prestamo.getEmpleado().getId() <= 0) {
            throw new ValidacionException("Debe especificar un empleado válido.");
        }
        Empleado empleado = empleadoRepository.buscarPorId(prestamo.getEmpleado().getId());
        if (empleado == null) {
            throw new RecursoNoEncontradoException("El empleado no existe.");
        }
        Validaciones.positivo(prestamo.getMonto(), "El monto");
        Validaciones.maximoDigitosEnteros(prestamo.getMonto(), 10, "El monto");
        Validaciones.interes(prestamo.getInteres());
        Validaciones.cuotas(prestamo.getCuotas());
        if (prestamo.getFechaInicio() == null) {
            throw new ValidacionException("La fecha de inicio es obligatoria.");
        }
        prestamo.setEstado(EstadoPrestamo.PENDIENTE);
        if (!prestamoRepository.guardar(prestamo)) {
            throw new ValidacionException("No se pudo registrar el préstamo.");
        }
        sincronizarRespaldo();
    }

    public void sincronizarRespaldo() {
        ArchivoUtil.sincronizar(ARCHIVO, prestamoRepository.listar().stream()
                .map(p -> ArchivoUtil.registro(p.getId(), p.getCliente().getId(),
                        p.getEmpleado().getId(), p.getMonto(), p.getInteres(), p.getCuotas(),
                        p.getFechaInicio(), p.getEstado()))
                .toList());
    }

    public List<Prestamo> listar() {
        return prestamoRepository.listar();
    }

    public Prestamo buscarPorId(int id) {
        validarId(id);
        return prestamoRepository.buscarPorId(id);
    }

    public void cambiarEstado(int id, EstadoPrestamo estado) {
        validarId(id);
        if (estado == null) {
            throw new ValidacionException("El estado del préstamo es obligatorio.");
        }
        if (prestamoRepository.buscarPorId(id) == null) {
            throw new RecursoNoEncontradoException("El préstamo no existe.");
        }
        BigDecimal saldoPendiente = pagoRepository.calcularSaldoPendiente(id);
        if (estado == EstadoPrestamo.PAGADO && saldoPendiente.signum() > 0) {
            throw new ValidacionException("No se puede marcar como pagado mientras exista saldo pendiente.");
        }
        if (estado == EstadoPrestamo.PENDIENTE && saldoPendiente.signum() <= 0) {
            throw new ValidacionException("Un préstamo sin saldo pendiente debe permanecer pagado.");
        }
        if (!prestamoRepository.cambiarEstado(id, estado)) {
            throw new RecursoNoEncontradoException("No se pudo actualizar el estado del préstamo.");
        }
        sincronizarRespaldo();
    }

    public List<Prestamo> buscarPorCliente(int clienteId) {
        validarId(clienteId);
        return prestamoRepository.buscarPorCliente(clienteId);
    }

    public void eliminar(int id) {
        validarId(id);
        if (!prestamoRepository.eliminar(id)) {
            throw new RecursoNoEncontradoException("El préstamo no existe o tiene pagos asociados.");
        }
        sincronizarRespaldo();
    }

    private void validarId(int id) {
        if (id <= 0) {
            throw new ValidacionException("El ID del préstamo debe ser positivo.");
        }
    }
}
