package clancode.controlador;

import clancode.modelo.*;
import clancode.modelo.dao.ClienteDAO_MySQL;
import clancode.excepciones.*;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDateTime;

public class Controlador {
    private Tienda tienda;

    public Controlador() {
        // Al instanciar la tienda, se activan los DAOs y la conexión a MySQL
        this.tienda = new Tienda();
    }

    // ================= GESTIÓN DE ARTÍCULOS =================

    public void añadirArticulo(String codigo, String descripcion, double precio, double gastosEnvio, int tiempoPreparacion) throws ArticuloYaExisteException {
        Articulo articulo = new Articulo(codigo, descripcion, precio, gastosEnvio, tiempoPreparacion);
        tienda.añadirArticulo(articulo);
    }

    public List<Articulo> listarArticulos() {
        return tienda.getListaArticulos();
    }

    // ================= GESTIÓN DE CLIENTES =================

    public void añadirCliente(String nombre, String domicilio, String nif, String email, String tipo) throws ClienteYaExisteException {
        Cliente cliente;
        if (tipo.equalsIgnoreCase("Premium")) {
            cliente = new ClientePremium(nombre, domicilio, nif, email);
        } else {
            cliente = new ClienteEstandar(nombre, domicilio, nif, email);
        }
        tienda.añadirCliente(cliente);
    }

    public List<Cliente> listarClientes() {
        return tienda.getListaClientes();
    }

    public List<Cliente> listarClientesEstandar() {
        return tienda.getClientesEstandard();
    }

    public List<Cliente> listarClientesPremium() {
        return tienda.getClientesPremium();
    }

    // ================= GESTIÓN DE PEDIDOS =================

    public void añadirPedido(String emailCliente, String codigoArticulo, int cantidad) throws Exception {
        // 1. Buscamos el cliente y el artículo para crear el objeto Pedido
        Cliente cliente = tienda.getListaClientes().stream()
                .filter(c -> c.getEmail().equals(emailCliente))
                .findFirst()
                .orElse(null); // Si no lo encuentra, devuelve null en vez de dar error

        // 2. Si el cliente no existe, lo creamos y lo añadimos a la tienda
        if (cliente == null) {
            Scanner sc = new Scanner(System.in);
            System.out.println("--- Cliente nuevo detectado. Por favor, introduzca sus datos ---");

            System.out.print("Nombre: ");
            String nombre = sc.nextLine();

            System.out.print("Domicilio: ");
            String domicilio = sc.nextLine();

            System.out.print("NIF: ");
            String nif = sc.nextLine();

            System.out.print("¿Es cliente Premium? (S/N): ");
            String esPremium = sc.nextLine().equalsIgnoreCase("S") ? "Premium" : "Estandar";

            // Llamas a tu método de Gestión de Clientes que ya tienes hecho
            this.añadirCliente(nombre, domicilio, nif, emailCliente, esPremium);

            // Ahora recuperamos el objeto recién creado para el pedido
            cliente = tienda.getListaClientes().stream()
                .filter(c -> c.getEmail().equals(emailCliente))
                .findFirst()
                .get();
        }

        Articulo articulo = tienda.getListaArticulos().stream()
                .filter(a -> a.getCodigo().equals(codigoArticulo))
                .findFirst()
                .orElseThrow(() -> new Exception("Artículo no encontrado"));

        // 2. Creamos el pedido con la fecha y hora actual
        // El número de pedido se generará automáticamente en la base de datos (Auto-increment)
        Pedido pedido = new Pedido(0, cliente, articulo, cantidad, LocalDateTime.now());
        
        // 3. Lo mandamos a la tienda para que el DAO lo inserte (Paso 9: Transacciones)
        tienda.añadirPedido(pedido);
    }

    public void eliminarPedido(int numeroPedido) throws PedidoNoEncontradoException, PedidoNoCancelableException {
        // Llama a la tienda, que a su vez llama al Procedimiento Almacenado (Paso 9)
        tienda.eliminarPedido(numeroPedido);
    }

    public List<Pedido> listarPedidosPendientes() {
        return tienda.getPedidosPendientes();
    }

    public List<Pedido> listarPedidosEnviados() {
        return tienda.getPedidosEnviados();
    }
    
}