package com.alejotech.crediya.modelo;



import java.math.BigDecimal;
import java.time.LocalDate;

public class Pago {

    private int id;
    private Prestamo prestamo;
    private LocalDate fechaPago;
    private BigDecimal monto;

    // Constructor completo
    public Pago(int id, Prestamo prestamo,
                LocalDate fechaPago, BigDecimal monto) {

        this.id = id;
        this.prestamo = prestamo;
        this.fechaPago = fechaPago;
        this.monto = monto;
    }

    // Constructor sin ID
    public Pago(Prestamo prestamo,
                LocalDate fechaPago,
                BigDecimal monto) {

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

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
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
