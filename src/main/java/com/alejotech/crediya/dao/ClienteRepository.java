package com.alejotech.crediya.dao;

import com.alejotech.crediya.modelo.Cliente;
import java.util.List;

public interface ClienteRepository {
    boolean guardar(Cliente cliente);
    List<Cliente> listar();
    Cliente buscarPorId(int id);
    boolean actualizar(Cliente cliente);
    boolean eliminar(int id);
}
