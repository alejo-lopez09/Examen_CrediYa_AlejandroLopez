package com.alejotech.crediya.service;

import com.alejotech.crediya.dao.EmpleadoDAO;
import com.alejotech.crediya.dao.EmpleadoRepository;
import com.alejotech.crediya.excepciones.RecursoNoEncontradoException;
import com.alejotech.crediya.excepciones.ValidacionException;
import com.alejotech.crediya.modelo.Empleado;
import com.alejotech.crediya.util.ArchivoUtil;
import com.alejotech.crediya.util.Validaciones;

import java.util.List;

public class EmpleadoService {
    private static final String ARCHIVO = "empleados.txt";
    private final EmpleadoRepository empleadoRepository;

    public EmpleadoService() {
        this(new EmpleadoDAO());
    }

    public EmpleadoService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    public void registrar(Empleado empleado) {
        validar(empleado);
        if (!empleadoRepository.guardar(empleado)) {
            throw new ValidacionException("No se pudo registrar el empleado.");
        }
        sincronizarRespaldo();
    }

    public void sincronizarRespaldo() {
        ArchivoUtil.sincronizar(ARCHIVO, empleadoRepository.listar().stream()
                .map(e -> ArchivoUtil.registro(e.getId(), e.getNombre(), e.getDocumento(),
                        e.getRol(), e.getCorreo(), e.getSalario()))
                .toList());
    }

    public List<Empleado> listar() {
        return empleadoRepository.listar();
    }

    public Empleado buscarPorId(int id) {
        validarId(id);
        return empleadoRepository.buscarPorId(id);
    }

    public void actualizar(Empleado empleado) {
        validar(empleado);
        validarId(empleado.getId());
        if (!empleadoRepository.actualizar(empleado)) {
            throw new RecursoNoEncontradoException("El empleado no existe o no se pudo actualizar.");
        }
        sincronizarRespaldo();
    }

    public void eliminar(int id) {
        validarId(id);
        if (!empleadoRepository.eliminar(id)) {
            throw new RecursoNoEncontradoException("El empleado no existe.");
        }
        sincronizarRespaldo();
    }

    private void validar(Empleado empleado) {
        if (empleado == null) {
            throw new ValidacionException("El empleado es obligatorio.");
        }
        empleado.setNombre(Validaciones.texto(empleado.getNombre(), "El nombre", 80));
        empleado.setDocumento(Validaciones.texto(empleado.getDocumento(), "El documento", 30));
        Validaciones.documento(empleado.getDocumento());
        empleado.setRol(Validaciones.texto(empleado.getRol(), "El rol", 30));
        empleado.setCorreo(Validaciones.texto(empleado.getCorreo(), "El correo", 80));
        Validaciones.correo(empleado.getCorreo());
        Validaciones.positivo(empleado.getSalario(), "El salario");
        Validaciones.maximoDigitosEnteros(empleado.getSalario(), 8, "El salario");
    }

    private void validarId(int id) {
        if (id <= 0) {
            throw new ValidacionException("El ID del empleado debe ser positivo.");
        }
    }
}
