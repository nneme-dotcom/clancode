package clancode.modelo.dao;

import clancode.modelo.Articulo; // Añade este
import clancode.modelo.Cliente;  // Añade este
import clancode.modelo.Pedido;   // ESTE ES EL QUE FALTA

public class MySQLDAOFactory implements DAOFactory {
    
    @Override
    public DAO<Articulo, String> getArticuloDAO() {
        return new ArticuloDAO_MySQL();
    }

    @Override
    public DAO<Cliente, String> getClienteDAO() {
        return new ClienteDAO_MySQL();
    }

    @Override
    public DAO<Pedido, Integer> getPedidoDAO() {
        return new PedidoDAO_MySQL();
    }
}