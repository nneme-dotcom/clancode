package clancode.modelo.dao;

import clancode.modelo.*;
import clancode.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación MySQL del DAO de Pedidos.
 *
 * Operaciones soportadas:
 *   - insertar   → INSERT con transacción explícita (rollback si falla)
 *   - obtener    → SELECT por número de pedido, reconstruye Cliente + Artículo
 *   - obtenerTodos → SELECT todos, con JOIN a clientes y artículos
 *   - eliminar   → llama al procedimiento almacenado eliminarPedido(id)
 *
 * Nota sobre reconstrucción de objetos: para crear un Pedido completo
 * necesitamos los objetos Cliente y Articulo, así que hacemos un JOIN
 * en lugar de dos consultas separadas.
 */
public class PedidoDAO_MySQL implements DAO<Pedido, Integer> {

    // ── INSERT ────────────────────────────────────────────────────────────────

    /**
     * Inserta un pedido nuevo en la BD usando transacción explícita.
     * El número de pedido lo asigna AUTO_INCREMENT de MySQL.
     */
    @Override
    public void insertar(Pedido p) throws SQLException {
        Connection conn = null;
        try {
            conn = ConexionBD.conectar();
            conn.setAutoCommit(false);

            String sql = "INSERT INTO pedidos (cliente_email, articulo_codigo, cantidad, fecha_hora) "
                       + "VALUES (?, ?, ?, ?)";
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

    // ── SELECT por ID ─────────────────────────────────────────────────────────

    /**
     * Recupera un pedido por su número, incluyendo los datos del cliente
     * y del artículo mediante JOIN.
     *
     * @param id número de pedido (PRIMARY KEY AUTO_INCREMENT)
     * @return el Pedido reconstruido, o null si no existe
     */
    @Override
    public Pedido obtener(Integer id) throws SQLException {
        String sql = "SELECT p.numero_pedido, p.cantidad, p.fecha_hora, "
                   + "  c.email, c.nombre, c.domicilio, c.nif, c.tipo_cliente, "
                   + "  a.codigo, a.descripcion, a.precio_venta, a.gastos_envio, a.tiempo_preparacion_min "
                   + "FROM pedidos p "
                   + "JOIN clientes c ON p.cliente_email = c.email "
                   + "JOIN articulos a ON p.articulo_codigo = a.codigo "
                   + "WHERE p.numero_pedido = ?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPedido(rs);
                }
            }
        }
        return null;
    }

    // ── SELECT todos ──────────────────────────────────────────────────────────

    /**
     * Recupera todos los pedidos con JOIN a clientes y artículos,
     * ordenados por fecha descendente (los más recientes primero).
     */
    @Override
    public List<Pedido> obtenerTodos() throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT p.numero_pedido, p.cantidad, p.fecha_hora, "
                   + "  c.email, c.nombre, c.domicilio, c.nif, c.tipo_cliente, "
                   + "  a.codigo, a.descripcion, a.precio_venta, a.gastos_envio, a.tiempo_preparacion_min "
                   + "FROM pedidos p "
                   + "JOIN clientes c ON p.cliente_email = c.email "
                   + "JOIN articulos a ON p.articulo_codigo = a.codigo "
                   + "ORDER BY p.fecha_hora DESC";

        try (Connection conn = ConexionBD.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearPedido(rs));
            }
        }
        return lista;
    }

    // ── DELETE via procedimiento almacenado ───────────────────────────────────

    /**
     * Elimina un pedido llamando al procedimiento almacenado eliminarPedido(id).
     * El procedimiento se encarga de validar que el pedido exista en la BD.
     *
     * @param id número de pedido a eliminar
     */
    @Override
    public void eliminar(Integer id) throws SQLException {
        String sql = "{ call eliminarPedido(?) }";
        try (Connection conn = ConexionBD.conectar();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, id);
            cs.execute();
        }
    }

    // ── Utilidad privada ──────────────────────────────────────────────────────

    /**
     * Construye un objeto Pedido a partir de la fila actual del ResultSet.
     * Espera las columnas en el orden definido por los SELECT de esta clase.
     */
    private Pedido mapearPedido(ResultSet rs) throws SQLException {
        // Reconstruir cliente
        Cliente cliente;
        if ("Premium".equals(rs.getString("tipo_cliente"))) {
            cliente = new ClientePremium(
                rs.getString("nombre"), rs.getString("domicilio"),
                rs.getString("nif"),    rs.getString("email"));
        } else {
            cliente = new ClienteEstandar(
                rs.getString("nombre"), rs.getString("domicilio"),
                rs.getString("nif"),    rs.getString("email"));
        }

        // Reconstruir artículo
        Articulo articulo = new Articulo(
            rs.getString("codigo"),
            rs.getString("descripcion"),
            rs.getDouble("precio_venta"),
            rs.getDouble("gastos_envio"),
            rs.getInt("tiempo_preparacion_min"));

        // Reconstruir pedido con los datos originales de la BD
        return new Pedido(
            rs.getInt("numero_pedido"),
            cliente,
            articulo,
            rs.getInt("cantidad"),
            rs.getTimestamp("fecha_hora").toLocalDateTime());
    }
}
