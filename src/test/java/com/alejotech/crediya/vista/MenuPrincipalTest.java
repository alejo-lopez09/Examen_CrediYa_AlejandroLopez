package com.alejotech.crediya.vista;

import com.alejotech.crediya.service.ClienteService;
import com.alejotech.crediya.service.EmpleadoService;
import com.alejotech.crediya.service.PagoService;
import com.alejotech.crediya.service.PrestamoService;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MenuPrincipalTest {
    @Test
    void permiteSalirSinAbrirLaConexionDeBaseDeDatos() {
        MenuPrincipal menu = new MenuPrincipal(new Scanner("7\n"),
                new EmpleadoService(), new ClienteService(),
                new PrestamoService(), new PagoService());

        assertDoesNotThrow(menu::mostrar);
    }
}
