package com.alejotech.crediya.util;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArchivoUtilTest {
    @Test
    void leeRegistrosPersistidosEnArchivoDeDatos() throws Exception {
        String nombre = "test-" + UUID.randomUUID() + ".txt";
        Path archivo = Path.of("data", nombre);
        try {
            String registro = ArchivoUtil.registro(1, "Nombre|con barra", "texto\\literal");
            ArchivoUtil.sincronizar(nombre, List.of(registro));

            assertEquals(List.of("1|Nombre\\|con barra|texto\\\\literal"),
                    ArchivoUtil.leer(nombre));
        } finally {
            Files.deleteIfExists(archivo);
        }
    }

    @Test
    void rechazaRutasFueraDeLaCarpetaDeDatos() {
        assertThrows(IllegalArgumentException.class,
                () -> ArchivoUtil.leer("../fuera.txt"));
    }
}
