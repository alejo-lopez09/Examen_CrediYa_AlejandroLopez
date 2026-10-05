package com.alejotech.crediya.vista;

import com.alejotech.crediya.excepciones.CrediYaException;
import com.alejotech.crediya.util.ArchivoUtil;

import java.util.List;
import java.util.Scanner;

public class MenuRespaldos {
    private final Scanner scanner;

    public MenuRespaldos(Scanner scanner) {
        this.scanner = scanner;
    }

    public void mostrar() {
        int opcion;
        do {
            System.out.println("\n========== RESPALDOS EN ARCHIVO ==========");
            System.out.println("1. Empleados");
            System.out.println("2. Clientes");
            System.out.println("3. Préstamos");
            System.out.println("4. Pagos");
            System.out.println("5. Todos");
            System.out.println("6. Volver");
            opcion = leerEntero("Seleccione una opción: ");
            try {
                switch (opcion) {
                    case 1 -> mostrarArchivo("empleados.txt");
                    case 2 -> mostrarArchivo("clientes.txt");
                    case 3 -> mostrarArchivo("prestamos.txt");
                    case 4 -> mostrarArchivo("pagos.txt");
                    case 5 -> {
                        mostrarArchivo("empleados.txt");
                        mostrarArchivo("clientes.txt");
                        mostrarArchivo("prestamos.txt");
                        mostrarArchivo("pagos.txt");
                    }
                    case 6 -> { }
                    default -> System.out.println("Opción inválida.");
                }
            } catch (CrediYaException e) {
                System.out.println("Error al leer respaldos: " + e.getMessage());
            }
        } while (opcion != 6);
    }

    private void mostrarArchivo(String nombre) {
        List<String> registros = ArchivoUtil.leer(nombre);
        System.out.println("\n--- data/" + nombre + " (" + registros.size() + " registros) ---");
        if (registros.isEmpty()) {
            System.out.println("El archivo está vacío o aún no existe.");
            return;
        }
        registros.forEach(System.out::println);
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
