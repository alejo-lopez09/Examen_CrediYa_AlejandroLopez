package com.alejotech.crediya;

import com.alejotech.crediya.Conexion.Conexion_DB;

public class Main {

    public static void main(String[] args) {

        try {
            Conexion_DB.getConnection();

            System.out.println("Conexión exitosa a MySQL");

        } catch (Exception e) {
            System.out.println("Error de conexión");
            e.printStackTrace();
        }
    }
}