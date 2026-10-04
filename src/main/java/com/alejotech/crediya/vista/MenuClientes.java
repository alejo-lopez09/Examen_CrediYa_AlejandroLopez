package com.alejotech.crediya.vista;

import com.alejotech.crediya.excepciones.CrediYaException;
import com.alejotech.crediya.modelo.Cliente;
import com.alejotech.crediya.modelo.Prestamo;
import com.alejotech.crediya.service.ClienteService;

import java.util.List;
import java.util.Scanner;

public class MenuClientes {

    private final Scanner scanner;
    private final ClienteService clienteService;

    public MenuClientes(
            Scanner scanner,
            ClienteService clienteService) {

        this.scanner = scanner;
        this.clienteService = clienteService;
    }

    public void mostrar() {

        int opcion;

        do {

            System.out.println("\n=================================");
            System.out.println("        GESTIÓN DE CLIENTES");
            System.out.println("=================================");
            System.out.println("1. Registrar cliente");
            System.out.println("2. Listar clientes");
            System.out.println("3. Buscar cliente");
            System.out.println("4. Actualizar cliente");
            System.out.println("5. Eliminar cliente");
            System.out.println("6. Ver préstamos del cliente");
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
                        buscar();
                        break;

                    case 4:
                        actualizar();
                        break;

                    case 5:
                        eliminar();
                        break;

                    case 6:
                        verPrestamos();
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

        String nombre = leerTexto("Nombre: ");
        String documento = leerTexto("Documento: ");
        String correo = leerTexto("Correo: ");
        String telefono = leerTexto("Teléfono: ");

        Cliente cliente = new Cliente(
                0,
                nombre,
                documento,
                correo,
                telefono
        );

        clienteService.registrar(cliente);

        System.out.println("Cliente registrado correctamente.");
    }

    private void listar() throws CrediYaException {

        List<Cliente> clientes =
                clienteService.listar();

        if (clientes.isEmpty()) {
            System.out.println("No hay clientes.");
            return;
        }

        clientes.forEach(System.out::println);
    }

    private void buscar() throws CrediYaException {

        int id = leerEntero("ID del cliente: ");

        Cliente cliente =
                clienteService.buscarPorId(id);

        if (cliente == null) {
            System.out.println("Cliente no encontrado.");
            return;
        }

        System.out.println(cliente);
    }

    private void actualizar() throws CrediYaException {

        int id = leerEntero("ID del cliente: ");

        Cliente cliente =
                clienteService.buscarPorId(id);

        if (cliente == null) {
            System.out.println("Cliente no encontrado.");
            return;
        }

        cliente.setNombre(leerTexto("Nuevo nombre: "));
        cliente.setDocumento(leerTexto("Nuevo documento: "));
        cliente.setCorreo(leerTexto("Nuevo correo: "));
        cliente.setTelefono(leerTexto("Nuevo teléfono: "));

        clienteService.actualizar(cliente);

        System.out.println("Cliente actualizado.");
    }

    private void eliminar() throws CrediYaException {

        int id = leerEntero("ID del cliente: ");

        clienteService.eliminar(id);

        System.out.println("Cliente eliminado.");
    }

    private void verPrestamos() throws CrediYaException {

        int id = leerEntero("ID del cliente: ");

        List<Prestamo> prestamos =
                clienteService.consultarPrestamos(id);

        if (prestamos.isEmpty()) {
            System.out.println("El cliente no tiene préstamos.");
            return;
        }

        prestamos.forEach(System.out::println);
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
}