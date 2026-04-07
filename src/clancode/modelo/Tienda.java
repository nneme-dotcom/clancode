package clancode.modelo;

import clancode.modelo.dao.*;
import clancode.excepciones.*;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Tienda {
    // Capa de persistencia delegada a los DAOs
    private DAO<Articulo, String> articuloDAO;
    private DAO<Cliente, String> clienteDAO;
    private DAO<Pedido, Integer> pedidoDAO;

    public Tienda() {
        // Uso del patrón Factory para independencia del almacén de datos
        DAOFactory factory = new MySQLDAOFactory();
        
        this.articuloDAO = factory.getArticuloDAO();
        this.clienteDAO = factory.getClienteDAO();
        this.pedidoDAO = factory.getPedidoDAO();
    }

    // ================= SECCIÓN ARTÍCULOS =================

    public void añadirArticulo(Articulo articulo) throws ArticuloYaExisteException {
        try {
            if (articuloDAO.obtener(articulo.getCodigo()) != null) {
                throw new ArticuloYaExisteException("El artículo ya existe en el sistema.");
            }
            articuloDAO.insertar(articulo);
        } catch (ArticuloYaExisteException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Articulo> getListaArticulos() {
        try {
            return articuloDAO.obtenerTodos();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ================= SECCIÓN CLIENTES =================

    public void añadirCliente(Cliente cliente) throws ClienteYaExisteException {
        try {
            if (clienteDAO.obtener(cliente.getEmail()) != null) {
                throw new ClienteYaExisteException("El cliente ya existe en el sistema.");
            }
            clienteDAO.insertar(cliente);
        } catch (ClienteYaExisteException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Cliente> getListaClientes() {
        try {
            return clienteDAO.obtenerTodos();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Cliente> getClientesEstandard() {
        return getListaClientes().stream()
                .filter(c -> c instanceof ClienteEstandar)
                .collect(Collectors.toList());
    }

    public List<Cliente> getClientesPremium() {
        return getListaClientes().stream()
                .filter(c -> c instanceof ClientePremium)
                .collect(Collectors.toList());
    }

    // ================= SECCIÓN PEDIDOS =================

    public void añadirPedido(Pedido pedido) {
        try {
            pedidoDAO.insertar(pedido);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminarPedido(int numeroPedido) throws PedidoNoEncontradoException, PedidoNoCancelableException {
        try {
            Pedido pedido = pedidoDAO.obtener(numeroPedido);
            if (pedido == null) {
                throw new PedidoNoEncontradoException("No se encontró el pedido número: " + numeroPedido);
            }
            
            if (!pedido.esCancelable()) {
                throw new PedidoNoCancelableException("El pedido ya ha sido enviado y no puede cancelarse.");
            }
            
            pedidoDAO.eliminar(numeroPedido);
        } catch (PedidoNoEncontradoException | PedidoNoCancelableException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Pedido> getListaPedidos() {
        try {
            return pedidoDAO.obtenerTodos();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Pedido> getPedidosPendientes() {
        return getListaPedidos().stream()
                .filter(p -> !p.pedidoEnviado())
                .collect(Collectors.toList());
    }

    public List<Pedido> getPedidosEnviados() {
        return getListaPedidos().stream()
                .filter(Pedido::pedidoEnviado)
                .collect(Collectors.toList());
    }
}