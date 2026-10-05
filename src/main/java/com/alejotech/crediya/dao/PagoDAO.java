package com.alejotech.crediya.dao;

import com.alejotech.crediya.conexion.ConexionDB;
import com.alejotech.crediya.excepciones.CrediYaException;
import com.alejotech.crediya.excepciones.PagoExcedeSaldoException;
import com.alejotech.crediya.excepciones.RecursoNoEncontradoException;
import com.alejotech.crediya.modelo.EstadoPrestamo;
import com.alejotech.crediya.modelo.Pago;
import com.alejotech.crediya.modelo.Prestamo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class PagoDAO implements PagoRepository {

    @Override
    public boolean guardar(Pago pago) {
        String lockSql = "SELECT monto, interes FROM prestamos WHERE id = ? FOR UPDATE";
        String totalPagadoSql = "SELECT COALESCE(SUM(monto), 0) FROM pagos WHERE prestamo_id = ?";
        String insertSql = """
                INSERT INTO pagos (prestamo_id, fecha_pago, monto)
                VALUES (?, ?, ?)
                """;
        String estadoSql = "UPDATE prestamos SET estado = ? WHERE id = ?";

        try (Connection conexion = ConexionDB.getConnection()) {
            conexion.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            conexion.setAutoCommit(false);
            try {
                BigDecimal totalPrestamo;
                try (PreparedStatement ps = conexion.prepareStatement(lockSql)) {
                    ps.setInt(1, pago.getPrestamo().getId());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new RecursoNoEncontradoException("El préstamo no existe.");
                        }
                        BigDecimal monto = rs.getBigDecimal("monto");
                        BigDecimal interes = rs.getBigDecimal("interes");
                        totalPrestamo = Prestamo.calcularMontoTotal(monto, interes);
                    }
                }

                BigDecimal totalPagado;
                try (PreparedStatement ps = conexion.prepareStatement(totalPagadoSql)) {
                    ps.setInt(1, pago.getPrestamo().getId());
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        totalPagado = rs.getBigDecimal(1);
                    }
                }

                BigDecimal saldoPendiente = totalPrestamo.subtract(totalPagado);
                if (pago.getMonto().compareTo(saldoPendiente) > 0) {
                    throw new PagoExcedeSaldoException(saldoPendiente);
                }

                try (PreparedStatement ps = conexion.prepareStatement(insertSql)) {
                    ps.setInt(1, pago.getPrestamo().getId());
                    ps.setDate(2, Date.valueOf(pago.getFechaPago()));
                    ps.setBigDecimal(3, pago.getMonto());
                    ps.executeUpdate();
                }

                BigDecimal nuevoSaldo = saldoPendiente.subtract(pago.getMonto());
                try (PreparedStatement ps = conexion.prepareStatement(estadoSql)) {
                    ps.setString(1, nuevoSaldo.signum() <= 0
                            ? EstadoPrestamo.PAGADO.name() : EstadoPrestamo.PENDIENTE.name());
                    ps.setInt(2, pago.getPrestamo().getId());
                    ps.executeUpdate();
                }

                conexion.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                rollback(conexion, e);
                throw e;
            }
        } catch (SQLException e) {
            throw new CrediYaException(
                    "No se pudo registrar el pago en la base de datos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Pago> listar() {
        List<Pago> pagos = new ArrayList<>();
        String sql = """
                SELECT id, prestamo_id, fecha_pago, monto
                FROM pagos
                ORDER BY fecha_pago DESC, id DESC
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                pagos.add(mapear(rs));
            }
            return pagos;
        } catch (SQLException e) {
            throw new CrediYaException("No se pudieron consultar los pagos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Pago> buscarPorPrestamo(int prestamoId) {
        List<Pago> pagos = new ArrayList<>();
        String sql = """
                SELECT id, prestamo_id, fecha_pago, monto
                FROM pagos
                WHERE prestamo_id = ?
                ORDER BY fecha_pago DESC, id DESC
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, prestamoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pagos.add(mapear(rs));
                }
            }
            return pagos;
        } catch (SQLException e) {
            throw new CrediYaException("No se pudo consultar el historial de pagos: "
                    + e.getMessage(), e);
        }
    }

    private Pago mapear(ResultSet rs) throws SQLException {
        Prestamo prestamo = new Prestamo(
                rs.getInt("prestamo_id"), null, null, BigDecimal.ZERO,
                BigDecimal.ZERO, 0, null, EstadoPrestamo.PENDIENTE);
        return new Pago(rs.getInt("id"), prestamo,
                rs.getDate("fecha_pago").toLocalDate(), rs.getBigDecimal("monto"));
    }

    @Override
    public BigDecimal totalPagado(int prestamoId) {
        String sql = "SELECT COALESCE(SUM(monto), 0) FROM pagos WHERE prestamo_id = ?";
        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, prestamoId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            throw new CrediYaException("No se pudo calcular el total pagado: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<Integer, BigDecimal> totalesPagadosPorPrestamo() {
        String sql = """
                SELECT prestamo_id, COALESCE(SUM(monto), 0) AS total_pagado
                FROM pagos
                GROUP BY prestamo_id
                """;
        Map<Integer, BigDecimal> totales = new HashMap<>();
        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                totales.put(rs.getInt("prestamo_id"), rs.getBigDecimal("total_pagado"));
            }
            return totales;
        } catch (SQLException e) {
            throw new CrediYaException("No se pudieron consultar los totales por préstamo: "
                    + e.getMessage(), e);
        }
    }

    @Override
    public BigDecimal calcularSaldoPendiente(int prestamoId) {
        String sql = """
                SELECT p.monto, p.interes, COALESCE(SUM(pg.monto), 0) AS total_pagado
                FROM prestamos p
                LEFT JOIN pagos pg ON p.id = pg.prestamo_id
                WHERE p.id = ?
                GROUP BY p.id
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, prestamoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return BigDecimal.ZERO;
                }
                BigDecimal total = Prestamo.calcularMontoTotal(
                        rs.getBigDecimal("monto"), rs.getBigDecimal("interes"));
                return total.subtract(rs.getBigDecimal("total_pagado")).max(BigDecimal.ZERO);
            }
        } catch (SQLException e) {
            throw new CrediYaException("No se pudo calcular el saldo pendiente: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(int id) {
        String buscarPrestamoSql = "SELECT prestamo_id FROM pagos WHERE id = ?";
        String lockSql = "SELECT id FROM prestamos WHERE id = ? FOR UPDATE";
        String deleteSql = "DELETE FROM pagos WHERE id = ?";
        String pagosSql = "SELECT COALESCE(SUM(monto), 0) FROM pagos WHERE prestamo_id = ?";
        String prestamoSql = "SELECT monto, interes FROM prestamos WHERE id = ?";
        String estadoSql = "UPDATE prestamos SET estado = ? WHERE id = ?";

        try (Connection conexion = ConexionDB.getConnection()) {
            conexion.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            conexion.setAutoCommit(false);
            try {
                int prestamoId;
                try (PreparedStatement ps = conexion.prepareStatement(buscarPrestamoSql)) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            conexion.rollback();
                            return false;
                        }
                        prestamoId = rs.getInt(1);
                    }
                }

                try (PreparedStatement ps = conexion.prepareStatement(lockSql)) {
                    ps.setInt(1, prestamoId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            conexion.rollback();
                            return false;
                        }
                    }
                }

                try (PreparedStatement ps = conexion.prepareStatement(deleteSql)) {
                    ps.setInt(1, id);
                    if (ps.executeUpdate() == 0) {
                        conexion.rollback();
                        return false;
                    }
                }

                BigDecimal totalPrestamo;
                try (PreparedStatement ps = conexion.prepareStatement(prestamoSql)) {
                    ps.setInt(1, prestamoId);
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        BigDecimal monto = rs.getBigDecimal("monto");
                        BigDecimal interes = rs.getBigDecimal("interes");
                        totalPrestamo = Prestamo.calcularMontoTotal(monto, interes);
                    }
                }

                BigDecimal totalPagado;
                try (PreparedStatement ps = conexion.prepareStatement(pagosSql)) {
                    ps.setInt(1, prestamoId);
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        totalPagado = rs.getBigDecimal(1);
                    }
                }

                try (PreparedStatement ps = conexion.prepareStatement(estadoSql)) {
                    ps.setString(1, totalPagado.compareTo(totalPrestamo) >= 0
                            ? EstadoPrestamo.PAGADO.name() : EstadoPrestamo.PENDIENTE.name());
                    ps.setInt(2, prestamoId);
                    ps.executeUpdate();
                }

                conexion.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                rollback(conexion, e);
                throw e;
            }
        } catch (SQLException e) {
            throw new CrediYaException("No se pudo eliminar el pago: " + e.getMessage(), e);
        }
    }

    private void rollback(Connection conexion, Exception causa) {
        try {
            conexion.rollback();
        } catch (SQLException e) {
            causa.addSuppressed(e);
        }
    }
}
