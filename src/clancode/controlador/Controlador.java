package clancode.controlador;

import clancode.excepciones.*;
import clancode.modelo.*;
import clancode.modelo.dao.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador único de la aplicación. Actúa como puente entre la Vista y el Modelo.
 *
 * Estrategia de persistencia:
 *   - Al arrancar intenta conectar con MySQL vía MySQLDAOFactory.
 *   - Si la conexión falla (BD apagada, credenciales erróneas, etc.), el controlador
 *     cae automáticamente a modo en-memoria usando la clase Tienda.
 *   - En ambos modos la Vista llama exactamente los mismos métodos públicos,
 *     sin saber cuál está activo (patrón Strategy implícito).
 *
 * La Vista SOLO interactúa con esta clase; nunca accede directamente al Modelo.
 */
public class Controlador {

    // ── Fuentes de datos ──────────────────────────────────────────────────────

    /** Modo MySQL: acceso a la BD mediante los DAOs */
    private final DAOFactory daoFactory;

    /** Modo fallback: datos en memoria si la BD no está disponible */
    private final Tienda tienda;

    /** true → usar MySQL; false → usar Tienda en memoria */
    private final boolean modoMySQL;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Intenta conectar con MySQL. Si la conexión no está disponible,
     * registra el aviso y opera en modo memoria para que la app arranque igualmente.
     */
    public Controlador() {
        DAOFactory factory = null;
        boolean mysqlDisponible = false;

        try {
            factory = new MySQLDAOFactory();
            // Prueba real: intentamos listar artículos para verificar la conexión
            factory.getArticuloDAO().obtenerTodos();
            mysqlDisponible = true;
            System.out.println("[Controlador] Modo MySQL activo.");
        } catch (Exception e) {
            System.out.println("[Controlador] BD no disponible, usando modo memoria. (" + e.getMessage() + ")");
        }

        this.daoFactory = factory;
        this.modoMySQL  = mysqlDisponible;
        this.tienda     = new Tienda(); // siempre se crea; solo se usa si !modoMySQL
    }

    /**
     * Indica si la aplicación está usando la base de datos MySQL o modo memoria.
     * Útil para mostrar un indicador en la interfaz gráfica.
     */
    public boolean isModoMySQL() {
        return modoMySQL;
    }

    // ── ARTÍCULOS ─────────────────────────────────────────────────────────────

    /**
     * Añade un nuevo artículo.
     * En modo MySQL lo inserta en la BD; en modo memoria lo añade a la Tienda.
     *
     * @throws ArticuloYaExisteException si ya existe un artículo con ese código
     */
    public void añadirArticulo(String codigo, String descripcion,
                               double precio, double gastos, int tiempo)
            throws ArticuloYaExisteException {

        Articulo articulo = new Articulo(codigo, descripcion, precio, gastos, tiempo);

        if (modoMySQL) {
            try {
                // Verificamos duplicado consultando la BD antes de insertar
                if (daoFactory.getArticuloDAO().obtener(codigo) != null) {
                    throw new ArticuloYaExisteException(
                        "Ya existe un artículo con el código: " + codigo);
                }
                daoFactory.getArticuloDAO().insertar(articulo);
            } catch (ArticuloYaExisteException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("Error al guardar el artículo en la BD: " + e.getMessage(), e);
            }
        } else {
            tienda.añadirArticulo(articulo);
        }
    }

    /**
     * Devuelve todos los artículos disponibles.
     */
    public List<Articulo> getArticulos() {
        if (modoMySQL) {
            try {
                return daoFactory.getArticuloDAO().obtenerTodos();
            } catch (Exception e) {
                System.err.println("[Controlador] Error al obtener artículos: " + e.getMessage());
                return new ArrayList<>();
            }
        }
        return tienda.getArticulos();
    }

    // ── CLIENTES ──────────────────────────────────────────────────────────────

    /**
     * Añade un cliente de tipo Estándar.
     *
     * @throws ClienteYaExisteException si ya hay un cliente con ese email
     */
    public void añadirClienteEstandar(String nombre, String domicilio,
                                      String nif, String email)
            throws ClienteYaExisteException {
        Cliente cliente = new ClienteEstandar(nombre, domicilio, nif, email);
        añadirClienteInterno(cliente);
    }

    /**
     * Añade un cliente de tipo Premium.
     *
     * @throws ClienteYaExisteException si ya hay un cliente con ese email
     */
    public void añadirClientePremium(String nombre, String domicilio,
                                     String nif, String email)
            throws ClienteYaExisteException {
        Cliente cliente = new ClientePremium(nombre, domicilio, nif, email);
        añadirClienteInterno(cliente);
    }

    /** Lógica común de inserción de cliente (evita duplicar código). */
    private void añadirClienteInterno(Cliente cliente) throws ClienteYaExisteException {
        if (modoMySQL) {
            try {
                if (daoFactory.getClienteDAO().obtener(cliente.getEmail()) != null) {
                    throw new ClienteYaExisteException(
                        "Ya existe un cliente con el email: " + cliente.getEmail());
                }
                daoFactory.getClienteDAO().insertar(cliente);
            } catch (ClienteYaExisteException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("Error al guardar el cliente en la BD: " + e.getMessage(), e);
            }
        } else {
            tienda.añadirCliente(cliente);
        }
    }

    /**
     * Busca un cliente por email; devuelve null si no existe (sin lanzar excepción).
     * Útil para comprobar existencia antes de crear un pedido.
     */
    public Cliente buscarClienteONull(String email) {
        if (modoMySQL) {
            try {
                return daoFactory.getClienteDAO().obtener(email.toLowerCase());
            } catch (Exception e) {
                return null;
            }
        }
        return tienda.buscarClienteONull(email);
    }

    /** Devuelve todos los clientes. */
    public List<Cliente> getClientes() {
        if (modoMySQL) {
            try {
                return daoFactory.getClienteDAO().obtenerTodos();
            } catch (Exception e) {
                System.err.println("[Controlador] Error al obtener clientes: " + e.getMessage());
                return new ArrayList<>();
            }
        }
        return tienda.getClientes();
    }

    /** Devuelve solo los clientes de tipo Estándar. */
    public List<Cliente> getClientesEstandar() {
        return getClientes().stream()
                .filter(c -> c instanceof ClienteEstandar)
                .collect(Collectors.toList());
    }

    /** Devuelve solo los clientes de tipo Premium. */
    public List<Cliente> getClientesPremium() {
        return getClientes().stream()
                .filter(c -> c instanceof ClientePremium)
                .collect(Collectors.toList());
    }

    // ── PEDIDOS ───────────────────────────────────────────────────────────────

    /**
     * Crea un pedido a partir del email del cliente y el código del artículo.
     * Lanza excepciones si alguno no se encuentra.
     *
     * @throws ArticuloNoEncontradoException si el código no existe
     * @throws ClienteNoEncontradoException  si el email no está registrado
     */
    public void añadirPedido(String emailCliente, String codigoArticulo, int cantidad)
            throws ArticuloNoEncontradoException, ClienteNoEncontradoException {

        if (modoMySQL) {
            try {
                Cliente cliente = daoFactory.getClienteDAO().obtener(emailCliente);
                if (cliente == null) throw new ClienteNoEncontradoException(
                    "No se encontró el cliente: " + emailCliente);

                Articulo articulo = daoFactory.getArticuloDAO().obtener(codigoArticulo);
                if (articulo == null) throw new ArticuloNoEncontradoException(
                    "No se encontró el artículo: " + codigoArticulo);

                daoFactory.getPedidoDAO().insertar(new Pedido(cliente, articulo, cantidad));
            } catch (ArticuloNoEncontradoException | ClienteNoEncontradoException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("Error al guardar el pedido en la BD: " + e.getMessage(), e);
            }
        } else {
            Cliente cliente = tienda.buscarCliente(emailCliente);
            Articulo articulo = tienda.buscarArticulo(codigoArticulo);
            tienda.añadirPedido(new Pedido(cliente, articulo, cantidad));
        }
    }

    /**
     * Versión alternativa de añadirPedido cuando ya tenemos el objeto Cliente.
     * Útil desde la vista cuando el cliente ya fue validado previamente.
     *
     * @throws ArticuloNoEncontradoException si el código del artículo no existe
     */
    public void añadirPedidoConCliente(Cliente cliente, String codigoArticulo, int cantidad)
            throws ArticuloNoEncontradoException {

        if (modoMySQL) {
            try {
                Articulo articulo = daoFactory.getArticuloDAO().obtener(codigoArticulo);
                if (articulo == null) throw new ArticuloNoEncontradoException(
                    "No se encontró el artículo: " + codigoArticulo);

                daoFactory.getPedidoDAO().insertar(new Pedido(cliente, articulo, cantidad));
            } catch (ArticuloNoEncontradoException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("Error al guardar el pedido en la BD: " + e.getMessage(), e);
            }
        } else {
            try {
                Articulo articulo = tienda.buscarArticulo(codigoArticulo);
                tienda.añadirPedido(new Pedido(cliente, articulo, cantidad));
            } catch (ArticuloNoEncontradoException e) {
                throw e;
            }
        }
    }

    /**
     * Elimina un pedido si aún es cancelable (no enviado).
     *
     * @throws PedidoNoEncontradoException  si el número no existe
     * @throws PedidoNoCancelableException  si el pedido ya fue enviado
     */
    public void eliminarPedido(int numeroPedido)
            throws PedidoNoEncontradoException, PedidoNoCancelableException {

        if (modoMySQL) {
            try {
                // Verificamos si existe y si aún es cancelable antes de llamar al procedimiento
                Pedido p = daoFactory.getPedidoDAO().obtener(numeroPedido);
                if (p == null) throw new PedidoNoEncontradoException(
                    "No existe el pedido nº " + numeroPedido);
                if (!p.esCancelable()) throw new PedidoNoCancelableException(
                    "El pedido nº " + numeroPedido + " ya ha sido enviado y no se puede cancelar.");

                daoFactory.getPedidoDAO().eliminar(numeroPedido);
            } catch (PedidoNoEncontradoException | PedidoNoCancelableException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("Error al eliminar el pedido en la BD: " + e.getMessage(), e);
            }
        } else {
            tienda.eliminarPedido(numeroPedido);
        }
    }

    // ── Consultas de pedidos ──────────────────────────────────────────────────

    /** Devuelve todos los pedidos pendientes de envío (aún cancelables). */
    public List<Pedido> getPedidosPendientes() {
        return getTodosPedidos().stream()
                .filter(Pedido::esCancelable)
                .collect(Collectors.toList());
    }

    /** Devuelve los pedidos pendientes filtrados por email de cliente. */
    public List<Pedido> getPedidosPendientesPorCliente(String email) {
        return getTodosPedidos().stream()
                .filter(p -> p.esCancelable()
                          && p.getCliente().getEmail().equalsIgnoreCase(email))
                .collect(Collectors.toList());
    }

    /** Devuelve todos los pedidos ya enviados (no cancelables). */
    public List<Pedido> getPedidosEnviados() {
        return getTodosPedidos().stream()
                .filter(p -> !p.esCancelable())
                .collect(Collectors.toList());
    }

    /** Devuelve los pedidos enviados filtrados por email de cliente. */
    public List<Pedido> getPedidosEnviadosPorCliente(String email) {
        return getTodosPedidos().stream()
                .filter(p -> !p.esCancelable()
                          && p.getCliente().getEmail().equalsIgnoreCase(email))
                .collect(Collectors.toList());
    }

    // ── Utilidad privada ──────────────────────────────────────────────────────

    /**
     * Obtiene todos los pedidos del origen de datos activo (MySQL o memoria).
     * Centraliza el acceso para que los métodos de filtrado no dupliquen lógica.
     */
    private List<Pedido> getTodosPedidos() {
        if (modoMySQL) {
            try {
                return daoFactory.getPedidoDAO().obtenerTodos();
            } catch (Exception e) {
                System.err.println("[Controlador] Error al obtener pedidos: " + e.getMessage());
                return new ArrayList<>();
            }
        }
        // En modo memoria, combinamos pendientes + enviados de la Tienda
        List<Pedido> todos = new ArrayList<>();
        todos.addAll(tienda.getPedidosPendientes());
        todos.addAll(tienda.getPedidosEnviados());
        return todos;
    }
}
