package clancode.modelo;

import clancode.excepciones.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Clase central del modelo.
 * Contiene todas las colecciones de datos y toda la lógica de negocio.
 */
public class Tienda {

    // HashMap para artículos: clave = código, búsqueda O(1)
    private Map<String, Articulo> articulos;

    // HashMap para clientes: clave = email, búsqueda O(1)
    private Map<String, Cliente> clientes;

    // Lista genérica para pedidos: acceso secuencial + filtrado
    private ListaGenerica<Pedido> pedidos;

    public Tienda() {
        this.articulos = new HashMap<>();
        this.clientes = new HashMap<>();
        this.pedidos = new ListaGenerica<>();
    }

    // ==================== ARTÍCULOS ====================
    /**
     * Añade un nuevo artículo al inventario de la tienda.
     * Los artículos se identifican por código único.
     *
     * @param articulo el objeto Articulo a añadir
     * @throws ArticuloYaExisteException si ya existe un artículo con ese código
     *
     * Nota: El código del artículo es inmutable y se usa como clave en el HashMap
     */
    public void añadirArticulo(Articulo articulo) throws ArticuloYaExisteException {
        if (articulos.containsKey(articulo.getCodigo())) {
            throw new ArticuloYaExisteException(
                    "Ya existe un artículo con el código: " + articulo.getCodigo());
        }
        articulos.put(articulo.getCodigo(), articulo);
    }

    /**
     * Busca un artículo en la tienda por su código.
     *
     * @param codigo el código único del artículo a buscar
     * @return el objeto Articulo si existe
     * @throws ArticuloNoEncontradoException si el código no existe en la tienda
     *
     * @example buscarArticulo("A001") retorna el artículo con código A001
     */
    public Articulo buscarArticulo(String codigo) throws ArticuloNoEncontradoException {
        Articulo a = articulos.get(codigo);
        if (a == null) {
            throw new ArticuloNoEncontradoException(
                    "No se encontró ningún artículo con el código: " + codigo);
        }
        return a;
    }

    public List<Articulo> getArticulos() {
        return new ArrayList<>(articulos.values());
    }

    // ==================== CLIENTES ====================

    public void añadirCliente(Cliente cliente) throws ClienteYaExisteException {
        String key = cliente.getEmail().toLowerCase();
        if (clientes.containsKey(key)) {
            throw new ClienteYaExisteException(
                    "Ya existe un cliente con el email: " + cliente.getEmail());
        }
        clientes.put(key, cliente);
    }
    /**
     * Busca un cliente en la tienda por su email.
     * Los emails son únicos y se usan como identificador principal.
     *
     * @param email el email del cliente a buscar
     * @return el objeto Cliente si existe (puede ser ClienteEstandar o ClientePremium)
     * @throws ClienteNoEncontradoException si el email no está registrado
     *
     * @example buscarCliente("ana@test.com") retorna el objeto Cliente correspondiente
     */
    public Cliente buscarCliente(String email) throws ClienteNoEncontradoException {
        Cliente c = clientes.get(email.toLowerCase());
        if (c == null) {
            throw new ClienteNoEncontradoException(
                    "No se encontró ningún cliente con el email: " + email);
        }
        return c;
    }

    public Cliente buscarClienteONull(String email) {
        return clientes.get(email.toLowerCase());
    }

    public List<Cliente> getClientes() {
        return new ArrayList<>(clientes.values());
    }

    public List<Cliente> getClientesEstandar() {
        return clientes.values().stream()
                .filter(c -> c instanceof ClienteEstandar)
                .collect(Collectors.toList());
    }

    public List<Cliente> getClientesPremium() {
        return clientes.values().stream()
                .filter(c -> c instanceof ClientePremium)
                .collect(Collectors.toList());
    }

    // ==================== PEDIDOS ====================

    public void añadirPedido(Pedido pedido) {
        pedidos.añadir(pedido);
    }

    public Pedido buscarPedido(int numeroPedido) throws PedidoNoEncontradoException {
        for (Pedido p : pedidos.getLista()) {
            if (p.getNumeroPedido() == numeroPedido) {
                return p;
            }
        }
        throw new PedidoNoEncontradoException(
                "No se encontró ningún pedido con el número: " + numeroPedido);
    }

    public void eliminarPedido(int numeroPedido)
            throws PedidoNoEncontradoException, PedidoNoCancelableException {
        Pedido pedido = buscarPedido(numeroPedido);
        if (!pedido.esCancelable()) {
            throw new PedidoNoCancelableException(
                    "El pedido nº " + numeroPedido + " no se puede cancelar: ya ha sido enviado.");
        }
        pedidos.eliminar(pedido);
    }

    public List<Pedido> getPedidosPendientes() {
        return pedidos.getLista().stream()
                .filter(Pedido::esCancelable)
                .collect(Collectors.toList());
    }
    /**
     * Retorna todos los pedidos asociados a un cliente específico.
     *
     * @param email el email del cliente
     * @return una List con todos los pedidos del cliente
     * @throws ClienteNoEncontradoException si el email no existe
     *
     * Nota: Este método filtra los pedidos iterando la ListaGenerica
     */
    public List<Pedido> getPedidosPendientesPorCliente(String emailCliente) {
        return pedidos.getLista().stream()
                .filter(p -> p.esCancelable()
                        && p.getCliente().getEmail().equalsIgnoreCase(emailCliente))
                .collect(Collectors.toList());
    }

    public List<Pedido> getPedidosEnviados() {
        return pedidos.getLista().stream()
                .filter(p -> !p.esCancelable())
                .collect(Collectors.toList());
    }
    /**
     * Retorna todos los pedidos asociados a un cliente específico.
     *
     * @param email el email del cliente
     * @return una List con todos los pedidos del cliente
     * @throws ClienteNoEncontradoException si el email no existe
     *
     * Nota: Este método filtra los pedidos iterando la ListaGenerica
     */
    public List<Pedido> getPedidosEnviadosPorCliente(String emailCliente) {
        return pedidos.getLista().stream()
                .filter(p -> !p.esCancelable()
                        && p.getCliente().getEmail().equalsIgnoreCase(emailCliente))
                .collect(Collectors.toList());
    }
}