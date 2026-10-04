package com.alejotech.crediya.Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion_DB {
    private static final String URL =
            "jdbc:mysql://localhost:3306/crediya_db";

    private static final String USER = "root";
    private static final String PASSWORD = "supapaxd12A";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

}
