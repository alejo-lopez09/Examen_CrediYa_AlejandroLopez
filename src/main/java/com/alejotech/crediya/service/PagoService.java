package com.alejotech.crediya.service;

import com.alejotech.crediya.dao.PagoDAO;
import com.alejotech.crediya.dao.PagoRepository;
import com.alejotech.crediya.dao.PrestamoDAO;
import com.alejotech.crediya.dao.PrestamoRepository;
import com.alejotech.crediya.excepciones.PagoExcedeSaldoException;
import com.alejotech.crediya.excepciones.RecursoNoEncontradoException;
import com.alejotech.crediya.excepciones.ValidacionException;
import com.alejotech.crediya.modelo.Pago;
import com.alejotech.crediya.modelo.Prestamo;
import com.alejotech.crediya.util.ArchivoUtil;
import com.alejotech.crediya.util.Validaciones;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class PagoService {
    private static final String ARCHIVO = "pagos.txt";
    private final PagoRepository pagoRepository;
    private final PrestamoRepository prestamoRepository;

    public PagoService() {
        this(new PagoDAO(), new PrestamoDAO());
    }

    public PagoService(PagoRepository pagoRepository, PrestamoRepository prestamoRepository) {
        this.pagoRepository = pagoRepository;
        this.prestamoRepository = prestamoRepository;
    }

    public void registrar(Pago pago) {
        if (pago == null || pago.getPrestamo() == null || pago.getPrestamo().getId() <= 0) {
            throw new ValidacionException("Debe especificar un pago y un préstamo válidos.");
        }
        int prestamoId = pago.getPrestamo().getId();
        Prestamo prestamo = prestamoRepository.buscarPorId(prestamoId);
        if (prestamo == null) {
            throw new RecursoNoEncontradoException("El préstamo no existe.");
        }
        if (pago.getFechaPago() == null) {
            throw new ValidacionException("La fecha del pago es obligatoria.");
        }
        if (pago.getFechaPago().isAfter(LocalDate.now())) {
            throw new ValidacionException("La fecha del pago no puede estar en el futuro.");
        }
        Validaciones.positivo(pago.getMonto(), "El monto del pago");
        BigDecimal saldoPendiente = pagoRepository.calcularSaldoPendiente(prestamoId);
        if (saldoPendiente.signum() <= 0) {
            throw new ValidacionException("El préstamo ya está completamente pagado.");
        }
        if (pago.getMonto().compareTo(saldoPendiente) > 0) {
            throw new PagoExcedeSaldoException(saldoPendiente);
        }
        if (!pagoRepository.guardar(pago)) {
            throw new ValidacionException("No se pudo registrar el pago.");
        }
        sincronizarRespaldo();
    }

    public void sincronizarRespaldo() {
        ArchivoUtil.sincronizar(ARCHIVO, pagoRepository.listar().stream()
                .map(p -> ArchivoUtil.registro(p.getId(), p.getPrestamo().getId(),
                        p.getFechaPago(), p.getMonto()))
                .toList());
    }

    public List<Pago> listar() {
        return pagoRepository.listar();
    }

    public List<Pago> historial(int prestamoId) {
        validarPrestamoExiste(prestamoId);
        return pagoRepository.buscarPorPrestamo(prestamoId);
    }

    public BigDecimal totalPagado(int prestamoId) {
        validarPrestamoExiste(prestamoId);
        return pagoRepository.totalPagado(prestamoId);
    }

    public Map<Integer, BigDecimal> totalesPagadosPorPrestamo() {
        return pagoRepository.totalesPagadosPorPrestamo();
    }

    public BigDecimal saldoPendiente(int prestamoId) {
        validarPrestamoExiste(prestamoId);
        return pagoRepository.calcularSaldoPendiente(prestamoId);
    }

    public void eliminar(int id) {
        validarId(id, "pago");
        if (!pagoRepository.eliminar(id)) {
            throw new RecursoNoEncontradoException("El pago no existe.");
        }
        sincronizarRespaldo();
    }

    private void validarId(int id, String recurso) {
        if (id <= 0) {
            throw new ValidacionException("El ID del " + recurso + " debe ser positivo.");
        }
    }

    private void validarPrestamoExiste(int prestamoId) {
        validarId(prestamoId, "préstamo");
        if (prestamoRepository.buscarPorId(prestamoId) == null) {
            throw new RecursoNoEncontradoException("El préstamo no existe.");
        }
    }
}
