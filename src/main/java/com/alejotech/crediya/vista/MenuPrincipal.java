package com.alejotech.crediya.vista;

import com.alejotech.crediya.service.ClienteService;
import com.alejotech.crediya.service.EmpleadoService;
import com.alejotech.crediya.service.PagoService;
import com.alejotech.crediya.service.PrestamoService;

import java.util.Scanner;

public class MenuPrincipal {

    private final Scanner scanner;

    private final MenuEmpleados menuEmpleados;
    private final MenuClientes menuClientes;
    private final MenuPrestamos menuPrestamos;
    private final MenuPagos menuPagos;
    private final MenuReportes menuReportes;

    public MenuPrincipal(
            Scanner scanner,
            EmpleadoService empleadoService,
            ClienteService clienteService,
            PrestamoService prestamoService,
            PagoService pagoService) {

        this.scanner = scanner;

        this.menuEmpleados =
                new MenuEmpleados(scanner, empleadoService);

        this.menuClientes =
                new MenuClientes(scanner, clienteService);

        this.menuPrestamos =
                new MenuPrestamos(scanner, prestamoService);

        this.menuPagos =
                new MenuPagos(scanner, pagoService, prestamoService);

        this.menuReportes =
                new MenuReportes(
                        scanner,
                        empleadoService,
                        clienteService,
                        prestamoService,
                        pagoService
                );
    }

    public void mostrar() {

        int opcion;

        do {
            System.out.println("\n=================================");
            System.out.println("       SISTEMA CREDIYA");
            System.out.println("=================================");
            System.out.println("1. Gestión de empleados");
            System.out.println("2. Gestión de clientes");
            System.out.println("3. Gestión de préstamos");
            System.out.println("4. Gestión de pagos");
            System.out.println("5. Reportes");
            System.out.println("6. Salir");
            System.out.println("=================================");

            opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {

                case 1:
                    menuEmpleados.mostrar();
                    break;

                case 2:
                    menuClientes.mostrar();
                    break;

                case 3:
                    menuPrestamos.mostrar();
                    break;

                case 4:
                    menuPagos.mostrar();
                    break;

                case 5:
                    menuReportes.mostrar();
                    break;

                case 6:
                    System.out.println("Gracias por utilizar CrediYa.");
                    break;

                default:
                    System.out.println("Opción inválida.");
            }

        } while (opcion != 6);
    }

    private int leerEntero(String mensaje) {

        while (true) {

            try {
                System.out.print(mensaje);
                return Integer.parseInt(scanner.nextLine());

            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un número válido.");
            }
        }
    }
}