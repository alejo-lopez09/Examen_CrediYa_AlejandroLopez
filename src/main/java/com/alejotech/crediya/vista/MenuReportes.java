package com.alejotech.crediya.vista;

import com.alejotech.crediya.excepciones.CrediYaException;
import com.alejotech.crediya.modelo.Cliente;
import com.alejotech.crediya.modelo.Empleado;
import com.alejotech.crediya.modelo.EstadoPrestamo;
import com.alejotech.crediya.modelo.Pago;
import com.alejotech.crediya.modelo.Persona;
import com.alejotech.crediya.modelo.Prestamo;
import com.alejotech.crediya.service.ClienteService;
import com.alejotech.crediya.service.EmpleadoService;
import com.alejotech.crediya.service.PagoService;
import com.alejotech.crediya.service.PrestamoService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MenuReportes {
    private final Scanner scanner;
    private final EmpleadoService empleadoService;
    private final ClienteService clienteService;
    private final PrestamoService prestamoService;
    private final PagoService pagoService;

    public MenuReportes(Scanner scanner, EmpleadoService empleadoService,
                        ClienteService clienteService, PrestamoService prestamoService,
                        PagoService pagoService) {
        this.scanner = scanner;
        this.empleadoService = empleadoService;
        this.clienteService = clienteService;
        this.prestamoService = prestamoService;
        this.pagoService = pagoService;
    }

    public void mostrar() {
        int opcion;
        do {
            System.out.println("\n========== REPORTES ==========");
            System.out.println("1. Préstamos activos (pendientes)");
            System.out.println("2. Préstamos pagados");
            System.out.println("3. Préstamos superiores a $1.000.000");
            System.out.println("4. Préstamos con cuotas vencidas (en mora)");
            System.out.println("5. Clientes con préstamos");
            System.out.println("6. Clientes morosos");
            System.out.println("7. Total prestado por empleado");
            System.out.println("8. Saldo total de cartera");
            System.out.println("9. Recaudo por cliente");
            System.out.println("10. Personas (polimorfismo)");
            System.out.println("11. Volver");
            opcion = leerEntero("Seleccione una opción: ");
            try {
                switch (opcion) {
                    case 1 -> filtrarEstado(EstadoPrestamo.PENDIENTE);
                    case 2 -> filtrarEstado(EstadoPrestamo.PAGADO);
                    case 3 -> prestamosGrandes();
                    case 4 -> prestamosEnMora();
                    case 5 -> clientesConPrestamos();
                    case 6 -> clientesMorosos();
                    case 7 -> totalPrestadoPorEmpleado();
                    case 8 -> saldoTotalCartera();
                    case 9 -> recaudoPorCliente();
                    case 10 -> listarPersonas();
                    case 11 -> { }
                    default -> System.out.println("Opción inválida.");
                }
            } catch (CrediYaException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (opcion != 11);
    }

    private void filtrarEstado(EstadoPrestamo estado) {
        System.out.println(estado == EstadoPrestamo.PENDIENTE
                ? "\n--- PRÉSTAMOS ACTIVOS (PENDIENTES) ---"
                : "\n--- PRÉSTAMOS PAGADOS ---");
        prestamoService.listar().stream()
                .filter(p -> p.getEstado() == estado)
                .forEach(System.out::println);
    }

    private void prestamosGrandes() {
        System.out.println("\n--- PRÉSTAMOS SUPERIORES A $1.000.000 ---");
        prestamoService.listar().stream()
                .filter(p -> p.getMonto().compareTo(new BigDecimal("1000000")) > 0)
                .forEach(System.out::println);
    }

    private void prestamosEnMora() {
        Map<Integer, BigDecimal> pagos = pagoService.totalesPagadosPorPrestamo();
        System.out.println("\n--- PRÉSTAMOS EN MORA ---");
        prestamoService.listar().stream()
                .filter(p -> p.estaVencido(pagos.getOrDefault(p.getId(), BigDecimal.ZERO),
                        LocalDate.now()))
                .forEach(System.out::println);
    }

    private void clientesConPrestamos() {
        var clientesConPrestamos = prestamoService.listar().stream()
                .map(Prestamo::getCliente)
                .filter(cliente -> cliente != null)
                .collect(Collectors.toMap(Cliente::getId, c -> c, (a, b) -> a, LinkedHashMap::new));
        System.out.println("\n--- CLIENTES CON PRÉSTAMOS ---");
        clientesConPrestamos.values().forEach(System.out::println);
    }

    private void clientesMorosos() {
        Map<Integer, BigDecimal> pagos = pagoService.totalesPagadosPorPrestamo();
        Map<Integer, Cliente> clientes = prestamoService.listar().stream()
                .filter(p -> p.estaVencido(pagos.getOrDefault(p.getId(), BigDecimal.ZERO),
                        LocalDate.now()))
                .map(Prestamo::getCliente)
                .filter(cliente -> cliente != null)
                .collect(Collectors.toMap(Cliente::getId, c -> c, (a, b) -> a, LinkedHashMap::new));
        System.out.println("\n--- CLIENTES MOROSOS ---");
        clientes.values().forEach(System.out::println);
    }

    private void totalPrestadoPorEmpleado() {
        List<Prestamo> prestamos = prestamoService.listar();
        Map<Integer, BigDecimal> totales = prestamos.stream()
                .collect(Collectors.groupingBy(p -> p.getEmpleado().getId(),
                        TreeMap::new,
                        Collectors.reducing(BigDecimal.ZERO, Prestamo::getMonto, BigDecimal::add)));
        Map<Integer, Empleado> empleados = empleadoService.listar().stream()
                .collect(Collectors.toMap(Empleado::getId, e -> e));
        empleados.keySet().forEach(id -> totales.putIfAbsent(id, BigDecimal.ZERO));
        System.out.println("\n--- TOTAL PRESTADO POR EMPLEADO ---");
        totales.forEach((id, total) -> System.out.println(
                empleados.get(id).getNombre() + ": $" + total));
    }

    private void saldoTotalCartera() {
        Map<Integer, BigDecimal> pagos = pagoService.totalesPagadosPorPrestamo();
        BigDecimal saldo = prestamoService.listar().stream()
                .map(p -> p.calcularMontoTotal()
                        .subtract(pagos.getOrDefault(p.getId(), BigDecimal.ZERO))
                        .max(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("\nSaldo total de cartera: $" + saldo);
    }

    private void recaudoPorCliente() {
        Map<Integer, Prestamo> prestamos = prestamoService.listar().stream()
                .collect(Collectors.toMap(Prestamo::getId, p -> p));
        Map<Integer, BigDecimal> recaudo = pagoService.listar().stream()
                .filter(p -> prestamos.containsKey(p.getPrestamo().getId()))
                .collect(Collectors.groupingBy(
                        p -> prestamos.get(p.getPrestamo().getId()).getCliente().getId(),
                        TreeMap::new,
                        Collectors.reducing(BigDecimal.ZERO, Pago::getMonto, BigDecimal::add)));
        Map<Integer, Cliente> clientes = clienteService.listar().stream()
                .collect(Collectors.toMap(Cliente::getId, c -> c));
        clientes.keySet().forEach(id -> recaudo.putIfAbsent(id, BigDecimal.ZERO));
        System.out.println("\n--- RECAUDO POR CLIENTE ---");
        recaudo.forEach((id, total) ->
                System.out.println(clientes.get(id).getNombre() + ": $" + total));
    }

    private void listarPersonas() {
        List<Persona> personas = Stream.concat(empleadoService.listar().stream(),
                clienteService.listar().stream()).toList();
        System.out.println("\n--- PERSONAS ---");
        personas.forEach(persona -> System.out.println(
                persona.getTipo() + " | " + persona.getNombre() + " | " + persona.getDocumento()));
    }

    private int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido.");
            }
        }
    }
}
