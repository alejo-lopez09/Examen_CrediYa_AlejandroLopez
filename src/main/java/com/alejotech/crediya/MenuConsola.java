package com.alejotech.crediya;

import com.alejotech.crediya.modelo.Cliente;
import com.alejotech.crediya.modelo.Empleado;
import com.alejotech.crediya.modelo.Pago;
import com.alejotech.crediya.modelo.Prestamo;
import com.alejotech.crediya.modelo.EstadoPrestamo;
import com.alejotech.crediya.excepciones.CrediYaException;
import com.alejotech.crediya.util.ArchivoUtil;

import java.math.BigDecimal;

import com.alejotech.crediya.service.ClienteService;
import com.alejotech.crediya.service.EmpleadoService;
import com.alejotech.crediya.service.PagoService;
import com.alejotech.crediya.service.PrestamoService;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;

public class MenuConsola {

    private static final Scanner scanner = new Scanner(System.in);

    private static final EmpleadoService empleadoService = new EmpleadoService();
    private static final ClienteService clienteService = new ClienteService();
    private static final PrestamoService prestamoService = new PrestamoService();
    private static final PagoService pagoService = new PagoService();

    private static final EstadoPrestamo PENDIENTE = EstadoPrestamo.PENDIENTE;
    private static final EstadoPrestamo PAGADO = EstadoPrestamo.PAGADO;
    private static final BigDecimal MONTO_GRANDE = BigDecimal.valueOf(1_000_000);
    private static final Locale CO = Locale.forLanguageTag("es-CO");

    /** Una opción de menú: texto + acción a ejecutar. */
    private record Opcion(String texto, Runnable accion) {}

    public static void iniciar() {

        ArchivoUtil.crearCarpetaData();

        ejecutarMenu("CREDIYA S.A.S. - SISTEMA DE CARTERA", "Salir",
                new Opcion("Gestión de empleados", MenuConsola::menuEmpleados),
                new Opcion("Gestión de clientes", MenuConsola::menuClientes),
                new Opcion("Gestión de préstamos", MenuConsola::menuPrestamos),
                new Opcion("Gestión de pagos", MenuConsola::menuPagos),
                new Opcion("Reportes", MenuConsola::menuReportes));

        System.out.println("\nCerrando CrediYa...");
        scanner.close();
    }

    // =========================================================
    // MOTOR DE MENÚS (reemplaza 5 do/while + switch repetidos)
    // =========================================================

    private static void ejecutarMenu(String titulo, String textoSalida, Opcion... opciones) {

        int salir = opciones.length + 1;
        int opcion;

        do {
            System.out.println();
            System.out.println("========== " + titulo + " ==========");

            for (int i = 0; i < opciones.length; i++) {
                System.out.println((i + 1) + ". " + opciones[i].texto());
            }
            System.out.println(salir + ". " + textoSalida);

            opcion = leerEntero("Seleccione una opción: ");

            if (opcion >= 1 && opcion <= opciones.length) {
                try {
                    opciones[opcion - 1].accion().run();
                } catch (CrediYaException | IllegalArgumentException e) {
                    System.out.println("Operación no realizada: " + e.getMessage());
                }
            } else if (opcion != salir) {
                System.out.println("Opción inválida.");
            }

        } while (opcion != salir);
    }

    private static void resultado(boolean ok, String exito, String fallo) {
        System.out.println(ok ? exito : fallo);
    }

    // =========================================================
    // EMPLEADOS
    // =========================================================

    private static void menuEmpleados() {
        ejecutarMenu("EMPLEADOS", "Volver",
                new Opcion("Registrar empleado", MenuConsola::registrarEmpleado),
                new Opcion("Listar empleados", MenuConsola::listarEmpleados),
                new Opcion("Buscar empleado", MenuConsola::buscarEmpleado),
                new Opcion("Actualizar empleado", MenuConsola::actualizarEmpleado),
                new Opcion("Eliminar empleado", MenuConsola::eliminarEmpleado));
    }

    private static void registrarEmpleado() {

        System.out.println("\n----- REGISTRAR EMPLEADO -----");

        Empleado empleado = new Empleado(
                0,
                leerTexto("Nombre: "),
                leerTexto("Documento: "),
                leerTexto("Correo: "),
                leerTexto("Rol: "),
                leerDecimalPositivo("Salario: ")
        );

        resultado(empleadoService.registrar(empleado),
                "Empleado registrado correctamente.",
                "No se pudo registrar el empleado.");
    }

    private static void listarEmpleados() {

        System.out.println("\n----- LISTA DE EMPLEADOS -----");

        List<Empleado> empleados = empleadoService.listar();

        if (empleados.isEmpty()) {
            System.out.println("No hay empleados registrados.");
            return;
        }

        empleados.forEach(e -> System.out.println(describir(e)));
    }

    private static void buscarEmpleado() {

        Empleado empleado = empleadoService.buscarPorId(leerEntero("ID del empleado: "));

        if (empleado == null) {
            System.out.println("Empleado no encontrado.");
            return;
        }

        System.out.println(describir(empleado));
    }

    private static void actualizarEmpleado() {

        Empleado empleado = empleadoService.buscarPorId(leerEntero("ID del empleado: "));

        if (empleado == null) {
            System.out.println("Empleado no encontrado.");
            return;
        }

        System.out.println("(Enter vacío = dejar el valor actual)");

        empleado.setNombre(leerTextoOpcional("Nombre", empleado.getNombre()));
        empleado.setDocumento(leerTextoOpcional("Documento", empleado.getDocumento()));
        empleado.setCorreo(leerTextoOpcional("Correo", empleado.getCorreo()));
        empleado.setRol(leerTextoOpcional("Rol", empleado.getRol()));
        empleado.setSalario(leerDecimalOpcional("Salario", empleado.getSalario()));

        resultado(empleadoService.actualizar(empleado),
                "Empleado actualizado correctamente.",
                "No se pudo actualizar.");
    }

    private static void eliminarEmpleado() {

        int id = leerEntero("ID del empleado: ");

        if (!confirmar("¿Seguro que desea eliminar el empleado " + id + "?")) {
            return;
        }

        resultado(empleadoService.eliminar(id),
                "Empleado eliminado correctamente.",
                "No se pudo eliminar el empleado.");
    }

    private static String describir(Empleado e) {
        return "ID: " + e.getId()
                + " | Nombre: " + e.getNombre()
                + " | Documento: " + e.getDocumento()
                + " | Rol: " + e.getRol()
                + " | Correo: " + e.getCorreo()
                + " | Salario: " + dinero(e.getSalario());
    }

    // =========================================================
    // CLIENTES
    // =========================================================

    private static void menuClientes() {
        ejecutarMenu("CLIENTES", "Volver",
                new Opcion("Registrar cliente", MenuConsola::registrarCliente),
                new Opcion("Listar clientes", MenuConsola::listarClientes),
                new Opcion("Buscar cliente", MenuConsola::buscarCliente),
                new Opcion("Consultar préstamos del cliente", MenuConsola::consultarPrestamosCliente),
                new Opcion("Actualizar cliente", MenuConsola::actualizarCliente),
                new Opcion("Eliminar cliente", MenuConsola::eliminarCliente));
    }

    private static void registrarCliente() {

        System.out.println("\n----- REGISTRAR CLIENTE -----");

        Cliente cliente = new Cliente(
                0,
                leerTexto("Nombre: "),
                leerTexto("Documento: "),
                leerTexto("Correo: "),
                leerTexto("Teléfono: ")
        );

        resultado(clienteService.registrar(cliente),
                "Cliente registrado correctamente.",
                "No se pudo registrar el cliente.");
    }

    private static void listarClientes() {

        System.out.println("\n----- LISTA DE CLIENTES -----");

        List<Cliente> clientes = clienteService.listar();

        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }

        clientes.forEach(c -> System.out.println(describir(c)));
    }

    private static void buscarCliente() {

        Cliente cliente = clienteService.buscarPorId(leerEntero("ID del cliente: "));

        if (cliente == null) {
            System.out.println("Cliente no encontrado.");
            return;
        }

        System.out.println(describir(cliente));
    }

    private static void consultarPrestamosCliente() {

        int clienteId = leerEntero("ID del cliente: ");
        Cliente cliente = clienteService.buscarPorId(clienteId);

        if (cliente == null) {
            System.out.println("El cliente no existe.");
            return;
        }

        List<Prestamo> prestamos = clienteService.consultarPrestamos(clienteId);

        System.out.println("\nPréstamos de " + cliente.getNombre());

        if (prestamos.isEmpty()) {
            System.out.println("El cliente no tiene préstamos.");
            return;
        }

        mostrarPrestamos(prestamos);
    }

    private static void actualizarCliente() {

        Cliente cliente = clienteService.buscarPorId(leerEntero("ID del cliente: "));

        if (cliente == null) {
            System.out.println("Cliente no encontrado.");
            return;
        }

        System.out.println("(Enter vacío = dejar el valor actual)");

        cliente.setNombre(leerTextoOpcional("Nombre", cliente.getNombre()));
        cliente.setDocumento(leerTextoOpcional("Documento", cliente.getDocumento()));
        cliente.setCorreo(leerTextoOpcional("Correo", cliente.getCorreo()));
        cliente.setTelefono(leerTextoOpcional("Teléfono", cliente.getTelefono()));

        resultado(clienteService.actualizar(cliente),
                "Cliente actualizado correctamente.",
                "No se pudo actualizar.");
    }

    private static void eliminarCliente() {

        int id = leerEntero("ID del cliente: ");

        if (!confirmar("¿Seguro que desea eliminar el cliente " + id + "?")) {
            return;
        }

        resultado(clienteService.eliminar(id),
                "Cliente eliminado correctamente.",
                "No se pudo eliminar el cliente.");
    }

    private static String describir(Cliente c) {
        return "ID: " + c.getId()
                + " | Nombre: " + c.getNombre()
                + " | Documento: " + c.getDocumento()
                + " | Correo: " + c.getCorreo()
                + " | Teléfono: " + c.getTelefono();
    }

    // =========================================================
    // PRÉSTAMOS
    // =========================================================

    private static void menuPrestamos() {
        ejecutarMenu("PRÉSTAMOS", "Volver",
                new Opcion("Crear préstamo", MenuConsola::crearPrestamo),
                new Opcion("Listar préstamos", MenuConsola::listarPrestamos),
                new Opcion("Buscar préstamo", MenuConsola::buscarPrestamo),
                new Opcion("Cambiar estado", MenuConsola::cambiarEstadoPrestamo),
                new Opcion("Eliminar préstamo", MenuConsola::eliminarPrestamo));
    }

    private static void crearPrestamo() {

        System.out.println("\n----- CREAR PRÉSTAMO -----");

        Cliente cliente = clienteService.buscarPorId(leerEntero("ID del cliente: "));

        if (cliente == null) {
            System.out.println("El cliente no existe.");
            return;
        }

        Empleado empleado = empleadoService.buscarPorId(leerEntero("ID del empleado: "));

        if (empleado == null) {
            System.out.println("El empleado no existe.");
            return;
        }

        BigDecimal monto = leerDecimalPositivo("Monto del préstamo: ");
        BigDecimal interes = leerDecimalNoNegativo("Interés (%): ");
        int cuotas = leerEnteroPositivo("Número de cuotas: ");

        Prestamo prestamo = new Prestamo(
                cliente, empleado, monto, interes, cuotas, LocalDate.now(), PENDIENTE.name()
        );

        System.out.println();
        System.out.println("Monto total: " + dinero(prestamo.calcularMontoTotal()));
        System.out.println("Cuota mensual: " + dinero(prestamo.calcularCuotaMensual()));

        resultado(prestamoService.crear(prestamo),
                "Préstamo creado correctamente.",
                "No se pudo crear el préstamo.");
    }

    private static void listarPrestamos() {

        System.out.println("\n----- LISTA DE PRÉSTAMOS -----");

        List<Prestamo> prestamos = prestamoService.listar();

        if (prestamos.isEmpty()) {
            System.out.println("No hay préstamos registrados.");
            return;
        }

        mostrarPrestamos(prestamos);
    }

    private static void mostrarPrestamos(List<Prestamo> prestamos) {

        for (Prestamo p : prestamos) {

            System.out.println("--------------------------------");
            System.out.println("ID: " + p.getId());
            System.out.println("Cliente: " + p.getCliente().getNombre());
            System.out.println("Empleado: " + p.getEmpleado().getNombre());
            System.out.println("Monto: " + dinero(p.getMonto()));
            System.out.println("Interés: " + p.getInteres() + "%");
            System.out.println("Monto total: " + dinero(p.calcularMontoTotal()));
            System.out.println("Cuotas: " + p.getCuotas());
            System.out.println("Cuota mensual: " + dinero(p.calcularCuotaMensual()));
            System.out.println("Fecha: " + p.getFechaInicio());
            System.out.println("Estado: " + p.getEstado());
        }

        System.out.println("--------------------------------");
    }

    private static void buscarPrestamo() {

        Prestamo prestamo = prestamoService.buscarPorId(leerEntero("ID del préstamo: "));

        if (prestamo == null) {
            System.out.println("Préstamo no encontrado.");
            return;
        }

        mostrarPrestamos(List.of(prestamo));
    }

    private static void cambiarEstadoPrestamo() {

        int id = leerEntero("ID del préstamo: ");

        System.out.println("1. " + PENDIENTE);
        System.out.println("2. " + PAGADO);

        int opcion = leerEntero("Nuevo estado: ");

        EstadoPrestamo estado = switch (opcion) {
            case 1 -> PENDIENTE;
            case 2 -> PAGADO;
            default -> null;
        };

        if (estado == null) {
            System.out.println("Opción inválida.");
            return;
        }

        resultado(prestamoService.cambiarEstado(id, String.valueOf(estado)),
                "Estado actualizado correctamente.",
                "No se pudo actualizar el estado.");
    }

    private static void eliminarPrestamo() {

        int id = leerEntero("ID del préstamo: ");

        if (!confirmar("¿Seguro que desea eliminar el préstamo " + id + "?")) {
            return;
        }

        resultado(prestamoService.eliminar(id),
                "Préstamo eliminado correctamente.",
                "No se pudo eliminar el préstamo.");
    }

    // =========================================================
    // PAGOS
    // =========================================================

    private static void menuPagos() {
        ejecutarMenu("PAGOS", "Volver",
                new Opcion("Registrar pago", MenuConsola::registrarPago),
                new Opcion("Listar pagos", MenuConsola::listarPagos),
                new Opcion("Ver historial de préstamo", MenuConsola::historialPagos),
                new Opcion("Consultar saldo pendiente", MenuConsola::consultarSaldo),
                new Opcion("Consultar total pagado", MenuConsola::consultarTotalPagado),
                new Opcion("Eliminar pago", MenuConsola::eliminarPago));
    }

    private static void registrarPago() {

        System.out.println("\n----- REGISTRAR PAGO -----");

        int prestamoId = leerEntero("ID del préstamo: ");
        Prestamo prestamo = prestamoService.buscarPorId(prestamoId);

        if (prestamo == null) {
            System.out.println("El préstamo no existe.");
            return;
        }

        BigDecimal saldo = pagoService.saldoPendiente(prestamoId);
        System.out.println("Saldo pendiente: " + dinero(saldo));

        if (saldo.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Este préstamo ya está pagado.");
            return;
        }

        BigDecimal monto = leerDecimalPositivo("Monto del pago: ");

        if (monto.compareTo(saldo) > 0) {
            System.out.println("El pago no puede superar el saldo pendiente.");
            return;
        }

        Pago pago = new Pago(prestamo, LocalDate.now(), monto);

        if (pagoService.registrar(pago)) {
            System.out.println("Pago registrado correctamente.");
            System.out.println("Nuevo saldo pendiente: "
                    + dinero(pagoService.saldoPendiente(prestamoId)));
        } else {
            System.out.println("No se pudo registrar el pago.");
        }
    }

    private static void listarPagos() {

        System.out.println("\n----- LISTA DE PAGOS -----");
        imprimirPagos(pagoService.listar(), "No hay pagos registrados.");
    }

    private static void historialPagos() {

        List<Pago> pagos = pagoService.historial(leerEntero("ID del préstamo: "));

        System.out.println("\n----- HISTORIAL DE PAGOS -----");
        imprimirPagos(pagos, "No hay pagos para este préstamo.");
    }

    private static void imprimirPagos(List<Pago> pagos, String mensajeVacio) {

        if (pagos.isEmpty()) {
            System.out.println(mensajeVacio);
            return;
        }

        pagos.forEach(p -> System.out.println(
                "ID: " + p.getId()
                        + " | Préstamo: " + (p.getPrestamo() != null ? p.getPrestamo().getId() : "N/D")
                        + " | Fecha: " + p.getFechaPago()
                        + " | Monto: " + dinero(p.getMonto())));
    }

    private static void consultarSaldo() {
        int id = leerEntero("ID del préstamo: ");
        System.out.println("Saldo pendiente: " + dinero(pagoService.saldoPendiente(id)));
    }

    private static void consultarTotalPagado() {
        int id = leerEntero("ID del préstamo: ");
        System.out.println("Total pagado: " + dinero(pagoService.totalPagado(id)));
    }

    private static void eliminarPago() {

        int id = leerEntero("ID del pago: ");

        if (!confirmar("¿Seguro que desea eliminar el pago " + id + "?")) {
            return;
        }

        resultado(pagoService.eliminar(id),
                "Pago eliminado correctamente.",
                "No se pudo eliminar el pago.");
    }

    // =========================================================
    // REPORTES
    // =========================================================

    private static void menuReportes() {
        ejecutarMenu("REPORTES", "Volver",
                new Opcion("Préstamos pendientes",
                        () -> reporte("PRÉSTAMOS PENDIENTES",
                                "No hay préstamos pendientes.",
                                p -> PENDIENTE.name().equals(p.getEstado()))),
                new Opcion("Préstamos pagados",
                        () -> reporte("PRÉSTAMOS PAGADOS",
                                "No hay préstamos pagados.",
                                p -> PAGADO.name().equals(p.getEstado()))),
                new Opcion("Préstamos mayores a " + dinero(MONTO_GRANDE),
                        () -> reporte("PRÉSTAMOS MAYORES A " + dinero(MONTO_GRANDE),
                                "No existen préstamos superiores a " + dinero(MONTO_GRANDE) + ".",
                                p -> p.getMonto().compareTo(MONTO_GRANDE) > 0)),
                new Opcion("Préstamos vencidos", MenuConsola::reportePrestamosVencidos),
                new Opcion("Clientes morosos", MenuConsola::reporteClientesMorosos),
                new Opcion("Clientes con préstamos", MenuConsola::reporteClientesConPrestamos),
                new Opcion("Consultar archivos de respaldo", MenuConsola::reporteArchivos));
    }

    private static void reporte(String titulo, String mensajeVacio, Predicate<Prestamo> filtro) {

        List<Prestamo> filtrados = prestamoService.listar().stream()
                .filter(filtro)
                .toList();

        System.out.println("\n----- " + titulo + " -----");

        if (filtrados.isEmpty()) {
            System.out.println(mensajeVacio);
            return;
        }

        mostrarPrestamos(filtrados);
    }

    private static void reporteClientesConPrestamos() {

        System.out.println("\n----- CLIENTES CON PRÉSTAMOS -----");

        List<String> nombres = prestamoService.listar().stream()
                .map(p -> p.getCliente().getNombre())
                .distinct()
                .toList();

        if (nombres.isEmpty()) {
            System.out.println("Ningún cliente tiene préstamos.");
            return;
        }

        nombres.forEach(n -> System.out.println("- " + n));
    }

    private static void reportePrestamosVencidos() {
        reporte("PRÉSTAMOS VENCIDOS",
                "No hay préstamos vencidos.",
                Prestamo::estaVencido);
    }

    private static void reporteClientesMorosos() {
        System.out.println("\n----- CLIENTES MOROSOS -----");

        prestamoService.listar().stream()
                .filter(Prestamo::estaVencido)
                .map(p -> p.getCliente().getNombre() + " (documento: "
                        + p.getCliente().getDocumento() + ")")
                .distinct()
                .forEach(System.out::println);
    }

    private static void reporteArchivos() {
        System.out.println("\n----- ARCHIVOS DE RESPALDO -----");
        List.of("empleados.txt", "clientes.txt", "prestamos.txt", "pagos.txt")
                .forEach(archivo -> {
                    List<String> lineas = ArchivoUtil.leer(archivo);
                    System.out.println(archivo + ": " + lineas.size() + " registros");
                    lineas.forEach(linea -> System.out.println("  " + linea));
                });
    }

    // =========================================================
    // UTILIDADES DE ENTRADA / FORMATO
    // =========================================================

    private static String dinero(BigDecimal valor) {
        return String.format(CO, "$%,.2f", valor);
    }

    private static boolean confirmar(String mensaje) {
        System.out.print(mensaje + " (s/n): ");
        return scanner.nextLine().trim().equalsIgnoreCase("s");
    }

    private static String leerTexto(String mensaje) {
        return leer(mensaje, s -> s, s -> !s.isEmpty(), "El campo no puede estar vacío.");
    }


    private static String leerTextoOpcional(String campo, String actual) {
        System.out.print(campo + " [" + actual + "]: ");
        String entrada = scanner.nextLine().trim();
        return entrada.isEmpty() ? actual : entrada;
    }

    private static BigDecimal leerDecimalOpcional(String campo, BigDecimal actual) {
        while (true) {
            System.out.print(campo + " [" + actual + "]: ");
            String entrada = scanner.nextLine().trim();
            if (entrada.isEmpty()) return actual;
            try {
                BigDecimal valor = new BigDecimal(entrada);
                if (valor.compareTo(BigDecimal.ZERO) > 0) return valor;
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Ingrese un número mayor que 0.");
        }
    }

    private static int leerEntero(String mensaje) {
        return leer(mensaje, Integer::parseInt,
                n -> true, "Ingrese un número entero válido.");
    }

    private static int leerEnteroPositivo(String mensaje) {
        return leer(mensaje, Integer::parseInt,
                n -> n > 0, "Ingrese un entero mayor que 0.");
    }

    private static BigDecimal leerDecimalPositivo(String mensaje) {
        return leer(mensaje, BigDecimal::new,
                d -> d.compareTo(BigDecimal.ZERO) > 0,
                "Ingrese un número mayor que 0.");
    }

    private static BigDecimal leerDecimalNoNegativo(String mensaje) {
        return leer(mensaje, BigDecimal::new,
                d -> d.compareTo(BigDecimal.ZERO) >= 0,
                "Ingrese un número mayor o igual a 0.");
    }


    private static <T> T leer(String mensaje,
                              Function<String, T> parser,
                              Predicate<T> valido,
                              String error) {
        while (true) {
            System.out.print(mensaje);

            try {
                T valor = parser.apply(scanner.nextLine().trim());
                if (valido.test(valor)) {
                    return valor;
                }
            } catch (NumberFormatException ignored) {
            }

            System.out.println(error);
        }
    }
}