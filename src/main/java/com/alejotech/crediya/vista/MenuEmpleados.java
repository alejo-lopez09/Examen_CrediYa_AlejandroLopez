package com.alejotech.crediya.vista;

import com.alejotech.crediya.excepciones.CrediYaException;
import com.alejotech.crediya.modelo.Empleado;
import com.alejotech.crediya.service.EmpleadoService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class MenuEmpleados {

    private final Scanner scanner;
    private final EmpleadoService empleadoService;

    public MenuEmpleados(
            Scanner scanner,
            EmpleadoService empleadoService) {

        this.scanner = scanner;
        this.empleadoService = empleadoService;
    }

    public void mostrar() {

        int opcion;

        do {
            System.out.println("\n=================================");
            System.out.println("       GESTIÓN DE EMPLEADOS");
            System.out.println("=================================");
            System.out.println("1. Registrar empleado");
            System.out.println("2. Listar empleados");
            System.out.println("3. Buscar empleado");
            System.out.println("4. Actualizar empleado");
            System.out.println("5. Eliminar empleado");
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
                        actualizar();
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

        System.out.println("\n--- REGISTRAR EMPLEADO ---");

        String nombre = leerTexto("Nombre: ");
        String documento = leerTexto("Documento: ");
        String rol = leerTexto("Rol: ");
        String correo = leerTexto("Correo: ");
        BigDecimal salario = leerDecimal("Salario: ");

        Empleado empleado = new Empleado(
                0,
                nombre,
                documento,
                correo,
                rol,
                salario
        );

        empleadoService.registrar(empleado);

        System.out.println("Empleado registrado correctamente.");
    }

    private void listar() throws CrediYaException {

        System.out.println("\n--- EMPLEADOS ---");

        List<Empleado> empleados =
                empleadoService.listar();

        if (empleados.isEmpty()) {
            System.out.println("No hay empleados registrados.");
            return;
        }

        empleados.forEach(System.out::println);
    }

    private void buscar() throws CrediYaException {

        int id = leerEntero("ID del empleado: ");

        Empleado empleado =
                empleadoService.buscarPorId(id);

        if (empleado == null) {
            System.out.println("Empleado no encontrado.");
            return;
        }

        System.out.println(empleado);
    }

    private void actualizar() throws CrediYaException {

        int id = leerEntero("ID del empleado: ");

        Empleado empleado =
                empleadoService.buscarPorId(id);

        if (empleado == null) {
            System.out.println("Empleado no encontrado.");
            return;
        }

        empleado.setNombre(leerTexto("Nuevo nombre: "));
        empleado.setDocumento(leerTexto("Nuevo documento: "));
        empleado.setRol(leerTexto("Nuevo rol: "));
        empleado.setCorreo(leerTexto("Nuevo correo: "));
        empleado.setSalario(leerDecimal("Nuevo salario: "));

        empleadoService.actualizar(empleado);

        System.out.println("Empleado actualizado correctamente.");
    }

    private void eliminar() throws CrediYaException {

        int id = leerEntero("ID del empleado: ");

        empleadoService.eliminar(id);

        System.out.println("Empleado eliminado correctamente.");
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