package com.alejotech.crediya.vista;

import com.alejotech.crediya.excepciones.CrediYaException;
import com.alejotech.crediya.excepciones.PagoExcedeSaldoException;
import com.alejotech.crediya.excepciones.RecursoNoEncontradoException;
import com.alejotech.crediya.excepciones.ValidacionException;
import com.alejotech.crediya.modelo.Pago;
import com.alejotech.crediya.modelo.Prestamo;
import com.alejotech.crediya.service.GestorPagos;
import com.alejotech.crediya.service.PagoService;
import com.alejotech.crediya.service.PrestamoService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class MenuPagos {

    private final Scanner scanner;
    private final PagoService pagoService;
    private final PrestamoService prestamoService;
    private final GestorPagos gestorPagos = new GestorPagos();

    public MenuPagos(
            Scanner scanner,
            PagoService pagoService,
            PrestamoService prestamoService) {

        this.scanner = scanner;
        this.pagoService = pagoService;
        this.prestamoService = prestamoService;
    }

    public void mostrar() {

        int opcion;

        do {

            System.out.println("\n=================================");
            System.out.println("          GESTIÓN DE PAGOS");
            System.out.println("=================================");
            System.out.println("1. Registrar pago");
            System.out.println("2. Listar pagos");
            System.out.println("3. Pagos mayores a un monto");
            System.out.println("4. Historial de pagos");
            System.out.println("5. Ver total pagado");
            System.out.println("6. Ver saldo pendiente");
            System.out.println("7. Eliminar pago");
            System.out.println("8. Volver");
            System.out.println("=================================");

            opcion = leerEntero("Seleccione una opción: ");

            try {

                switch (opcion) {

                    case 1:
                        registrar();
                        break;

                    case 2:
                        listar();
                        break;

                    case 3:
                        filtrarMayores();
                        break;

                    case 4:
                        historial();
                        break;

                    case 5:
                        totalPagado();
                        break;

                    case 6:
                        saldoPendiente();
                        break;

                    case 7:
                        eliminar();
                        break;

                    case 8:
                        break;

                    default:
                        System.out.println("Opción inválida.");
                }

            } catch (CrediYaException e) {
                System.out.println("Error: " + e.getMessage());
            }

        } while (opcion != 8);
    }

    private void registrar() {

        int prestamoId = leerEntero("ID del préstamo: ");
        if (prestamoId <= 0) {
            System.out.println("Error: El ID del préstamo debe ser positivo.");
            return;
        }

        BigDecimal monto = leerMonto("Monto del pago: ");
        if (monto == null) {
            return;
        }
        if (monto.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("Error: El monto del pago no puede ser negativo.");
            return;
        }
        if (monto.signum() == 0) {
            System.out.println("Error: El monto del pago debe ser mayor que cero.");
            return;
        }

        LocalDate fecha = leerFecha("Fecha del pago (aaaa-mm-dd): ");
        if (fecha == null) {
            return;
        }

        Pago pago = crearPago(prestamoId, fecha, monto);
        gestorPagos.registrarPago(pago);
        try {
            pagoService.registrar(pago);
        } catch (ValidacionException | PagoExcedeSaldoException | RecursoNoEncontradoException e) {
            gestorPagos.descartar(pago);
            System.out.println("Error: " + e.getMessage());
            System.out.println("El pago no se guardó en la base de datos.");
            return;
        } catch (CrediYaException e) {
            System.out.println("El pago no se guardó en la base de datos: " + e.getMessage());
            System.out.println("Quedó registrado solo en memoria.");
            return;
        }

        try {
            gestorPagos.reemplazar(pagoService.listar());
        } catch (CrediYaException e) {
            System.out.println("El pago se guardó en la base de datos, pero no se pudo actualizar el listado: "
                    + e.getMessage());
            return;
        }
        System.out.println("Pago registrado y guardado en la base de datos.");
    }

    private Pago crearPago(int prestamoId, LocalDate fecha, BigDecimal monto) {
        try {
            Prestamo prestamo = prestamoService.buscarPorId(prestamoId);
            if (prestamo == null) {
                throw new RecursoNoEncontradoException("El préstamo no existe.");
            }
            return new Pago(prestamo, fecha, monto);
        } catch (ValidacionException | RecursoNoEncontradoException e) {
            throw e;
        } catch (CrediYaException e) {
            System.out.println("No se pudo verificar el préstamo en MySQL: " + e.getMessage());
            return new Pago(prestamoId, fecha, monto.doubleValue());
        }
    }

    private void listar() {
        prepararListado();
        gestorPagos.listarPagos();
    }

    private void filtrarMayores() {
        System.out.print("Mostrar pagos con monto mayor a: ");
        String texto = scanner.nextLine().trim();
        double valor;
        try {
            valor = Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            System.out.println("Error: Ingrese un valor numérico válido.");
            return;
        }
        if (!Double.isFinite(valor) || valor < 0) {
            System.out.println("Error: El valor de comparación debe ser finito y no puede ser negativo.");
            return;
        }
        prepararListado();
        gestorPagos.mostrarPagosMayoresA(valor);
    }

    private void prepararListado() {
        try {
            gestorPagos.reemplazar(pagoService.listar());
        } catch (CrediYaException e) {
            System.out.println("No se pudo consultar MySQL: " + e.getMessage());
            System.out.println("Se usan los pagos disponibles en memoria.");
        }
    }

    private void historial() throws CrediYaException {

        int prestamoId =
                leerEntero("ID del préstamo: ");

        List<Pago> pagos =
                pagoService.historial(prestamoId);

        if (pagos.isEmpty()) {
            System.out.println(
                    "No existen pagos para este préstamo."
            );
            return;
        }

        pagos.forEach(System.out::println);
    }

    private void totalPagado() throws CrediYaException {

        int prestamoId =
                leerEntero("ID del préstamo: ");

        BigDecimal total =
                pagoService.totalPagado(prestamoId);

        System.out.println("Total pagado: " + total);
    }

    private void saldoPendiente() throws CrediYaException {

        int prestamoId =
                leerEntero("ID del préstamo: ");

        BigDecimal saldo =
                pagoService.saldoPendiente(prestamoId);

        System.out.println("Saldo pendiente: " + saldo);
    }

    private void eliminar() throws CrediYaException {

        int id =
                leerEntero("ID del pago: ");

        pagoService.eliminar(id);
        gestorPagos.descartarPorId(id);

        System.out.println("Pago eliminado.");
    }

    private int leerEntero(String mensaje) {

        while (true) {

            try {
                System.out.print(mensaje);
                return Integer.parseInt(scanner.nextLine().trim());

            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido.");
            }
        }
    }

    private BigDecimal leerMonto(String mensaje) {
        System.out.print(mensaje);
        String texto = scanner.nextLine().trim();
        try {
            return new BigDecimal(texto);
        } catch (NumberFormatException e) {
            System.out.println("Error: Ingrese un monto numérico válido.");
            return null;
        }
    }

    private LocalDate leerFecha(String mensaje) {
        System.out.print(mensaje);
        String texto = scanner.nextLine().trim();
        if (texto.isEmpty()) {
            System.out.println("Error: La fecha del pago no puede ser nula.");
            return null;
        }
        try {
            return LocalDate.parse(texto);
        } catch (DateTimeParseException e) {
            System.out.println("Error: La fecha debe tener el formato aaaa-mm-dd.");
            return null;
        }
    }
}
