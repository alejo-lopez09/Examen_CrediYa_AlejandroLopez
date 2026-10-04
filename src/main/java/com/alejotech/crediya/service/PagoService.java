package com.alejotech.crediya.service;

import com.alejotech.crediya.dao.PagoDAO;
import com.alejotech.crediya.dao.PagoRepository;
import com.alejotech.crediya.dao.PrestamoDAO;
import com.alejotech.crediya.dao.PrestamoRepository;
import com.alejotech.crediya.modelo.Pago;
import com.alejotech.crediya.modelo.Prestamo;
import com.alejotech.crediya.util.ArchivoUtil;
import com.alejotech.crediya.util.Validaciones;

import java.math.BigDecimal;
import java.util.List;

public class PagoService {

    private static final String ARCHIVO = "pagos.txt";
    private final PagoRepository pagoDAO;
    private final PrestamoRepository prestamoDAO;

    public PagoService() {
        this(new PagoDAO(), new PrestamoDAO());
    }

    public PagoService(PagoRepository pagoDAO, PrestamoRepository prestamoDAO) {
        this.pagoDAO = pagoDAO;
        this.prestamoDAO = prestamoDAO;
    }

    // Registrar pago
    public boolean registrar(Pago pago) {

        if (pago == null) {
            System.out.println("El pago no puede ser null.");
            return false;
        }

        // Validar préstamo
        if (pago.getPrestamo() == null ||
                pago.getPrestamo().getId() <= 0) {

            System.out.println(
                    "Debe especificar un préstamo válido."
            );

            return false;
        }

        int prestamoId = pago.getPrestamo().getId();

        Prestamo prestamo = prestamoDAO.buscarPorId(prestamoId);

        if (prestamo == null) {

            System.out.println(
                    "El préstamo no existe."
            );

            return false;
        }

        // Validar fecha
        if (pago.getFechaPago() == null) {

            System.out.println(
                    "La fecha del pago es obligatoria."
            );

            return false;
        }
        if (pago.getFechaPago().isAfter(java.time.LocalDate.now())) {
            System.out.println("La fecha del pago no puede estar en el futuro.");
            return false;
        }

        // Validar monto
        try {
            Validaciones.positivo(pago.getMonto(), "El monto del pago");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return false;
        }

        // Obtener saldo
        BigDecimal saldoPendiente =
                pagoDAO.calcularSaldoPendiente(prestamoId);

        if (saldoPendiente.signum() <= 0) {

            System.out.println(
                    "El préstamo ya está completamente pagado."
            );

            return false;
        }

        // Evitar pagar más de lo que se debe
        if (pago.getMonto().compareTo(saldoPendiente) > 0) {

            System.out.println(
                    "El pago supera el saldo pendiente."
            );

            System.out.println(
                    "Saldo pendiente: $" + saldoPendiente
            );

            return false;
        }

        // Registrar pago
        boolean registrado = pagoDAO.guardar(pago);

        if (!registrado) {
            return false;
        }

        sincronizarRespaldo();
        return true;
    }

    public void sincronizarRespaldo() {
        ArchivoUtil.sincronizar(ARCHIVO, pagoDAO.listar().stream()
                .map(p -> ArchivoUtil.registro(p.getId(), p.getPrestamo().getId(),
                        p.getFechaPago(), p.getMonto()))
                .toList());
    }

    // Listar todos los pagos
    public List<Pago> listar() {
        return pagoDAO.listar();
    }

    // Histórico de pagos de un préstamo
    public List<Pago> historial(int prestamoId) {

        if (prestamoId <= 0) {
            return List.of();
        }

        return pagoDAO.buscarPorPrestamo(prestamoId);
    }

    // Total pagado
    public BigDecimal totalPagado(int prestamoId) {

        if (prestamoId <= 0) {
            return BigDecimal.ZERO;
        }

        return pagoDAO.totalPagado(prestamoId);
    }

    // Saldo pendiente
    public BigDecimal saldoPendiente(int prestamoId) {

        if (prestamoId <= 0) {
            return BigDecimal.ZERO;
        }

        return pagoDAO.calcularSaldoPendiente(prestamoId);
    }

    // Eliminar pago
    public boolean eliminar(int id) {

        if (id <= 0) {
            return false;
        }

        if (!pagoDAO.eliminar(id)) {
            return false;
        }
        sincronizarRespaldo();
        return true;
    }
}