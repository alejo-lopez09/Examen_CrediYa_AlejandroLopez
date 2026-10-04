package com.alejotech.crediya.service;

import com.alejotech.crediya.dao.ClienteDAO;
import com.alejotech.crediya.dao.ClienteRepository;
import com.alejotech.crediya.dao.PrestamoDAO;
import com.alejotech.crediya.dao.PrestamoRepository;
import com.alejotech.crediya.modelo.Cliente;
import com.alejotech.crediya.modelo.Prestamo;
import com.alejotech.crediya.util.ArchivoUtil;
import com.alejotech.crediya.util.Validaciones;

import java.util.List;

public class ClienteService {

    private static final String ARCHIVO = "clientes.txt";
    private final ClienteRepository clienteDAO;
    private final PrestamoRepository prestamoDAO;

    public ClienteService() {
        this(new ClienteDAO(), new PrestamoDAO());
    }

    public ClienteService(ClienteRepository clienteDAO, PrestamoRepository prestamoDAO) {
        this.clienteDAO = clienteDAO;
        this.prestamoDAO = prestamoDAO;
    }

    // Registrar cliente
    public boolean registrar(Cliente cliente) {

        if (cliente == null) {
            System.out.println("El cliente no puede ser null.");
            return false;
        }

        if (!datosValidos(cliente)) {
            return false;
        }
        if (!clienteDAO.guardar(cliente)) {
            return false;
        }
        sincronizarRespaldo();
        return true;
    }

    private boolean datosValidos(Cliente cliente) {
        try {
            Validaciones.requerido(cliente.getNombre(), "El nombre");
            Validaciones.documento(cliente.getDocumento());
            Validaciones.correo(cliente.getCorreo());
            Validaciones.telefono(cliente.getTelefono());
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void sincronizarRespaldo() {
        ArchivoUtil.sincronizar(ARCHIVO, clienteDAO.listar().stream()
                .map(c -> ArchivoUtil.registro(c.getId(), c.getNombre(), c.getDocumento(),
                        c.getCorreo(), c.getTelefono()))
                .toList());
    }

    // Listar clientes
    public List<Cliente> listar() {
        return clienteDAO.listar();
    }

    // Buscar cliente
    public Cliente buscarPorId(int id) {

        if (id <= 0) {
            return null;
        }

        return clienteDAO.buscarPorId(id);
    }

    // Consultar préstamos de un cliente
    public List<Prestamo> consultarPrestamos(int clienteId) {

        if (clienteId <= 0) {
            return List.of();
        }

        return prestamoDAO.buscarPorCliente(clienteId);
    }

    // Actualizar cliente
    public boolean actualizar(Cliente cliente) {

        if (cliente == null || cliente.getId() <= 0 || !datosValidos(cliente)) {
            return false;
        }

        if (!clienteDAO.actualizar(cliente)) {
            return false;
        }
        sincronizarRespaldo();
        return true;
    }

    // Eliminar cliente
    public boolean eliminar(int id) {

        if (id <= 0) {
            return false;
        }

        if (!clienteDAO.eliminar(id)) {
            return false;
        }
        sincronizarRespaldo();
        return true;
    }
}