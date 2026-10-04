package com.alejotech.crediya.dao;

import com.alejotech.crediya.modelo.Prestamo;
import java.util.List;

public interface PrestamoRepository {
    boolean guardar(Prestamo prestamo);
    List<Prestamo> listar();
    Prestamo buscarPorId(int id);
    boolean cambiarEstado(int id, String estado);
    boolean eliminar(int id);
    List<Prestamo> buscarPorCliente(int clienteId);
}
