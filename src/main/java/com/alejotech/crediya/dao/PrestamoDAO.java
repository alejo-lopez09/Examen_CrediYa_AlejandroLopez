package com.alejotech.crediya.dao;

import com.alejotech.crediya.Conexion.Conexion_DB;
import com.alejotech.crediya.modelo.Cliente;
import com.alejotech.crediya.modelo.Empleado;
import com.alejotech.crediya.modelo.Prestamo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {

    // Registrar préstamo
    public boolean guardar(Prestamo prestamo) {

        String sql = """
                INSERT INTO prestamos
                (cliente_id, empleado_id, monto, interes,
                 cuotas, fecha_inicio, estado)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conexion = Conexion_DB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, prestamo.getCliente().getId());
            ps.setInt(2, prestamo.getEmpleado().getId());
            ps.setDouble(3, prestamo.getMonto());
            ps.setDouble(4, prestamo.getInteres());
            ps.setInt(5, prestamo.getCuotas());

            ps.setDate(
                    6,
                    Date.valueOf(prestamo.getFechaInicio())
            );

            ps.setString(7, prestamo.getEstado());

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {

            System.out.println("Error al guardar préstamo: "
                    + e.getMessage());

            return false;
        }
    }

    // Listar todos los préstamos
    public List<Prestamo> listar() {

        List<Prestamo> prestamos = new ArrayList<>();

        String sql = """
                SELECT
                    p.*,

                    c.nombre AS cliente_nombre,
                    c.documento AS cliente_documento,
                    c.correo AS cliente_correo,
                    c.telefono AS cliente_telefono,

                    e.nombre AS empleado_nombre,
                    e.documento AS empleado_documento,
                    e.correo AS empleado_correo,
                    e.rol AS empleado_rol,
                    e.salario AS empleado_salario

                FROM prestamos p

                INNER JOIN clientes c
                    ON p.cliente_id = c.id

                INNER JOIN empleados e
                    ON p.empleado_id = e.id
                """;

        try (Connection conexion = Conexion_DB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Cliente cliente = new Cliente(
                        rs.getInt("cliente_id"),
                        rs.getString("cliente_nombre"),
                        rs.getString("cliente_documento"),
                        rs.getString("cliente_correo"),
                        rs.getString("cliente_telefono")
                );

                Empleado empleado = new Empleado(
                        rs.getInt("empleado_id"),
                        rs.getString("empleado_nombre"),
                        rs.getString("empleado_documento"),
                        rs.getString("empleado_correo"),
                        rs.getString("empleado_rol"),
                        rs.getDouble("empleado_salario")
                );

                Prestamo prestamo = new Prestamo(
                        rs.getInt("id"),
                        cliente,
                        empleado,
                        rs.getDouble("monto"),
                        rs.getDouble("interes"),
                        rs.getInt("cuotas"),
                        rs.getDate("fecha_inicio").toLocalDate(),
                        rs.getString("estado")
                );

                prestamos.add(prestamo);
            }

        } catch (SQLException e) {

            System.out.println("Error al listar préstamos: "
                    + e.getMessage());
        }

        return prestamos;
    }

    // Buscar préstamo por ID
    public Prestamo buscarPorId(int id) {

        String sql = """
                SELECT
                    p.*,

                    c.nombre AS cliente_nombre,
                    c.documento AS cliente_documento,
                    c.correo AS cliente_correo,
                    c.telefono AS cliente_telefono,

                    e.nombre AS empleado_nombre,
                    e.documento AS empleado_documento,
                    e.correo AS empleado_correo,
                    e.rol AS empleado_rol,
                    e.salario AS empleado_salario

                FROM prestamos p

                INNER JOIN clientes c
                    ON p.cliente_id = c.id

                INNER JOIN empleados e
                    ON p.empleado_id = e.id

                WHERE p.id = ?
                """;

        try (Connection conexion = Conexion_DB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Cliente cliente = new Cliente(
                        rs.getInt("cliente_id"),
                        rs.getString("cliente_nombre"),
                        rs.getString("cliente_documento"),
                        rs.getString("cliente_correo"),
                        rs.getString("cliente_telefono")
                );

                Empleado empleado = new Empleado(
                        rs.getInt("empleado_id"),
                        rs.getString("empleado_nombre"),
                        rs.getString("empleado_documento"),
                        rs.getString("empleado_correo"),
                        rs.getString("empleado_rol"),
                        rs.getDouble("empleado_salario")
                );

                return new Prestamo(
                        rs.getInt("id"),
                        cliente,
                        empleado,
                        rs.getDouble("monto"),
                        rs.getDouble("interes"),
                        rs.getInt("cuotas"),
                        rs.getDate("fecha_inicio").toLocalDate(),
                        rs.getString("estado")
                );
            }

        } catch (SQLException e) {

            System.out.println("Error al buscar préstamo: "
                    + e.getMessage());
        }

        return null;
    }

    // Cambiar estado del préstamo
    public boolean cambiarEstado(int id, String estado) {

        String sql = """
                UPDATE prestamos
                SET estado = ?
                WHERE id = ?
                """;

        try (Connection conexion = Conexion_DB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, estado);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println("Error al cambiar estado: "
                    + e.getMessage());

            return false;
        }
    }

    // Eliminar préstamo
    public boolean eliminar(int id) {

        String sql = "DELETE FROM prestamos WHERE id = ?";

        try (Connection conexion = Conexion_DB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println("Error al eliminar préstamo: "
                    + e.getMessage());

            return false;
        }
    }

    // Buscar préstamos de un cliente
    public List<Prestamo> buscarPorCliente(int clienteId) {

        List<Prestamo> prestamos = new ArrayList<>();

        String sql = """
                SELECT
                    p.*,

                    c.nombre AS cliente_nombre,
                    c.documento AS cliente_documento,
                    c.correo AS cliente_correo,
                    c.telefono AS cliente_telefono,

                    e.nombre AS empleado_nombre,
                    e.documento AS empleado_documento,
                    e.correo AS empleado_correo,
                    e.rol AS empleado_rol,
                    e.salario AS empleado_salario

                FROM prestamos p

                INNER JOIN clientes c
                    ON p.cliente_id = c.id

                INNER JOIN empleados e
                    ON p.empleado_id = e.id

                WHERE p.cliente_id = ?
                """;

        try (Connection conexion = Conexion_DB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, clienteId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Cliente cliente = new Cliente(
                        rs.getInt("cliente_id"),
                        rs.getString("cliente_nombre"),
                        rs.getString("cliente_documento"),
                        rs.getString("cliente_correo"),
                        rs.getString("cliente_telefono")
                );

                Empleado empleado = new Empleado(
                        rs.getInt("empleado_id"),
                        rs.getString("empleado_nombre"),
                        rs.getString("empleado_documento"),
                        rs.getString("empleado_correo"),
                        rs.getString("empleado_rol"),
                        rs.getDouble("empleado_salario")
                );

                Prestamo prestamo = new Prestamo(
                        rs.getInt("id"),
                        cliente,
                        empleado,
                        rs.getDouble("monto"),
                        rs.getDouble("interes"),
                        rs.getInt("cuotas"),
                        rs.getDate("fecha_inicio").toLocalDate(),
                        rs.getString("estado")
                );

                prestamos.add(prestamo);
            }

        } catch (SQLException e) {

            System.out.println("Error al buscar préstamos del cliente: "
                    + e.getMessage());
        }

        return prestamos;
    }
}