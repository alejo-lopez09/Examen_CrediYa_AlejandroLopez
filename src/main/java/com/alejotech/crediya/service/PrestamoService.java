package com.alejotech.crediya.service;

import com.alejotech.crediya.dao.ClienteDAO;
import com.alejotech.crediya.dao.ClienteRepository;
import com.alejotech.crediya.dao.EmpleadoDAO;
import com.alejotech.crediya.dao.EmpleadoRepository;
import com.alejotech.crediya.dao.PagoDAO;
import com.alejotech.crediya.dao.PagoRepository;
import com.alejotech.crediya.dao.PrestamoDAO;
import com.alejotech.crediya.dao.PrestamoRepository;
import com.alejotech.crediya.modelo.Cliente;
import com.alejotech.crediya.modelo.Empleado;
import com.alejotech.crediya.modelo.EstadoPrestamo;
import com.alejotech.crediya.modelo.Prestamo;
import com.alejotech.crediya.util.ArchivoUtil;
import com.alejotech.crediya.util.Validaciones;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

public class PrestamoService {

    private static final String ARCHIVO = "prestamos.txt";
    private final PrestamoRepository prestamoDAO;
    private final ClienteRepository clienteDAO;
    private final EmpleadoRepository empleadoDAO;
    private final PagoRepository pagoDAO;

    public PrestamoService() {
        this(new PrestamoDAO(), new ClienteDAO(), new EmpleadoDAO(), new PagoDAO());
    }

    public PrestamoService(PrestamoRepository prestamoDAO, ClienteRepository clienteDAO,
                           EmpleadoRepository empleadoDAO, PagoRepository pagoDAO) {
        this.prestamoDAO = prestamoDAO;
        this.clienteDAO = clienteDAO;
        this.empleadoDAO = empleadoDAO;
        this.pagoDAO = pagoDAO;
    }

    // Crear préstamo
    public boolean crear(Prestamo prestamo) {

        if (prestamo == null) {
            System.out.println("El préstamo no puede ser null.");
            return false;
        }

        // Validar cliente
        if (prestamo.getCliente() == null ||
                prestamo.getCliente().getId() <= 0) {

            System.out.println("Debe especificar un cliente válido.");
            return false;
        }

        Cliente cliente = clienteDAO.buscarPorId(
                prestamo.getCliente().getId()
        );

        if (cliente == null) {
            System.out.println("El cliente no existe.");
            return false;
        }

        // Validar empleado
        if (prestamo.getEmpleado() == null ||
                prestamo.getEmpleado().getId() <= 0) {

            System.out.println("Debe especificar un empleado válido.");
            return false;
        }

        Empleado empleado = empleadoDAO.buscarPorId(
                prestamo.getEmpleado().getId()
        );

        if (empleado == null) {
            System.out.println("El empleado no existe.");
            return false;
        }

        // Validar monto
        try {
            Validaciones.positivo(prestamo.getMonto(), "El monto");
            Validaciones.interes(prestamo.getInteres());
            Validaciones.cuotas(prestamo.getCuotas());
            Validaciones.requerido(
                    prestamo.getFechaInicio() == null ? null : prestamo.getFechaInicio().toString(),
                    "La fecha de inicio");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return false;
        }

        if (prestamo.getEstado() == null ||
                prestamo.getEstado().isBlank()) {
            prestamo.setEstado(EstadoPrestamo.PENDIENTE.name());
        } else {
            try {
                prestamo.setEstado(EstadoPrestamo.desdeTexto(prestamo.getEstado()).name());
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                return false;
            }
        }

        if (!prestamoDAO.guardar(prestamo)) {
            return false;
        }
        sincronizarRespaldo();
        return true;
    }

    public void sincronizarRespaldo() {
        ArchivoUtil.sincronizar(ARCHIVO, prestamoDAO.listar().stream()
                .map(p -> ArchivoUtil.registro(p.getId(), p.getCliente().getId(),
                        p.getEmpleado().getId(), p.getMonto(), p.getInteres(), p.getCuotas(),
                        p.getFechaInicio(), p.getEstado()))
                .toList());
    }

    // Listar préstamos
    public List<Prestamo> listar() {
        return prestamoDAO.listar();
    }

    // Buscar préstamo
    public Prestamo buscarPorId(int id) {

        if (id <= 0) {
            return null;
        }

        return prestamoDAO.buscarPorId(id);
    }

    // Cambiar estado
    public boolean cambiarEstado(int id, String estado) {

        if (id <= 0) {
            return false;
        }

        if (estado == null || estado.isBlank()) {
            return false;
        }

        estado = estado.trim().toUpperCase(Locale.ROOT);

        if (!estado.equals("PENDIENTE") &&
                !estado.equals("PAGADO")) {

            System.out.println(
                    "Estado inválido. Use PENDIENTE o PAGADO."
            );

            return false;
        }
        BigDecimal saldoPendiente = pagoDAO.calcularSaldoPendiente(id);
        if (estado.equals("PAGADO") && saldoPendiente.signum() > 0) {
            System.out.println("No se puede marcar como pagado mientras exista saldo pendiente.");
            return false;
        }
        if (estado.equals("PENDIENTE") && saldoPendiente.signum() <= 0) {
            System.out.println("Un préstamo sin saldo pendiente debe permanecer pagado.");
            return false;
        }
        if (!prestamoDAO.cambiarEstado(id, estado)) {
            return false;
        }
        sincronizarRespaldo();
        return true;
    }

    // Obtener préstamos de un cliente
    public List<Prestamo> buscarPorCliente(int clienteId) {

        if (clienteId <= 0) {
            return List.of();
        }

        return prestamoDAO.buscarPorCliente(clienteId);
    }

    // Eliminar préstamo
    public boolean eliminar(int id) {

        if (id <= 0) {
            return false;
        }

        if (!prestamoDAO.eliminar(id)) {
            return false;
        }
        sincronizarRespaldo();
        return true;
    }
}