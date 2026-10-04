package com.alejotech.crediya.util;

import java.math.BigDecimal;
import java.util.Scanner;

public class EntradaUtil {

    private EntradaUtil() {
    }

    public static String leerTexto(
            Scanner scanner,
            String mensaje) {

        System.out.print(mensaje);
        return scanner.nextLine();
    }

    public static int leerEntero(
            Scanner scanner,
            String mensaje) {

        while (true) {

            try {

                System.out.print(mensaje);

                return Integer.parseInt(
                        scanner.nextLine()
                );

            } catch (NumberFormatException e) {

                System.out.println(
                        "Ingrese un número válido."
                );
            }
        }
    }

    public static BigDecimal leerDecimal(
            Scanner scanner,
            String mensaje) {

        while (true) {

            try {

                System.out.print(mensaje);

                return new BigDecimal(
                        scanner.nextLine()
                );

            } catch (NumberFormatException e) {

                System.out.println(
                        "Ingrese un valor válido."
                );
            }
        }
    }

    public static void pausar(Scanner scanner) {

        System.out.println(
                "\nPresione ENTER para continuar..."
        );

        scanner.nextLine();
    }
}