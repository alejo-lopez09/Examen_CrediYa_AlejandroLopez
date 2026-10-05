package com.alejotech.crediya.excepciones;

import java.math.BigDecimal;

public class PagoExcedeSaldoException extends CrediYaException {
    private static final long serialVersionUID = 1L;
    private final BigDecimal saldoPendiente;

    public PagoExcedeSaldoException(BigDecimal saldoPendiente) {
        super("El pago supera el saldo pendiente de $" + saldoPendiente + ".");
        this.saldoPendiente = saldoPendiente;
    }

    public BigDecimal getSaldoPendiente() {
        return saldoPendiente;
    }
}
