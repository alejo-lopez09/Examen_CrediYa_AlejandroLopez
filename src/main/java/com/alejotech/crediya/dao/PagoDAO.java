package com.alejotech.crediya.dao;

import com.alejotech.crediya.Conexion.Conexion_DB;
import com.alejotech.crediya.modelo.Pago;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PagoDAO {

    // Registrar pago
    public boolean guardar(Pago pago) {

        String sql = """
                INSERT INTO pagos
                (prestamo_id, fecha_pago, monto)
                VALUES (?, ?, ?)
                """;

        try (Connection conexion = Conexion_DB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, pago.getPrestamo().getId());

            ps.setDate(
                    2,
                    Date.valueOf(pago.getFechaPago())
            );

            ps.setDouble(3, pago.getMonto());

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {

            System.out.println("Error al guardar pago: "
                    + e.getMessage());

            return false;
        }
    }

    // Listar todos los pagos
    public List<Pago> listar() {

        List<Pago> pagos = new ArrayList<>();

        String sql = """
                SELECT
                    p.id,
                    p.prestamo_id,
                    p.fecha_pago,
                    p.monto

                FROM pagos p

                ORDER BY p.fecha_pago DESC
                """;

        try (Connection conexion = Conexion_DB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Pago pago = new Pago();

                pago.setId(rs.getInt("id"));
                pago.setFechaPago(
                        rs.getDate("fecha_pago").toLocalDate()
                );
                pago.setMonto(rs.getDouble("monto"));

                pagos.add(pago);
            }

        } catch (SQLException e) {

            System.out.println("Error al listar pagos: "
                    + e.getMessage());
        }

        return pagos;
    }

    // Buscar pagos de un préstamo
    public List<Pago> buscarPorPrestamo(int prestamoId) {

        List<Pago> pagos = new ArrayList<>();

        String sql = """
                SELECT
                    id,
                    prestamo_id,
                    fecha_pago,
                    monto

                FROM pagos

                WHERE prestamo_id = ?

                ORDER BY fecha_pago DESC
                """;

        try (Connection conexion = Conexion_DB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, prestamoId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Pago pago = new Pago();

                pago.setId(rs.getInt("id"));
                pago.setFechaPago(
                        rs.getDate("fecha_pago").toLocalDate()
                );
                pago.setMonto(rs.getDouble("monto"));

                pagos.add(pago);
            }

        } catch (SQLException e) {

            System.out.println("Error al buscar pagos: "
                    + e.getMessage());
        }

        return pagos;
    }

    // Calcular cuánto se ha pagado de un préstamo
    public double totalPagado(int prestamoId) {

        String sql = """
                SELECT COALESCE(SUM(monto), 0)
                FROM pagos
                WHERE prestamo_id = ?
                """;

        try (Connection conexion = Conexion_DB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, prestamoId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (SQLException e) {

            System.out.println("Error al calcular total pagado: "
                    + e.getMessage());
        }

        return 0;
    }

    // Calcular saldo pendiente
    public double calcularSaldoPendiente(int prestamoId) {

        String sql = """
                SELECT
                    p.monto + (p.monto * p.interes / 100)
                    - COALESCE(SUM(pg.monto), 0)

                FROM prestamos p

                LEFT JOIN pagos pg
                    ON p.id = pg.prestamo_id

                WHERE p.id = ?

                GROUP BY p.id
                """;

        try (Connection conexion = Conexion_DB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, prestamoId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Math.max(0, rs.getDouble(1));
            }

        } catch (SQLException e) {

            System.out.println("Error al calcular saldo: "
                    + e.getMessage());
        }

        return 0;
    }

    // Eliminar pago
    public boolean eliminar(int id) {

        String sql = "DELETE FROM pagos WHERE id = ?";

        try (Connection conexion = Conexion_DB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println("Error al eliminar pago: "
                    + e.getMessage());

            return false;
        }
    }
}