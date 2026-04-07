package clancode.modelo.dao;

import clancode.modelo.*;
import clancode.util.ConexionBD;
import java.sql.*;
import java.time.LocalDateTime;
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

    @Override
    public Pedido obtener(Integer id) throws Exception {
        String sql = "SELECT p.numero_pedido, p.cantidad, p.fecha_hora, " +
                "c.email, c.nombre, c.domicilio, c.nif, c.tipo_cliente, " +
                "a.codigo, a.descripcion, a.precio_venta, a.gastos_envio, a.tiempo_preparacion_min " +
                "FROM pedidos p " +
                "JOIN clientes c ON p.cliente_email = c.email " +
                "JOIN articulos a ON p.articulo_codigo = a.codigo " +
                "WHERE p.numero_pedido = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearPedido(rs);
            }
        }
        return null;
    }

    @Override
    public List<Pedido> obtenerTodos() throws Exception {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT p.numero_pedido, p.cantidad, p.fecha_hora, " +
                "c.email, c.nombre, c.domicilio, c.nif, c.tipo_cliente, " +
                "a.codigo, a.descripcion, a.precio_venta, a.gastos_envio, a.tiempo_preparacion_min " +
                "FROM pedidos p " +
                "JOIN clientes c ON p.cliente_email = c.email " +
                "JOIN articulos a ON p.articulo_codigo = a.codigo";
        try (Connection conn = ConexionBD.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearPedido(rs));
            }
        }
        return lista;
    }

    private Pedido mapearPedido(ResultSet rs) throws SQLException {
        int numeroPedido = rs.getInt("numero_pedido");
        int cantidad = rs.getInt("cantidad");
        LocalDateTime fechaHora = rs.getTimestamp("fecha_hora").toLocalDateTime();

        String tipo = rs.getString("tipo_cliente");
        Cliente cliente;
        if ("Premium".equals(tipo)) {
            cliente = new ClientePremium(
                    rs.getString("nombre"), rs.getString("domicilio"),
                    rs.getString("nif"), rs.getString("email"));
        } else {
            cliente = new ClienteEstandar(
                    rs.getString("nombre"), rs.getString("domicilio"),
                    rs.getString("nif"), rs.getString("email"));
        }

        Articulo articulo = new Articulo(
                rs.getString("codigo"), rs.getString("descripcion"),
                rs.getDouble("precio_venta"), rs.getDouble("gastos_envio"),
                rs.getInt("tiempo_preparacion_min"));

        return new Pedido(numeroPedido, cliente, articulo, cantidad, fechaHora);
    }
}