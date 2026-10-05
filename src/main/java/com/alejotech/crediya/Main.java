package com.alejotech.crediya;

import com.alejotech.crediya.excepciones.CrediYaException;
import com.alejotech.crediya.service.ClienteService;
import com.alejotech.crediya.service.EmpleadoService;
import com.alejotech.crediya.service.PagoService;
import com.alejotech.crediya.service.PrestamoService;
import com.alejotech.crediya.vista.MenuPrincipal;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        EmpleadoService empleados = new EmpleadoService();
        ClienteService clientes = new ClienteService();
        PrestamoService prestamos = new PrestamoService();
        PagoService pagos = new PagoService();
        try {
            empleados.sincronizarRespaldo();
            clientes.sincronizarRespaldo();
            prestamos.sincronizarRespaldo();
            pagos.sincronizarRespaldo();
            new MenuPrincipal(new Scanner(System.in), empleados, clientes, prestamos, pagos).mostrar();
        } catch (CrediYaException e) {
            System.err.println("No se pudo iniciar CrediYa: " + e.getMessage());
        }
    }
}
