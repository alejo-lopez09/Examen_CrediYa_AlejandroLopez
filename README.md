# CrediYa - Sistema de cartera

Aplicación de consola Java para administrar empleados, clientes, préstamos y pagos. MySQL es la fuente principal de datos. Los archivos `data/*.txt` son respaldos reconstruibles que se actualizan después de cada escritura confirmada en la base.

## Requisitos

- JDK 17 o posterior.
- Maven 3.8 o posterior.
- MySQL 8.0 o posterior.

El proyecto apunta a Java 17: usa text blocks y `Stream.toList()`, disponibles desde versiones anteriores a 17, y compila en los equipos habituales de clase sin exigir Java 25.

## Preparar MySQL

1. Ejecutar [`sql/crediya_db.sql`](sql/crediya_db.sql) en MySQL. El script crea el esquema e inserta o actualiza datos de demostración; no elimina registros adicionales.
2. Configurar la conexión con variables de entorno. El URL por defecto es `jdbc:mysql://localhost:3306/crediya_db`, el usuario por defecto `root` y la contraseña por defecto vacía.

En PowerShell:

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

No guardes contraseñas reales en el código ni en archivos versionados. `CREATE TABLE IF NOT EXISTS` no altera tablas antiguas: comprueba que tengan las columnas, índices y restricciones definidas en el script.

## Ejecutar

Desde la raíz del proyecto:

```sh
mvn clean test
mvn exec:java -Dexec.mainClass=com.alejotech.crediya.Main
```

La base debe estar accesible al iniciar la aplicación. `Main` construye los servicios y delega la interacción a `vista/MenuPrincipal`; al iniciar también reconstruye los respaldos de texto desde MySQL.

## Ejemplo de salida

Con los datos incluidos en el script SQL y la fecha de ejemplo 5 de octubre de 2026, el menú ofrece las opciones:

```text
========== REPORTES ==========
1. Préstamos pendientes
2. Préstamos pagados
3. Préstamos superiores a $1.000.000
4. Préstamos en mora
5. Clientes con préstamos
6. Clientes morosos
7. Total prestado por empleado
8. Saldo total de cartera
9. Recaudo por cliente
10. Personas (polimorfismo)
11. Volver
Seleccione una opción:
```

El reporte de cartera muestra los agregados calculados a partir de los préstamos y pagos:

```text
--- TOTAL PRESTADO POR EMPLEADO ---
Ana Torres: $1300000.00
Luis Rojas: $500000.00

Saldo total de cartera: $1265000.00

--- RECAUDO POR CLIENTE ---
Laura Gómez: $150000.00
David Pérez: $510000.00
```

Los importes dependen de los datos vigentes de MySQL y pueden cambiar al registrar pagos o préstamos.

## Reglas de negocio

- El interés es simple sobre el capital. `Prestamo.calcularMontoTotal` es la implementación única del cálculo y redondea a dos decimales.
- La cuota mensual es el total dividido por las cuotas, redondeado a dos decimales.
- `EstadoPrestamo` modela los estados `PENDIENTE` y `PAGADO`; el acceso JDBC convierte el enum a texto solo al persistirlo.
- Un préstamo está en mora si ya venció una o más cuotas mensuales y los pagos acumulados no cubren el valor esperado para esas cuotas. El reporte consulta los pagos agrupados en una sola operación, no una consulta por préstamo.
- Registrar o eliminar un pago recalcula y persiste el estado del préstamo dentro de una transacción. La acción de “cambiar estado” solo permite corregir el estado para que refleje el saldo; no permite marcar como pagado un préstamo con deuda ni como pendiente uno saldado.
- Los respaldos `.txt` se escriben desde datos confirmados en MySQL, no se importan al arrancar. Esta decisión evita tomar archivos editables/desactualizados como fuente de verdad y evita restauraciones parciales que violen relaciones o dupliquen registros. Si la base se pierde, debe restaurarse desde su propia copia de seguridad; los `.txt` son respaldo de lectura humana, no una exportación de restauración transaccional.
- El vencimiento se estima por meses desde `fecha_inicio`; no se almacenan fechas individuales de vencimiento por cuota.

## Arquitectura y UML

El proyecto tuvo tres interfaces de consola porque fue acumulando implementaciones sucesivas: la consola monolítica dentro de `Main`, una segunda versión en `MenuConsola` y los menús separados de `vista/`. La aplicación ahora usa únicamente `vista/`; se eliminaron los dos menús históricos para no mantener implementaciones divergentes. `Main` solo crea los servicios, sincroniza los respaldos y abre el menú principal.

Las interfaces `*Repository` declaran los contratos de persistencia y las clases `*DAO` son sus adaptadores JDBC concretos. Se conserva esa distinción para que los servicios dependan de contratos y puedan probarse con implementaciones sustitutas.

![Diagrama UML de CrediYa](docs/UML_CrediYa.png)

El archivo fuente editable es [`docs/UML_CrediYa.puml`](docs/UML_CrediYa.puml).

## Pruebas

Ejecutar:

```sh
mvn test
```

Las pruebas automatizadas cubren el cálculo del préstamo, detección de mora, validaciones y reglas de servicio como el rechazo de un pago superior al saldo. Los tests unitarios de servicios usan repositorios sustitutos; las transacciones JDBC deben verificarse además con MySQL.
