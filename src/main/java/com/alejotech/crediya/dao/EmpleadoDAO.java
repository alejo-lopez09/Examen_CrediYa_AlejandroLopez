package com.alejotech.crediya.dao;

import com.alejotech.crediya.Conexion.Conexion_DB;
import com.alejotech.crediya.excepciones.CrediYaException;
import com.alejotech.crediya.modelo.Empleado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAO implements EmpleadoRepository {


    public boolean guardar(Empleado empleado) {

        String sql = """
            INSERT INTO empleados
            (nombre, documento, rol, correo, salario)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conexion = Conexion_DB.getConnection();
         PreparedStatement ps = conexion.prepareStatement(
                 sql,
                 Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getDocumento());
            ps.setString(3, empleado.getRol());
            ps.setString(4, empleado.getCorreo());
            ps.setBigDecimal(5, empleado.getSalario());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {
                    empleado.setId(rs.getInt(1));
            }
        }

        return true;

    } catch (SQLException e) {
        throw new CrediYaException("No se pudo guardar el empleado: " + e.getMessage(), e);
    }
}


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
                        rs.getBigDecimal("salario")
                );

                empleados.add(empleado);
            }

        } catch (SQLException e) {
            throw new CrediYaException("No se pudieron consultar los empleados: " + e.getMessage(), e);
        }

        return empleados;
    }


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
                        rs.getBigDecimal("salario")
                );
            }

        } catch (SQLException e) {
            throw new CrediYaException("No se pudo consultar el empleado: " + e.getMessage(), e);
        }

        return null;
    }


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
            ps.setBigDecimal(5, empleado.getSalario());
            ps.setInt(6, empleado.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new CrediYaException("No se pudo actualizar el empleado: " + e.getMessage(), e);
        }
    }


    public boolean eliminar(int id) {

        String sql = "DELETE FROM empleados WHERE id = ?";

        try (Connection conexion = Conexion_DB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new CrediYaException("No se pudo eliminar el empleado: " + e.getMessage(), e);
        }
    }
}