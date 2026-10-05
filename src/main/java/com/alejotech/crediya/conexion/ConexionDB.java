package com.alejotech.crediya.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    private static final String URL = valor("CREDIYA_DB_URL", "jdbc:mysql://localhost:3306/crediya_db");
    private static final String USER = valor("CREDIYA_DB_USER", "root");
    private static final String PASSWORD = valor("CREDIYA_DB_PASSWORD", "supapaxd12A");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String valor(String variable, String valorPorDefecto) {
        String valor = System.getenv(variable);
        return valor == null ? valorPorDefecto : valor;
    }
}
