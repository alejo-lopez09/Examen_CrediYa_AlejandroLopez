package com.alejotech.crediya.vista;

import com.alejotech.crediya.excepciones.CrediYaException;
import com.alejotech.crediya.modelo.Pago;
import com.alejotech.crediya.modelo.Prestamo;
import com.alejotech.crediya.service.PagoService;
import com.alejotech.crediya.service.PrestamoService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class MenuPagos {

    private final Scanner scanner;
    private final PagoService pagoService;
    private final PrestamoService prestamoService;

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
            System.out.println("3. Historial de pagos");
            System.out.println("4. Ver total pagado");
            System.out.println("5. Ver saldo pendiente");
            System.out.println("6. Eliminar pago");
            System.out.println("7. Volver");
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
                        historial();
                        break;

                    case 4:
                        totalPagado();
                        break;

                    case 5:
                        saldoPendiente();
                        break;

                    case 6:
                        eliminar();
                        break;

                    case 7:
                        break;

                    default:
                        System.out.println("Opción inválida.");
                }

            } catch (CrediYaException e) {
                System.out.println("Error: " + e.getMessage());
            }

        } while (opcion != 7);
    }

    private void registrar() throws CrediYaException {

        int prestamoId = leerEntero("ID del préstamo: ");
        Prestamo prestamo = prestamoService.buscarPorId(prestamoId);
        if (prestamo == null) {
            System.out.println("El préstamo no existe.");
            return;
        }

        BigDecimal monto = leerDecimal("Monto del pago: ");
        if (monto.signum() <= 0) {
            System.out.println("El monto debe ser mayor que cero.");
            return;
        }

        Pago pago = new Pago(prestamo, LocalDate.now(), monto);
        pagoService.registrar(pago);
        System.out.println("Pago registrado correctamente.");
    }

    private void listar() throws CrediYaException {

        List<Pago> pagos =
                pagoService.listar();

        if (pagos.isEmpty()) {
            System.out.println("No hay pagos.");
            return;
        }

        pagos.forEach(System.out::println);
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

        System.out.println("Pago eliminado.");
    }

    private int leerEntero(String mensaje) {

        while (true) {

            try {
                System.out.print(mensaje);
                return Integer.parseInt(scanner.nextLine());

            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido.");
            }
        }
    }

    private BigDecimal leerDecimal(String mensaje) {

        while (true) {

            try {
                System.out.print(mensaje);
                BigDecimal valor = new BigDecimal(scanner.nextLine());
                if (valor.signum() <= 0) {
                    System.out.println("Ingrese un valor mayor que cero.");
                    continue;
                }
                return valor;

            } catch (NumberFormatException e) {
                System.out.println("Ingrese un valor válido.");
            }
        }
    }
}