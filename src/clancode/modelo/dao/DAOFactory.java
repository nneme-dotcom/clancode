package clancode.modelo.dao;

import clancode.modelo.Articulo; // Añade este
import clancode.modelo.Cliente;  // Añade este
import clancode.modelo.Pedido;   // ESTE ES EL QUE FALTA

public interface DAOFactory {
    DAO<Articulo, String> getArticuloDAO();
    DAO<Cliente, String> getClienteDAO();
    DAO<Pedido, Integer> getPedidoDAO();
}