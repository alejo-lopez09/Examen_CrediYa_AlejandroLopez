package com.alejotech.crediya.service;

import com.alejotech.crediya.dao.ClienteDAO;
import com.alejotech.crediya.dao.ClienteRepository;
import com.alejotech.crediya.dao.PrestamoDAO;
import com.alejotech.crediya.dao.PrestamoRepository;
import com.alejotech.crediya.excepciones.RecursoNoEncontradoException;
import com.alejotech.crediya.excepciones.ValidacionException;
import com.alejotech.crediya.modelo.Cliente;
import com.alejotech.crediya.modelo.Prestamo;
import com.alejotech.crediya.util.ArchivoUtil;
import com.alejotech.crediya.util.Validaciones;

import java.util.List;

public class ClienteService {
    private static final String ARCHIVO = "clientes.txt";
    private final ClienteRepository clienteRepository;
    private final PrestamoRepository prestamoRepository;

    public ClienteService() {
        this(new ClienteDAO(), new PrestamoDAO());
    }

    public ClienteService(ClienteRepository clienteRepository, PrestamoRepository prestamoRepository) {
        this.clienteRepository = clienteRepository;
        this.prestamoRepository = prestamoRepository;
    }

    public void registrar(Cliente cliente) {
        validar(cliente);
        if (!clienteRepository.guardar(cliente)) {
            throw new ValidacionException("No se pudo registrar el cliente.");
        }
        sincronizarRespaldo();
    }

    public void sincronizarRespaldo() {
        ArchivoUtil.sincronizar(ARCHIVO, clienteRepository.listar().stream()
                .map(c -> ArchivoUtil.registro(c.getId(), c.getNombre(), c.getDocumento(),
                        c.getCorreo(), c.getTelefono()))
                .toList());
    }

    public List<Cliente> listar() {
        return clienteRepository.listar();
    }

    public Cliente buscarPorId(int id) {
        validarId(id);
        return clienteRepository.buscarPorId(id);
    }

    public List<Prestamo> consultarPrestamos(int clienteId) {
        validarId(clienteId);
        if (clienteRepository.buscarPorId(clienteId) == null) {
            throw new RecursoNoEncontradoException("El cliente no existe.");
        }
        return prestamoRepository.buscarPorCliente(clienteId);
    }

    public void actualizar(Cliente cliente) {
        validar(cliente);
        validarId(cliente.getId());
        if (!clienteRepository.actualizar(cliente)) {
            throw new RecursoNoEncontradoException("El cliente no existe o no se pudo actualizar.");
        }
        sincronizarRespaldo();
    }

    public void eliminar(int id) {
        validarId(id);
        if (!clienteRepository.eliminar(id)) {
            throw new RecursoNoEncontradoException("El cliente no existe.");
        }
        sincronizarRespaldo();
    }

    private void validar(Cliente cliente) {
        if (cliente == null) {
            throw new ValidacionException("El cliente es obligatorio.");
        }
        Validaciones.requerido(cliente.getNombre(), "El nombre");
        Validaciones.documento(cliente.getDocumento());
        Validaciones.correo(cliente.getCorreo());
        Validaciones.telefono(cliente.getTelefono());
    }

    private void validarId(int id) {
        if (id <= 0) {
            throw new ValidacionException("El ID del cliente debe ser positivo.");
        }
    }
}
