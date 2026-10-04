package com.alejotech.crediya.modelo;

import java.math.BigDecimal;

public class Empleado extends Persona {

    private String rol;
    private BigDecimal salario;

    public Empleado(int id, String nombre, String documento, String correo, String rol, BigDecimal salario) {
        super(id, nombre, documento, correo);
        this.rol = rol;
        this.salario = salario == null ? BigDecimal.ZERO : salario;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public void setSalario(BigDecimal salario) {
        this.salario = salario;
    }

    @Override
    public String getTipo() {
        return "Empleado";
    }
}