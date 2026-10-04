package com.alejotech.crediya.modelo;

import java.time.LocalDate;

public class Prestamo {

    private int id;
    private Cliente cliente;
    private Empleado empleado;
    private double monto;
    private double interes;
    private int cuotas;
    private LocalDate fechaInicio;
    private String estado;

    // Constructor vacío
    public Prestamo() {
    }

    // Constructor completo
    public Prestamo(int id, Cliente cliente, Empleado empleado,
                    double monto, double interes, int cuotas,
                    LocalDate fechaInicio, String estado) {

        this.id = id;
        this.cliente = cliente;
        this.empleado = empleado;
        this.monto = monto;
        this.interes = interes;
        this.cuotas = cuotas;
        this.fechaInicio = fechaInicio;
        this.estado = estado;
    }

    // Constructor sin ID
    public Prestamo(Cliente cliente, Empleado empleado,
                    double monto, double interes, int cuotas,
                    LocalDate fechaInicio, String estado) {

        this.cliente = cliente;
        this.empleado = empleado;
        this.monto = monto;
        this.interes = interes;
        this.cuotas = cuotas;
        this.fechaInicio = fechaInicio;
        this.estado = estado;
    }

    // Calcular monto total con interés
    public double calcularMontoTotal() {
        return monto + (monto * interes / 100);
    }

    // Calcular cuota mensual
    public double calcularCuotaMensual() {

        if (cuotas <= 0) {
            return 0;
        }

        return calcularMontoTotal() / cuotas;
    }

    // Getters y setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public double getInteres() {
        return interes;
    }

    public void setInteres(double interes) {
        this.interes = interes;
    }

    public int getCuotas() {
        return cuotas;
    }

    public void setCuotas(int cuotas) {
        this.cuotas = cuotas;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Prestamo{" +
                "id=" + id +
                ", cliente=" + (cliente != null ? cliente.getNombre() : "Sin cliente") +
                ", empleado=" + (empleado != null ? empleado.getNombre() : "Sin empleado") +
                ", monto=" + monto +
                ", interes=" + interes +
                ", cuotas=" + cuotas +
                ", fechaInicio=" + fechaInicio +
                ", estado='" + estado + '\'' +
                '}';
    }
}