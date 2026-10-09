package com.alejotech.crediya.modelo;

import com.alejotech.crediya.excepciones.ValidacionException;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Pago {

    private int id;
    private int prestamoId;
    private Prestamo prestamo;
    private LocalDate fechaPago;
    private BigDecimal monto;

    public Pago() {
    }

    public Pago(int id, Prestamo prestamo, LocalDate fechaPago, BigDecimal monto) {
        this.id = id;
        asignarPrestamo(prestamo);
        setFechaPago(fechaPago);
        setMonto(monto);
    }

    public Pago(Prestamo prestamo, LocalDate fechaPago, BigDecimal monto) {
        asignarPrestamo(prestamo);
        setFechaPago(fechaPago);
        setMonto(monto);
    }

    public Pago(int prestamoId, LocalDate fechaPago, double monto) {
        setPrestamoId(prestamoId);
        setFechaPago(fechaPago);
        setMonto(monto);
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
        asignarPrestamo(prestamo);
    }

    public int getPrestamoId() {
        return prestamoId;
    }

    public void setPrestamoId(int prestamoId) {
        if (prestamoId <= 0) {
            throw new ValidacionException("El ID del préstamo debe ser positivo.");
        }
        this.prestamoId = prestamoId;
        if (prestamo == null) {
            prestamo = new Prestamo(prestamoId, null, null, BigDecimal.ZERO,
                    BigDecimal.ZERO, 0, null, EstadoPrestamo.PENDIENTE);
            return;
        }
        prestamo.setId(prestamoId);
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        if (fechaPago == null) {
            throw new ValidacionException("La fecha del pago no puede ser nula.");
        }
        this.fechaPago = fechaPago;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        if (monto == null) {
            throw new ValidacionException("El monto del pago es obligatorio.");
        }
        if (monto.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacionException("El monto del pago no puede ser negativo.");
        }
        this.monto = monto;
    }

    public void setMonto(double monto) {
        if (!Double.isFinite(monto)) {
            throw new ValidacionException("El monto del pago debe ser un número finito.");
        }
        setMonto(BigDecimal.valueOf(monto));
    }

    private void asignarPrestamo(Prestamo prestamo) {
        this.prestamo = prestamo;
        this.prestamoId = prestamo == null ? 0 : prestamo.getId();
    }

    @Override
    public String toString() {
        return "Pago{" +
                "id=" + id +
                ", prestamoId=" + (prestamo != null ? prestamo.getId() : "Sin préstamo") +
                ", fechaPago=" + fechaPago +
                ", monto=" + monto +
                '}';
    }
}
