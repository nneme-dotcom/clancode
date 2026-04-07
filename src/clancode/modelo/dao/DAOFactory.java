package clancode.modelo.dao;

import clancode.modelo.Articulo;
import clancode.modelo.Cliente; 
import clancode.modelo.Pedido;  

public interface DAOFactory {
    DAO<Articulo, String> getArticuloDAO();
    DAO<Cliente, String> getClienteDAO();
    DAO<Pedido, Integer> getPedidoDAO();
}