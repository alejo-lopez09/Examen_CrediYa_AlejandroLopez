package com.alejotech.crediya.dao;

import com.alejotech.crediya.Conexion.Conexion_DB;
import com.alejotech.crediya.modelo.Empleado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAO {

    // Registrar empleado
    public boolean guardar(Empleado empleado) {

        String sql = """
                INSERT INTO empleados
                (nombre, documento, rol, correo, salario)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conexion = Conexion_DB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getDocumento());
            ps.setString(3, empleado.getRol());
            ps.setString(4, empleado.getCorreo());
            ps.setDouble(5, empleado.getSalario());

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {

            System.out.println("Error al guardar empleado: "
                    + e.getMessage());

            return false;
        }
    }

    // Listar empleados
    public List<Empleado> listar() {

        List<Empleado> empleados = new ArrayList<>();

        String sql = "SELECT * FROM empleados";

        try (Connection conexion = Conexion_DB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Empleado empleado = new Empleado(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("documento"),
                        rs.getString("correo"),
                        rs.getString("rol"),
                        rs.getDouble("salario")
                );

                empleados.add(empleado);
            }

        } catch (SQLException e) {

            System.out.println("Error al listar empleados: "
                    + e.getMessage());
        }

        return empleados;
    }

    // Buscar empleado por ID
    public Empleado buscarPorId(int id) {

        String sql = "SELECT * FROM empleados WHERE id = ?";

        try (Connection conexion = Conexion_DB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new Empleado(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("documento"),
                        rs.getString("correo"),
                        rs.getString("rol"),
                        rs.getDouble("salario")
                );
            }

        } catch (SQLException e) {

            System.out.println("Error al buscar empleado: "
                    + e.getMessage());
        }

        return null;
    }

    // Actualizar empleado
    public boolean actualizar(Empleado empleado) {

        String sql = """
                UPDATE empleados
                SET nombre = ?,
                    documento = ?,
                    rol = ?,
                    correo = ?,
                    salario = ?
                WHERE id = ?
                """;

        try (Connection conexion = Conexion_DB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getDocumento());
            ps.setString(3, empleado.getRol());
            ps.setString(4, empleado.getCorreo());
            ps.setDouble(5, empleado.getSalario());
            ps.setInt(6, empleado.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println("Error al actualizar empleado: "
                    + e.getMessage());

            return false;
        }
    }

    // Eliminar empleado
    public boolean eliminar(int id) {

        String sql = "DELETE FROM empleados WHERE id = ?";

        try (Connection conexion = Conexion_DB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println("Error al eliminar empleado: "
                    + e.getMessage());

            return false;
        }
    }
}