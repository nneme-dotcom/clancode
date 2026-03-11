package clancode.controlador;

import clancode.excepciones.*;
import clancode.modelo.*;

import java.util.List;

/**
 * Controlador único de la aplicación.
 * Actúa como puente entre la Vista y el Modelo.
 * La Vista SOLO interactúa con esta clase.
 */
public class Controlador {

    private Tienda tienda;

    public Controlador() {
        this.tienda = new Tienda();
    }

    // ==================== ARTÍCULOS ====================

    public void añadirArticulo(String codigo, String descripcion,
                               double precio, double gastos, int tiempo)
            throws ArticuloYaExisteException {
        Articulo articulo = new Articulo(codigo, descripcion, precio, gastos, tiempo);
        tienda.añadirArticulo(articulo);
    }

    public List<Articulo> getArticulos() {
        return tienda.getArticulos();
    }

    // ==================== CLIENTES ====================

    public void añadirClienteEstandar(String nombre, String domicilio,
                                      String nif, String email)
            throws ClienteYaExisteException {
        Cliente cliente = new ClienteEstandar(nombre, domicilio, nif, email);
        tienda.añadirCliente(cliente);
    }

    public void añadirClientePremium(String nombre, String domicilio,
                                     String nif, String email)
            throws ClienteYaExisteException {
        Cliente cliente = new ClientePremium(nombre, domicilio, nif, email);
        tienda.añadirCliente(cliente);
    }

    public Cliente buscarClienteONull(String email) {
        return tienda.buscarClienteONull(email);
    }

    public List<Cliente> getClientes() {
        return tienda.getClientes();
    }

    public List<Cliente> getClientesEstandar() {
        return tienda.getClientesEstandar();
    }

    public List<Cliente> getClientesPremium() {
        return tienda.getClientesPremium();
    }

    // ==================== PEDIDOS ====================

    public void añadirPedido(String emailCliente, String codigoArticulo, int cantidad)
            throws ArticuloNoEncontradoException, ClienteNoEncontradoException {
        Cliente cliente = tienda.buscarCliente(emailCliente);
        Articulo articulo = tienda.buscarArticulo(codigoArticulo);
        Pedido pedido = new Pedido(cliente, articulo, cantidad);
        tienda.añadirPedido(pedido);
    }

    public void añadirPedidoConCliente(Cliente cliente, String codigoArticulo, int cantidad)
            throws ArticuloNoEncontradoException {
        Articulo articulo = tienda.buscarArticulo(codigoArticulo);
        Pedido pedido = new Pedido(cliente, articulo, cantidad);
        tienda.añadirPedido(pedido);
    }

    public void eliminarPedido(int numeroPedido)
            throws PedidoNoEncontradoException, PedidoNoCancelableException {
        tienda.eliminarPedido(numeroPedido);
    }

    public List<Pedido> getPedidosPendientes() {
        return tienda.getPedidosPendientes();
    }

    public List<Pedido> getPedidosPendientesPorCliente(String email) {
        return tienda.getPedidosPendientesPorCliente(email);
    }

    public List<Pedido> getPedidosEnviados() {
        return tienda.getPedidosEnviados();
    }

    public List<Pedido> getPedidosEnviadosPorCliente(String email) {
        return tienda.getPedidosEnviadosPorCliente(email);
    }
}