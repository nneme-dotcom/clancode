package clancode.controlador;

import clancode.excepciones.*;
import clancode.modelo.*;
import clancode.modelo.dao.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador de la aplicación. Conecta la Vista con el Modelo.
 * Al arrancar intenta conectar con MySQL; si falla, usa datos en memoria.
 */
public class Controlador {

    private final DAOFactory daoFactory;
    private final Tienda tienda;
    private final boolean modoMySQL;

    public Controlador() {
        DAOFactory factory = null;
        boolean mysqlDisponible = false;

        try {
            factory = new MySQLDAOFactory();
            factory.getArticuloDAO().obtenerTodos();
            mysqlDisponible = true;
            System.out.println("[Controlador] Modo MySQL activo.");
        } catch (Exception e) {
            System.out.println("[Controlador] BD no disponible, usando modo memoria. (" + e.getMessage() + ")");
        }

        this.daoFactory = factory;
        this.modoMySQL  = mysqlDisponible;
        this.tienda     = new Tienda();
    }

    public boolean isModoMySQL() {
        return modoMySQL;
    }

    // ARTÍCULOS

    public void añadirArticulo(String codigo, String descripcion,
                               double precio, double gastos, int tiempo)
            throws ArticuloYaExisteException {

        Articulo articulo = new Articulo(codigo, descripcion, precio, gastos, tiempo);

        if (modoMySQL) {
            try {
                if (daoFactory.getArticuloDAO().obtener(codigo) != null) {
                    throw new ArticuloYaExisteException("Ya existe un artículo con el código: " + codigo);
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

    // CLIENTES

    public void añadirClienteEstandar(String nombre, String domicilio,
                                      String nif, String email)
            throws ClienteYaExisteException {
        añadirClienteInterno(new ClienteEstandar(nombre, domicilio, nif, email));
    }

    public void añadirClientePremium(String nombre, String domicilio,
                                     String nif, String email)
            throws ClienteYaExisteException {
        añadirClienteInterno(new ClientePremium(nombre, domicilio, nif, email));
    }

    private void añadirClienteInterno(Cliente cliente) throws ClienteYaExisteException {
        if (modoMySQL) {
            try {
                if (daoFactory.getClienteDAO().obtener(cliente.getEmail()) != null) {
                    throw new ClienteYaExisteException("Ya existe un cliente con el email: " + cliente.getEmail());
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

    public List<Cliente> getClientesEstandar() {
        return getClientes().stream()
                .filter(c -> c instanceof ClienteEstandar)
                .collect(Collectors.toList());
    }

    public List<Cliente> getClientesPremium() {
        return getClientes().stream()
                .filter(c -> c instanceof ClientePremium)
                .collect(Collectors.toList());
    }

    // PEDIDOS

    public void añadirPedido(String emailCliente, String codigoArticulo, int cantidad)
            throws ArticuloNoEncontradoException, ClienteNoEncontradoException {

        if (modoMySQL) {
            try {
                Cliente cliente = daoFactory.getClienteDAO().obtener(emailCliente);
                if (cliente == null) throw new ClienteNoEncontradoException("No se encontró el cliente: " + emailCliente);

                Articulo articulo = daoFactory.getArticuloDAO().obtener(codigoArticulo);
                if (articulo == null) throw new ArticuloNoEncontradoException("No se encontró el artículo: " + codigoArticulo);

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

    public void añadirPedidoConCliente(Cliente cliente, String codigoArticulo, int cantidad)
            throws ArticuloNoEncontradoException {

        if (modoMySQL) {
            try {
                Articulo articulo = daoFactory.getArticuloDAO().obtener(codigoArticulo);
                if (articulo == null) throw new ArticuloNoEncontradoException("No se encontró el artículo: " + codigoArticulo);
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

    public void eliminarPedido(int numeroPedido)
            throws PedidoNoEncontradoException, PedidoNoCancelableException {

        if (modoMySQL) {
            try {
                Pedido p = daoFactory.getPedidoDAO().obtener(numeroPedido);
                if (p == null) throw new PedidoNoEncontradoException("No existe el pedido nº " + numeroPedido);
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

    public List<Pedido> getPedidosPendientes() {
        return getTodosPedidos().stream().filter(Pedido::esCancelable).collect(Collectors.toList());
    }

    public List<Pedido> getPedidosPendientesPorCliente(String email) {
        return getTodosPedidos().stream()
                .filter(p -> p.esCancelable() && p.getCliente().getEmail().equalsIgnoreCase(email))
                .collect(Collectors.toList());
    }

    public List<Pedido> getPedidosEnviados() {
        return getTodosPedidos().stream().filter(p -> !p.esCancelable()).collect(Collectors.toList());
    }

    public List<Pedido> getPedidosEnviadosPorCliente(String email) {
        return getTodosPedidos().stream()
                .filter(p -> !p.esCancelable() && p.getCliente().getEmail().equalsIgnoreCase(email))
                .collect(Collectors.toList());
    }

    private List<Pedido> getTodosPedidos() {
        if (modoMySQL) {
            try {
                return daoFactory.getPedidoDAO().obtenerTodos();
            } catch (Exception e) {
                System.err.println("[Controlador] Error al obtener pedidos: " + e.getMessage());
                return new ArrayList<>();
            }
        }
        List<Pedido> todos = new ArrayList<>();
        todos.addAll(tienda.getPedidosPendientes());
        todos.addAll(tienda.getPedidosEnviados());
        return todos;
    }
}
