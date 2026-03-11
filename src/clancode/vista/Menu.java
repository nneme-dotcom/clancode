package clancode.vista;

import clancode.controlador.Controlador;
import clancode.excepciones.*;
import clancode.modelo.*;

import java.util.List;
import java.util.Scanner;

public class Menu {

    private Controlador controlador;
    private Scanner scanner;

    public Menu() {
        this.controlador = new Controlador();
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        int opcion;
        do {
            System.out.println("\n========================================");
            System.out.println("     ONLINE STORE - MENÚ PRINCIPAL");
            System.out.println("========================================");
            System.out.println("  1. Gestión de Artículos");
            System.out.println("  2. Gestión de Clientes");
            System.out.println("  3. Gestión de Pedidos");
            System.out.println("  0. Salir");
            System.out.println("========================================");
            opcion = leerEntero("Selecciona una opción: ");
            switch (opcion) {
                case 1 -> menuArticulos();
                case 2 -> menuClientes();
                case 3 -> menuPedidos();
                case 0 -> System.out.println("\n¡Hasta pronto!");
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    // ==================== MENÚ ARTÍCULOS ====================

    private void menuArticulos() {
        int opcion;
        do {
            System.out.println("\n--- Gestión de Artículos ---");
            System.out.println("  1. Añadir Artículo");
            System.out.println("  2. Mostrar Artículos");
            System.out.println("  0. Volver");
            opcion = leerEntero("Selecciona una opción: ");
            switch (opcion) {
                case 1 -> añadirArticulo();
                case 2 -> mostrarArticulos();
                case 0 -> {}
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    private void añadirArticulo() {
        System.out.println("\n-- Añadir Artículo --");
        System.out.print("Código: ");
        String codigo = scanner.nextLine().trim();
        System.out.print("Descripción: ");
        String descripcion = scanner.nextLine().trim();
        double precio = leerDouble("Precio de venta (€): ");
        double gastos = leerDouble("Gastos de envío (€): ");
        int tiempo = leerEntero("Tiempo de preparación (minutos): ");

        try {
            controlador.añadirArticulo(codigo, descripcion, precio, gastos, tiempo);
            System.out.println("✓ Artículo añadido correctamente.");
        } catch (ArticuloYaExisteException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private void mostrarArticulos() {
        List<Articulo> articulos = controlador.getArticulos();
        if (articulos.isEmpty()) {
            System.out.println("\nNo hay artículos registrados.");
        } else {
            System.out.println("\n-- Lista de Artículos --");
            articulos.forEach(a -> System.out.println("  " + a));
        }
    }

    // ==================== MENÚ CLIENTES ====================

    private void menuClientes() {
        int opcion;
        do {
            System.out.println("\n--- Gestión de Clientes ---");
            System.out.println("  1. Añadir Cliente");
            System.out.println("  2. Mostrar Todos los Clientes");
            System.out.println("  3. Mostrar Clientes Estándar");
            System.out.println("  4. Mostrar Clientes Premium");
            System.out.println("  0. Volver");
            opcion = leerEntero("Selecciona una opción: ");
            switch (opcion) {
                case 1 -> añadirCliente();
                case 2 -> mostrarLista(controlador.getClientes(), "Todos los Clientes");
                case 3 -> mostrarLista(controlador.getClientesEstandar(), "Clientes Estándar");
                case 4 -> mostrarLista(controlador.getClientesPremium(), "Clientes Premium");
                case 0 -> {}
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    private void añadirCliente() {
        System.out.println("\n-- Añadir Cliente --");
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine().trim();
        System.out.print("Domicilio: ");
        String domicilio = scanner.nextLine().trim();
        System.out.print("NIF: ");
        String nif = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("¿Es cliente Premium? (s/n): ");
        String tipo = scanner.nextLine().trim();

        try {
            if (tipo.equalsIgnoreCase("s")) {
                controlador.añadirClientePremium(nombre, domicilio, nif, email);
            } else {
                controlador.añadirClienteEstandar(nombre, domicilio, nif, email);
            }
            System.out.println("✓ Cliente añadido correctamente.");
        } catch (ClienteYaExisteException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    // Método genérico para mostrar cualquier lista
    private <T> void mostrarLista(List<T> lista, String titulo) {
        if (lista.isEmpty()) {
            System.out.println("\nNo hay elementos en: " + titulo);
        } else {
            System.out.println("\n-- " + titulo + " --");
            lista.forEach(item -> System.out.println("  " + item));
        }
    }

    // ==================== MENÚ PEDIDOS ====================

    private void menuPedidos() {
        int opcion;
        do {
            System.out.println("\n--- Gestión de Pedidos ---");
            System.out.println("  1. Añadir Pedido");
            System.out.println("  2. Eliminar Pedido");
            System.out.println("  3. Mostrar Pedidos Pendientes de Envío");
            System.out.println("  4. Mostrar Pedidos Enviados");
            System.out.println("  0. Volver");
            opcion = leerEntero("Selecciona una opción: ");
            switch (opcion) {
                case 1 -> añadirPedido();
                case 2 -> eliminarPedido();
                case 3 -> mostrarPedidosPendientes();
                case 4 -> mostrarPedidosEnviados();
                case 0 -> {}
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    private void añadirPedido() {
        System.out.println("\n-- Añadir Pedido --");

        System.out.print("Email del cliente: ");
        String email = scanner.nextLine().trim();
        Cliente cliente = controlador.buscarClienteONull(email);

        if (cliente == null) {
            System.out.println("Cliente no encontrado. Registrando nuevo cliente...");
            System.out.print("Nombre: ");
            String nombre = scanner.nextLine().trim();
            System.out.print("Domicilio: ");
            String domicilio = scanner.nextLine().trim();
            System.out.print("NIF: ");
            String nif = scanner.nextLine().trim();
            System.out.print("¿Es cliente Premium? (s/n): ");
            String tipo = scanner.nextLine().trim();

            try {
                if (tipo.equalsIgnoreCase("s")) {
                    controlador.añadirClientePremium(nombre, domicilio, nif, email);
                } else {
                    controlador.añadirClienteEstandar(nombre, domicilio, nif, email);
                }
                cliente = controlador.buscarClienteONull(email);
                System.out.println("✓ Cliente registrado.");
            } catch (ClienteYaExisteException e) {
                System.out.println("✗ Error: " + e.getMessage());
                return;
            }
        }

        System.out.print("Código del artículo: ");
        String codigo = scanner.nextLine().trim();

        int cantidad = leerEntero("Cantidad: ");
        if (cantidad <= 0) {
            System.out.println("✗ La cantidad debe ser mayor que 0.");
            return;
        }

        try {
            controlador.añadirPedidoConCliente(cliente, codigo, cantidad);
            System.out.println("✓ Pedido añadido correctamente.");
        } catch (ArticuloNoEncontradoException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private void eliminarPedido() {
        System.out.println("\n-- Eliminar Pedido --");
        int numero = leerEntero("Número de pedido a eliminar: ");
        try {
            controlador.eliminarPedido(numero);
            System.out.println("✓ Pedido eliminado correctamente.");
        } catch (PedidoNoEncontradoException | PedidoNoCancelableException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private void mostrarPedidosPendientes() {
        System.out.print("¿Filtrar por cliente? (s/n): ");
        String filtrar = scanner.nextLine().trim();
        List<Pedido> pedidos;
        if (filtrar.equalsIgnoreCase("s")) {
            System.out.print("Email del cliente: ");
            String email = scanner.nextLine().trim();
            pedidos = controlador.getPedidosPendientesPorCliente(email);
        } else {
            pedidos = controlador.getPedidosPendientes();
        }
        mostrarLista(pedidos, "Pedidos Pendientes de Envío");
    }

    private void mostrarPedidosEnviados() {
        System.out.print("¿Filtrar por cliente? (s/n): ");
        String filtrar = scanner.nextLine().trim();
        List<Pedido> pedidos;
        if (filtrar.equalsIgnoreCase("s")) {
            System.out.print("Email del cliente: ");
            String email = scanner.nextLine().trim();
            pedidos = controlador.getPedidosEnviadosPorCliente(email);
        } else {
            pedidos = controlador.getPedidosEnviados();
        }
        mostrarLista(pedidos, "Pedidos Enviados");
    }

    // ==================== UTILIDADES ====================

    private int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Por favor, introduce un número entero válido.");
            }
        }
    }

    private double leerDouble(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Por favor, introduce un número válido.");
            }
        }
    }
}