package com.alejotech.crediya.modelo;



import java.time.LocalDate;

public class Pago {

    private int id;
    private Prestamo prestamo;
    private LocalDate fechaPago;
    private double monto;

    // Constructor vacío
    public Pago() {
    }

    // Constructor completo
    public Pago(int id, Prestamo prestamo,
                LocalDate fechaPago, double monto) {

        this.id = id;
        this.prestamo = prestamo;
        this.fechaPago = fechaPago;
        this.monto = monto;
    }

    // Constructor sin ID
    public Pago(Prestamo prestamo,
                LocalDate fechaPago,
                double monto) {

        this.prestamo = prestamo;
        this.fechaPago = fechaPago;
        this.monto = monto;
    }

    // Getters y setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Prestamo getPrestamo() {
        return prestamo;
    }

    public void setPrestamo(Prestamo prestamo) {
        this.prestamo = prestamo;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    @Override
    public String toString() {
        return "Pago{" +
                "id=" + id +
                ", prestamo=" +
                (prestamo != null ? prestamo.getId() : "Sin préstamo") +
                ", fechaPago=" + fechaPago +
                ", monto=" + monto +
                '}';
    }
}
