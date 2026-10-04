package com.alejotech.crediya.dao;

import com.alejotech.crediya.Conexion.Conexion_DB;
import com.alejotech.crediya.modelo.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    // Registrar cliente
    public boolean guardar(Cliente cliente) {

        String sql = """
                INSERT INTO clientes
                (nombre, documento, correo, telefono)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conexion = Conexion_DB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getDocumento());
            ps.setString(3, cliente.getCorreo());
            ps.setString(4, cliente.getTelefono());

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {

            System.out.println("Error al guardar cliente: "
                    + e.getMessage());

            return false;
        }
    }

    // Listar clientes
    public List<Cliente> listar() {

        List<Cliente> clientes = new ArrayList<>();

        String sql = "SELECT * FROM clientes";

        try (Connection conexion = Conexion_DB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Cliente cliente = new Cliente(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("documento"),
                        rs.getString("correo"),
                        rs.getString("telefono")
                );

                clientes.add(cliente);
            }

        } catch (SQLException e) {

            System.out.println("Error al listar clientes: "
                    + e.getMessage());
        }

        return clientes;
    }

    // Buscar cliente por ID
    public Cliente buscarPorId(int id) {

        String sql = "SELECT * FROM clientes WHERE id = ?";

        try (Connection conexion = Conexion_DB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new Cliente(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("documento"),
                        rs.getString("correo"),
                        rs.getString("telefono")
                );
            }

        } catch (SQLException e) {

            System.out.println("Error al buscar cliente: "
                    + e.getMessage());
        }

        return null;
    }

    // Actualizar cliente
    public boolean actualizar(Cliente cliente) {

        String sql = """
                UPDATE clientes
                SET nombre = ?,
                    documento = ?,
                    correo = ?,
                    telefono = ?
                WHERE id = ?
                """;

        try (Connection conexion = Conexion_DB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getDocumento());
            ps.setString(3, cliente.getCorreo());
            ps.setString(4, cliente.getTelefono());
            ps.setInt(5, cliente.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println("Error al actualizar cliente: "
                    + e.getMessage());

            return false;
        }
    }

    // Eliminar cliente
    public boolean eliminar(int id) {

        String sql = "DELETE FROM clientes WHERE id = ?";

        try (Connection conexion = Conexion_DB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println("Error al eliminar cliente: "
                    + e.getMessage());

            return false;
        }
    }
}