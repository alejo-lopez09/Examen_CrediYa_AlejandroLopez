package com.alejotech.crediya.service;

import com.alejotech.crediya.dao.EmpleadoDAO;
import com.alejotech.crediya.dao.EmpleadoRepository;
import com.alejotech.crediya.modelo.Empleado;
import com.alejotech.crediya.util.ArchivoUtil;
import com.alejotech.crediya.util.Validaciones;
import java.util.List;

public class EmpleadoService {

    private static final String ARCHIVO = "empleados.txt";
    private final EmpleadoRepository empleadoDAO;

    public EmpleadoService() {
        this(new EmpleadoDAO());
    }

    public EmpleadoService(EmpleadoRepository empleadoDAO) {
        this.empleadoDAO = empleadoDAO;
    }

    public boolean registrar(Empleado empleado) {
        if (empleado == null) {
            System.out.println("El empleado no puede ser null.");
            return false;
        }
        if (!datosValidos(empleado)) {
            return false;
        }
        if (!empleadoDAO.guardar(empleado)) {
            return false;
        }
        sincronizarRespaldo();
        return true;
    }

    private boolean datosValidos(Empleado empleado) {
        try {
            Validaciones.requerido(empleado.getNombre(), "El nombre");
            Validaciones.documento(empleado.getDocumento());
            Validaciones.requerido(empleado.getRol(), "El rol");
            Validaciones.correo(empleado.getCorreo());
            Validaciones.positivo(empleado.getSalario(), "El salario");
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void sincronizarRespaldo() {
        ArchivoUtil.sincronizar(ARCHIVO, empleadoDAO.listar().stream()
                .map(e -> ArchivoUtil.registro(e.getId(), e.getNombre(), e.getDocumento(),
                        e.getRol(), e.getCorreo(), e.getSalario()))
                .toList());
    }

    public List<Empleado> listar() {
        return empleadoDAO.listar();
    }

    public Empleado buscarPorId(int id) {
        if (id <= 0) {
            return null;
        }
        return empleadoDAO.buscarPorId(id);
    }

    public boolean actualizar(Empleado empleado) {
        if (empleado == null || empleado.getId() <= 0 || !datosValidos(empleado)) {
            return false;
        }
        if (!empleadoDAO.actualizar(empleado)) {
            return false;
        }
        sincronizarRespaldo();
        return true;
    }

    public boolean eliminar(int id) {
        if (id <= 0) {
            return false;
        }
        if (!empleadoDAO.eliminar(id)) {
            return false;
        }
        sincronizarRespaldo();
        return true;
    }
}