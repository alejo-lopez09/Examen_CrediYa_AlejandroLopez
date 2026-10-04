package com.alejotech.crediya.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class Prestamo {

    private int id;
    private Cliente cliente;
    private Empleado empleado;
    private BigDecimal monto;
    private BigDecimal interes;
    private int cuotas;
    private LocalDate fechaInicio;
    private String estado;


    public Prestamo(int id, Cliente cliente, Empleado empleado,
                    BigDecimal monto, BigDecimal interes, int cuotas,
                    LocalDate fechaInicio, String estado) {

        this.id = id;
        this.cliente = cliente;
        this.empleado = empleado;
        this.monto = monto == null ? BigDecimal.ZERO : monto;
        this.interes = interes == null ? BigDecimal.ZERO : interes;
        this.cuotas = cuotas;
        this.fechaInicio = fechaInicio;
        this.estado = estado;
    }

    public Prestamo(Cliente cliente, Empleado empleado,
                    BigDecimal monto, BigDecimal interes, int cuotas,
                    LocalDate fechaInicio, String estado) {

        this.cliente = cliente;
        this.empleado = empleado;
        this.monto = monto == null ? BigDecimal.ZERO : monto;
        this.interes = interes == null ? BigDecimal.ZERO : interes;
        this.cuotas = cuotas;
        this.fechaInicio = fechaInicio;
        this.estado = estado;
    }

    public BigDecimal calcularMontoTotal() {
        return monto.add(monto.multiply(interes)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
    }

    public BigDecimal calcularCuotaMensual() {

        if (cuotas <= 0) {
            return BigDecimal.ZERO;
        }

        return calcularMontoTotal().divide(
                BigDecimal.valueOf(cuotas), 2, RoundingMode.HALF_UP);
    }

    public boolean estaVencido() {
        return "PENDIENTE".equalsIgnoreCase(estado)
                && fechaInicio != null
                && cuotas > 0
                && fechaInicio.plusMonths(cuotas).isBefore(LocalDate.now());
    }

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

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public BigDecimal getInteres() {
        return interes;
    }

    public void setInteres(BigDecimal interes) {
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
