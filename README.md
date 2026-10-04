# CrediYa - Sistema de cartera

Aplicación de consola Java para administrar empleados, clientes, préstamos y pagos. MySQL es la fuente principal de datos; después de cada operación de escritura exitosa, el sistema actualiza los respaldos de texto en `data/`.

## Requisitos

- JDK 25 o posterior (Java 25 es la versión LTS objetivo del proyecto).
- Maven 3.8 o posterior.
- MySQL 8.0 o posterior.

## Preparar MySQL

1. Ejecutar el contenido de [`sql/crediya_db.sql`](sql/crediya_db.sql) en MySQL.
2. Configurar las variables de entorno para la conexión. El URL por defecto es `jdbc:mysql://localhost:3306/crediya_db`; el usuario por defecto es `root` y la contraseña por defecto está vacía.

En PowerShell, por ejemplo:

```powershell
$env:CREDIYA_DB_URL = "jdbc:mysql://localhost:3306/crediya_db"
$env:CREDIYA_DB_USER = "root"
$env:CREDIYA_DB_PASSWORD = "tu-clave-local"
```

En Linux/macOS:

```sh
export CREDIYA_DB_URL="jdbc:mysql://localhost:3306/crediya_db"
export CREDIYA_DB_USER="root"
export CREDIYA_DB_PASSWORD="tu-clave-local"
```

No guardes contraseñas reales en el código ni en archivos versionados. Para una instalación existente, verifica que las tablas tengan las columnas y restricciones indicadas en el SQL; `CREATE TABLE IF NOT EXISTS` no modifica tablas preexistentes.

## Ejecutar

Desde la carpeta del proyecto:

```sh
mvn clean compile
mvn exec:java -Dexec.mainClass=com.alejotech.crediya.Main
```

La base debe estar accesible al iniciar la aplicación. El menú permite registrar, listar, buscar, actualizar y eliminar empleados y clientes; crear y consultar préstamos; registrar pagos y consultar saldos e historiales; y generar reportes.

## Reglas principales

- El interés se aplica como interés simple sobre el capital y el total se redondea a dos decimales.
- La cuota mensual es el total dividido entre el número de cuotas, con redondeo a dos decimales.
- Un préstamo se marca automáticamente como `PAGADO` al cubrir el saldo. No se permite marcar como pagado si conserva deuda.
- Los pagos se guardan en una transacción que bloquea el préstamo y actualiza su estado junto con el registro del abono. Eliminar un pago recalcula el saldo y estado en la misma transacción.
- Para este modelo, el vencimiento se estima como `fecha_inicio + número de cuotas en meses`; no se modelan fechas de vencimiento individuales por cuota.
- Los archivos `empleados.txt`, `clientes.txt`, `prestamos.txt` y `pagos.txt` son respaldos reconstruidos desde MySQL después de escrituras exitosas; no sustituyen a la base de datos ni se importan al iniciar.

## Estructura

- `modelo/`: entidades y reglas de cálculo.
- `service/`: validaciones y operaciones de negocio.
- `dao/`: repositorios e implementación JDBC.
- `Conexion/`: configuración de acceso a MySQL.
- `util/`: validación de datos y escritura/lectura de respaldos.
- `vista/`: menús por módulo; el punto de entrada activo es `Main`.
- `sql/`: esquema inicial de base de datos.
- `docs/UML_CrediYa.puml`: diagrama de clases PlantUML.

## Ejemplo de uso

1. Registrar un cliente y un empleado.
2. Crear un préstamo ingresando los identificadores de ambos, capital, interés y número de cuotas.
3. Revisar el total y el valor mensual calculados por el sistema.
4. Registrar uno o varios pagos y consultar el saldo o el historial.
5. Consultar reportes de préstamos pendientes, pagados, vencidos y clientes morosos.

## Verificaciones

```sh
mvn test
```

El proyecto aún no incluye pruebas automatizadas. Al agregarlas, deben residir bajo `src/test/java`. La conexión MySQL y el comportamiento transaccional deben verificarse además con una instancia de MySQL disponible.
