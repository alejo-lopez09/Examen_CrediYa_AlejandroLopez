package com.alejotech.crediya.vista;

import com.alejotech.crediya.excepciones.CrediYaException;
import com.alejotech.crediya.modelo.Cliente;
import com.alejotech.crediya.modelo.Prestamo;
import com.alejotech.crediya.service.ClienteService;
import com.alejotech.crediya.service.EmpleadoService;
import com.alejotech.crediya.service.PagoService;
import com.alejotech.crediya.service.PrestamoService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MenuReportes {

    private final Scanner scanner;

    private final EmpleadoService empleadoService;
    private final ClienteService clienteService;
    private final PrestamoService prestamoService;
    private final PagoService pagoService;

    public MenuReportes(
            Scanner scanner,
            EmpleadoService empleadoService,
            ClienteService clienteService,
            PrestamoService prestamoService,
            PagoService pagoService) {

        this.scanner = scanner;
        this.empleadoService = empleadoService;
        this.clienteService = clienteService;
        this.prestamoService = prestamoService;
        this.pagoService = pagoService;
    }

    public void mostrar() {

        int opcion;

        do {

            System.out.println("\n=================================");
            System.out.println("             REPORTES");
            System.out.println("=================================");
            System.out.println("1. Préstamos pendientes");
            System.out.println("2. Préstamos pagados");
            System.out.println("3. Préstamos superiores a $1.000.000");
            System.out.println("4. Préstamos vencidos");
            System.out.println("5. Clientes con préstamos");
            System.out.println("6. Clientes morosos");
            System.out.println("7. Volver");
            System.out.println("=================================");

            opcion = leerEntero("Seleccione una opción: ");

            try {

                switch (opcion) {

                    case 1:
                        pendientes();
                        break;

                    case 2:
                        pagados();
                        break;

                    case 3:
                        prestamosGrandes();
                        break;

                    case 4:
                        vencidos();
                        break;

                    case 5:
                        clientesConPrestamos();
                        break;

                    case 6:
                        morosos();
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

    private void pendientes() throws CrediYaException {

        List<Prestamo> prestamos =
                prestamoService.listar();

        List<Prestamo> pendientes =
                prestamos.stream()
                        .filter(p -> "PENDIENTE".equalsIgnoreCase(p.getEstado()))
                        .collect(Collectors.toList());

        System.out.println("\n--- PRÉSTAMOS PENDIENTES ---");

        pendientes.forEach(System.out::println);
    }

    private void pagados() throws CrediYaException {

        List<Prestamo> prestamos =
                prestamoService.listar();

        List<Prestamo> pagados =
                prestamos.stream()
                        .filter(p -> "PAGADO".equalsIgnoreCase(p.getEstado()))
                        .collect(Collectors.toList());

        System.out.println("\n--- PRÉSTAMOS PAGADOS ---");

        pagados.forEach(System.out::println);
    }

    private void prestamosGrandes()
            throws CrediYaException {

        List<Prestamo> prestamos =
                prestamoService.listar();

        BigDecimal limite =
                new BigDecimal("1000000");

        List<Prestamo> resultado =
                prestamos.stream()
                        .filter(p ->
                                p.getMonto().compareTo(limite) > 0)
                        .collect(Collectors.toList());

        System.out.println(
                "\n--- PRÉSTAMOS SUPERIORES A $1.000.000 ---"
        );

        resultado.forEach(System.out::println);
    }

    private void vencidos()
            throws CrediYaException {

        List<Prestamo> prestamos =
                prestamoService.listar();

        List<Prestamo> vencidos =
                prestamos.stream()
                        .filter(Prestamo::estaVencido)
                        .collect(Collectors.toList());

        System.out.println("\n--- PRÉSTAMOS VENCIDOS ---");

        vencidos.forEach(System.out::println);
    }

    private void clientesConPrestamos()
            throws CrediYaException {

        List<Cliente> clientes =
                clienteService.listar();

        System.out.println(
                "\n--- CLIENTES CON PRÉSTAMOS ---"
        );

        clientes.stream()
                .filter(cliente -> {

                    return !clienteService
                            .consultarPrestamos(
                                    cliente.getId()
                            )
                            .isEmpty();

                })
                .forEach(System.out::println);
    }

    private void morosos()
            throws CrediYaException {

        List<Prestamo> prestamos =
                prestamoService.listar();

        System.out.println("\n--- CLIENTES MOROSOS ---");

        prestamos.stream()
                .filter(Prestamo::estaVencido)
                .map(p -> p.getCliente())
                .filter(cliente -> cliente != null)
                .distinct()
                .forEach(System.out::println);
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
}