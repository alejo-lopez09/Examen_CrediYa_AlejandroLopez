package com.alejotech.crediya.util;

import com.alejotech.crediya.excepciones.CrediYaException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class ArchivoUtil {

    private static final Path CARPETA_DATA = Path.of("data");

    public static void crearCarpetaData() {
        try {
            Files.createDirectories(CARPETA_DATA);
        } catch (IOException e) {
            throw new CrediYaException("No se pudo crear la carpeta de datos.", e);
        }
    }

    public static void guardar(String nombreArchivo, String contenido) {
        crearCarpetaData();
        try (BufferedWriter writer = Files.newBufferedWriter(
                resolver(nombreArchivo), StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(contenido);
            writer.newLine();
        } catch (IOException e) {
            throw new CrediYaException("No se pudo guardar el archivo " + nombreArchivo + ".", e);
        }
    }

    public static List<String> leer(String nombreArchivo) {
        crearCarpetaData();
        try {
            return Files.exists(resolver(nombreArchivo))
                    ? Files.readAllLines(resolver(nombreArchivo))
                    : List.of();
        } catch (IOException e) {
            throw new CrediYaException("No se pudo leer el archivo " + nombreArchivo + ".", e);
        }
    }

    public static void sobrescribir(String nombreArchivo, List<String> contenido) {
        sincronizar(nombreArchivo, contenido);
    }

    public static void sincronizar(String nombreArchivo, List<String> contenido) {
        crearCarpetaData();
        Path ruta = resolver(nombreArchivo);
        Path temporal = null;
        try {
            temporal = Files.createTempFile(CARPETA_DATA, nombreArchivo, ".tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(temporal)) {
                for (String linea : contenido) {
                    writer.write(linea);
                    writer.newLine();
                }
            }
            try {
                Files.move(temporal, ruta, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporal, ruta, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            if (temporal != null) {
                try {
                    Files.deleteIfExists(temporal);
                } catch (IOException limpieza) {
                    e.addSuppressed(limpieza);
                }
            }
            throw new CrediYaException(
                    "No se pudo sincronizar el respaldo " + nombreArchivo
                            + "; la operación en la base de datos pudo haberse completado.",
                    e);
        }
    }

    public static void limpiar(String nombreArchivo) {
        crearCarpetaData();
        try {
            Files.writeString(
                    resolver(nombreArchivo),
                    "",
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            throw new CrediYaException("No se pudo limpiar el archivo " + nombreArchivo + ".", e);
        }
    }

    public static String registro(Object... campos) {
        return Stream.of(campos)
                .map(Objects::toString)
                .map(ArchivoUtil::escapar)
                .reduce((primero, siguiente) -> primero + "|" + siguiente)
                .orElse("");
    }

    private static String escapar(String valor) {
        return valor.replace("\\", "\\\\")
                .replace("|", "\\|")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }

    private static Path resolver(String nombreArchivo) {
        Path nombre = Path.of(nombreArchivo);
        if (nombre.isAbsolute() || nombre.getNameCount() != 1
                || !CARPETA_DATA.resolve(nombre).normalize().getParent().equals(CARPETA_DATA.normalize())) {
            throw new IllegalArgumentException("Nombre de archivo de datos inválido.");
        }
        return CARPETA_DATA.resolve(nombre);
    }
}