package clancode.modelo.dao;

import clancode.modelo.*;
import clancode.util.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO_MySQL implements DAO<Pedido, Integer> {

    @Override
    public void insertar(Pedido p) throws SQLException {
        Connection conn = null;
        try {
            conn = ConexionBD.conectar();
            conn.setAutoCommit(false);

            String sql = "INSERT INTO pedidos (cliente_email, articulo_codigo, cantidad, fecha_hora) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, p.getCliente().getEmail());
                ps.setString(2, p.getArticulo().getCodigo());
                ps.setInt(3, p.getCantidad());
                ps.setTimestamp(4, Timestamp.valueOf(p.getFechaHora()));
                ps.executeUpdate();
            }
            conn.commit(); 
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }

    @Override
    public void eliminar(Integer id) throws SQLException {
        String sql = "{ call eliminarPedido(?) }"; 
        try (Connection conn = ConexionBD.conectar(); 
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, id);
            cs.execute();
        }
    }

    // Implementar obtener y obtenerTodos siguiendo la lógica de reconstrucción de objetos
   @Override
    public Pedido obtener(Integer id) throws Exception {
        String sql = "SELECT * FROM pedidos WHERE numero_pedido = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Usa la misma lógica que en obtenerTodos para buscar cliente y artículo
                    Cliente cliente = new ClienteDAO_MySQL().obtener(rs.getString("cliente_email"));
                    Articulo articulo = new ArticuloDAO_MySQL().obtener(rs.getString("articulo_codigo"));
                    
                    return new Pedido(
                        rs.getInt("numero_pedido"),
                        cliente,
                        articulo,
                        rs.getInt("cantidad"),
                        rs.getTimestamp("fecha_hora").toLocalDateTime()
                    );
                }
            }
        }
    return null;
}
    @Override public List<Pedido> obtenerTodos() throws Exception {List<Pedido> lista = new ArrayList<>();
    String sql = "SELECT * FROM pedidos";
    
    // Necesitamos los otros DAOs para buscar al cliente y al artículo por su ID/Email
    ClienteDAO_MySQL clienteDAO = new ClienteDAO_MySQL();
    ArticuloDAO_MySQL articuloDAO = new ArticuloDAO_MySQL();

    try (Connection conn = ConexionBD.conectar();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            // 1. Extraemos los IDs de la base de datos
            String emailCliente = rs.getString("cliente_email");
            String codigoArticulo = rs.getString("articulo_codigo");
            
            
            // 2. Buscamos los objetos completos usando los otros DAOs
            Cliente cliente = clienteDAO.obtener(emailCliente);
            Articulo articulo = articuloDAO.obtener(codigoArticulo);

            // 3. Creamos el objeto Pedido con el constructor completo
            Pedido pedido = new Pedido(
                rs.getInt("numero_pedido"),
                cliente,
                articulo,
                rs.getInt("cantidad"),
                rs.getTimestamp("fecha_hora").toLocalDateTime()
            );
            
            lista.add(pedido);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        throw new Exception("Error al obtener los pedidos de la base de datos");
    }
    return lista;
    }
}