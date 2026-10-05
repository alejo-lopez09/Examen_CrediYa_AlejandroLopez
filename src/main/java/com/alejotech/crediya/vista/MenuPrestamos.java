package com.alejotech.crediya.vista;

import com.alejotech.crediya.excepciones.CrediYaException;
import com.alejotech.crediya.excepciones.ValidacionException;
import com.alejotech.crediya.modelo.Cliente;
import com.alejotech.crediya.modelo.Empleado;
import com.alejotech.crediya.modelo.EstadoPrestamo;
import com.alejotech.crediya.modelo.Prestamo;
import com.alejotech.crediya.service.PrestamoService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class MenuPrestamos {

    public final Scanner scanner;
    public final PrestamoService prestamoService;

    public MenuPrestamos(
            Scanner scanner,
            PrestamoService prestamoService) {

        this.scanner = scanner;
        this.prestamoService = prestamoService;
    }

    public void mostrar() {

        int opcion;

        do {

            System.out.println("\n=================================");
            System.out.println("       GESTIÓN DE PRÉSTAMOS");
            System.out.println("=================================");
            System.out.println("1. Registrar préstamo");
            System.out.println("2. Listar préstamos");
            System.out.println("3. Buscar préstamo");
            System.out.println("4. Cambiar estado");
            System.out.println("5. Eliminar préstamo");
            System.out.println("6. Volver");
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
                        buscar();
                        break;

                    case 4:
                        cambiarEstado();
                        break;

                    case 5:
                        eliminar();
                        break;

                    case 6:
                        break;

                    default:
                        System.out.println("Opción inválida.");
                }

            } catch (CrediYaException e) {
                System.out.println("Error: " + e.getMessage());
            }

        } while (opcion != 6);
    }

    private void registrar() throws CrediYaException {

        System.out.println("\n--- REGISTRAR PRÉSTAMO ---");

        int clienteId =
                leerEntero("ID del cliente: ");

        int empleadoId =
                leerEntero("ID del empleado: ");

        BigDecimal monto =
                leerDecimal("Monto: ");

        BigDecimal interes =
                leerDecimal("Interés (%): ");

        int cuotas =
                leerEntero("Número de cuotas: ");

        Cliente cliente = new Cliente(clienteId, null, null, null, null);
        Empleado empleado = new Empleado(
                empleadoId, null, null, null, null, BigDecimal.ZERO);

        Prestamo prestamo = new Prestamo(
                cliente,
                empleado,
                monto,
                interes,
                cuotas,
                LocalDate.now(),
                null
        );

        prestamoService.crear(prestamo);

        System.out.println("Préstamo registrado correctamente.");
        System.out.println(
                "Total: " + prestamo.calcularMontoTotal()
        );
        System.out.println(
                "Cuota mensual: " + prestamo.calcularCuotaMensual()
        );
    }

    private void listar() throws CrediYaException {

        List<Prestamo> prestamos =
                prestamoService.listar();

        if (prestamos.isEmpty()) {
            System.out.println("No hay préstamos.");
            return;
        }

        prestamos.forEach(System.out::println);
    }

    private void buscar() throws CrediYaException {

        int id = leerEntero("ID del préstamo: ");

        Prestamo prestamo =
                prestamoService.buscarPorId(id);

        if (prestamo == null) {
            System.out.println("Préstamo no encontrado.");
            return;
        }

        System.out.println(prestamo);
    }

    private void cambiarEstado() throws CrediYaException {

        int id =
                leerEntero("ID del préstamo: ");

        String estado =
                leerTexto("Nuevo estado: ");

        EstadoPrestamo nuevoEstado;
        try {
            nuevoEstado = EstadoPrestamo.desdeTexto(estado);
        } catch (IllegalArgumentException e) {
            throw new ValidacionException(e.getMessage());
        }

        prestamoService.cambiarEstado(id, nuevoEstado);

        System.out.println("Estado actualizado.");
    }

    private void eliminar() throws CrediYaException {

        int id =
                leerEntero("ID del préstamo: ");

        prestamoService.eliminar(id);

        System.out.println("Préstamo eliminado.");
    }

    private String leerTexto(String mensaje) {

        System.out.print(mensaje);
        return scanner.nextLine();
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
                return new BigDecimal(scanner.nextLine());

            } catch (NumberFormatException e) {
                System.out.println("Ingrese un valor válido.");
            }
        }
    }
}