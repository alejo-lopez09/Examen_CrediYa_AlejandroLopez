CREATE DATABASE IF NOT EXISTS crediya_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE crediya_db;

CREATE TABLE IF NOT EXISTS empleados (
  id INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(80) NOT NULL,
  documento VARCHAR(30) NOT NULL,
  rol VARCHAR(30) NOT NULL,
  correo VARCHAR(80) NOT NULL,
  salario DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_empleados_documento (documento)
);

CREATE TABLE IF NOT EXISTS clientes (
  id INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(80) NOT NULL,
  documento VARCHAR(30) NOT NULL,
  correo VARCHAR(80) NOT NULL,
  telefono VARCHAR(20) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_clientes_documento (documento)
);

CREATE TABLE IF NOT EXISTS prestamos (
  id INT NOT NULL AUTO_INCREMENT,
  cliente_id INT NOT NULL,
  empleado_id INT NOT NULL,
  monto DECIMAL(12,2) NOT NULL,
  interes DECIMAL(5,2) NOT NULL,
  cuotas INT NOT NULL,
  fecha_inicio DATE NOT NULL,
  estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
  PRIMARY KEY (id),
  KEY idx_prestamos_cliente (cliente_id),
  KEY idx_prestamos_empleado (empleado_id),
  KEY idx_prestamos_estado_fecha (estado, fecha_inicio),
  CONSTRAINT fk_prestamos_cliente
    FOREIGN KEY (cliente_id) REFERENCES clientes (id),
  CONSTRAINT fk_prestamos_empleado
    FOREIGN KEY (empleado_id) REFERENCES empleados (id)
);

CREATE TABLE IF NOT EXISTS pagos (
  id INT NOT NULL AUTO_INCREMENT,
  prestamo_id INT NOT NULL,
  fecha_pago DATE NOT NULL,
  monto DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (id),
  KEY idx_pagos_prestamo_fecha (prestamo_id, fecha_pago),
  CONSTRAINT fk_pagos_prestamo
    FOREIGN KEY (prestamo_id) REFERENCES prestamos (id)
);