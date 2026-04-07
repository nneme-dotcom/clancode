package clancode.vista;

import clancode.controlador.Controlador;
import java.util.Scanner;

public class Menu {
    private Controlador controlador;
    private Scanner teclado;

    public Menu() {
        this.controlador = new Controlador();
        this.teclado = new Scanner(System.in);
    }

    public void iniciar() {
        boolean salir = false;
        do {
            System.out.println("\n--- ONLINE STORE: CLANCODE ---");
            System.out.println("1. Gestión de Artículos");
            System.out.println("2. Gestión de Clientes");
            System.out.println("3. Gestión de Pedidos");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            String opcion = teclado.nextLine();

            switch (opcion) {
                case "1": menuArticulos(); break;
                case "2": menuClientes(); break;
                case "3": menuPedidos(); break;
                case "0": salir = true; break;
                default: System.out.println("Opción no válida.");
            }
        } while (!salir);
    }

    // ================= MENÚS SECUNDARIOS =================

    private void menuArticulos() {
        System.out.println("\n-- GESTIÓN DE ARTÍCULOS --");
        System.out.println("1. Añadir Artículo");
        System.out.println("2. Mostrar Artículos");
        String op = teclado.nextLine();

        if (op.equals("1")) {
            try {
                System.out.print("Código: "); String cp = teclado.nextLine();
                System.out.print("Descripción: "); String des = teclado.nextLine();
                System.out.print("Precio: "); double pr = Double.parseDouble(teclado.nextLine());
                System.out.print("Gastos de Envío: "); double ge = Double.parseDouble(teclado.nextLine());
                System.out.print("Tiempo Preparación (min): "); int tp = Integer.parseInt(teclado.nextLine());
                
                controlador.añadirArticulo(cp, des, pr, ge, tp);
                System.out.println("Articulo añadido correctamente.");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else if (op.equals("2")) {
            controlador.listarArticulos().forEach(System.out::println);
        }
    }

    private void menuClientes() {
        System.out.println("\n-- GESTIÓN DE CLIENTES --");
        System.out.println("1. Añadir Cliente");
        System.out.println("2. Mostrar Todos");
        System.out.println("3. Mostrar Premium");
        System.out.println("4. Mostrar Estandar");
        String op = teclado.nextLine();

        try {
            switch (op) {
                case "1":
                    System.out.print("Nombre: "); String nom = teclado.nextLine();
                    System.out.print("Domicilio: "); String dom = teclado.nextLine();
                    System.out.print("NIF: "); String nif = teclado.nextLine();
                    System.out.print("Email: "); String em = teclado.nextLine();
                    System.out.print("Tipo (Estandar/Premium): "); String tipo = teclado.nextLine();
                    controlador.añadirCliente(nom, dom, nif, em, tipo);
                    break;
                case "2": controlador.listarClientes().forEach(System.out::println); break;
                case "3": controlador.listarClientesPremium().forEach(System.out::println); break;
                case "4": controlador.listarClientesEstandar().forEach(System.out::println); break;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void menuPedidos() {
        System.out.println("\n-- GESTIÓN DE PEDIDOS --");
        System.out.println("1. Realizar Pedido");
        System.out.println("2. Eliminar Pedido");
        System.out.println("3. Mostrar Pendientes");
        System.out.println("4. Mostrar Enviados");
        String op = teclado.nextLine();

        try {
            switch (op) {
                case "1":
                    System.out.print("Email Cliente: "); String emC = teclado.nextLine();
                    System.out.print("Código Artículo: "); String cpA = teclado.nextLine();
                    System.out.print("Cantidad: "); int cant = Integer.parseInt(teclado.nextLine());
                    controlador.añadirPedido(emC, cpA, cant);
                    System.out.println("Pedido registrado.");
                    break;
                case "2":
                    System.out.print("Número de pedido a eliminar: ");
                    int num = Integer.parseInt(teclado.nextLine());
                    controlador.eliminarPedido(num);
                    System.out.println("Pedido eliminado.");
                    break;
                case "3": controlador.listarPedidosPendientes().forEach(System.out::println); break;
                case "4": controlador.listarPedidosEnviados().forEach(System.out::println); break;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}