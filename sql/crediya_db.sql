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

INSERT INTO empleados (id, nombre, documento, rol, correo, salario) VALUES
  (1, 'Ana Torres', '1001001001', 'Asesora', 'ana.torres@crediya.com', 2500000.00),
  (2, 'Luis Rojas', '1002002002', 'Coordinador', 'luis.rojas@crediya.com', 3200000.00)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), rol = VALUES(rol),
  correo = VALUES(correo), salario = VALUES(salario);

INSERT INTO clientes (id, nombre, documento, correo, telefono) VALUES
  (1, 'Laura Gómez', '2001001001', 'laura.gomez@example.com', '3001234567'),
  (2, 'David Pérez', '2002002002', 'david.perez@example.com', '3107654321')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), correo = VALUES(correo),
  telefono = VALUES(telefono);

INSERT INTO prestamos
  (id, cliente_id, empleado_id, monto, interes, cuotas, fecha_inicio, estado) VALUES
  (1, 1, 1, 1000000.00, 10.00, 6, '2026-08-05', 'PENDIENTE'),
  (2, 2, 2, 500000.00, 5.00, 5, '2026-08-01', 'PENDIENTE'),
  (3, 2, 1, 300000.00, 0.00, 3, '2026-04-01', 'PAGADO')
ON DUPLICATE KEY UPDATE cliente_id = VALUES(cliente_id), empleado_id = VALUES(empleado_id),
  monto = VALUES(monto), interes = VALUES(interes), cuotas = VALUES(cuotas),
  fecha_inicio = VALUES(fecha_inicio), estado = VALUES(estado);

INSERT INTO pagos (id, prestamo_id, fecha_pago, monto) VALUES
  (1, 1, '2026-09-05', 150000.00),
  (2, 2, '2026-09-01', 105000.00),
  (3, 2, '2026-10-01', 105000.00),
  (4, 3, '2026-05-01', 100000.00),
  (5, 3, '2026-06-01', 100000.00),
  (6, 3, '2026-07-01', 100000.00)
ON DUPLICATE KEY UPDATE prestamo_id = VALUES(prestamo_id), fecha_pago = VALUES(fecha_pago),
  monto = VALUES(monto);