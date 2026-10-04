package com.alejotech.crediya.modelo;

public class Empleado extends Persona {

    private String rol;
    private double salario;

    public Empleado(int id, String nombre, String documento, String correo, String rol, double salario) {
        super(id, nombre, documento, correo);
        this.rol = rol;
        this.salario = salario;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    @Override
    public String getTipo() {
        return "Empleado";
    }
}