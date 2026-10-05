package com.alejotech.crediya.dao;

import com.alejotech.crediya.modelo.Pago;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface PagoRepository {
    boolean guardar(Pago pago);
List<Pago> listar();
List<Pago> buscarPorPrestamo(int prestamoId);
BigDecimal totalPagado(int prestamoId);
Map<Integer, BigDecimal> totalesPagadosPorPrestamo();
BigDecimal calcularSaldoPendiente(int prestamoId);
boolean eliminar(int id);
}
