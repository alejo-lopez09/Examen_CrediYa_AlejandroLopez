package com.alejotech.crediya.dao;

import com.alejotech.crediya.modelo.Empleado;
import java.util.List;

public interface EmpleadoRepository {
    boolean guardar(Empleado empleado);
    List<Empleado> listar();
    Empleado buscarPorId(int id);
    boolean actualizar(Empleado empleado);
    boolean eliminar(int id);
}
