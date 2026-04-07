package clancode.modelo.dao;

import clancode.modelo.Articulo;
import clancode.util.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticuloDAO_MySQL implements DAO<Articulo, String> {

    @Override
    public void insertar(Articulo a) throws SQLException {
        String sql = "INSERT INTO articulos (codigo, descripcion, precio_venta, gastos_envio, tiempo_preparacion_min) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getCodigo());
            ps.setString(2, a.getDescripcion());
            ps.setDouble(3, a.getPrecioVenta());
            ps.setDouble(4, a.getGastosEnvio());
            ps.setInt(5, a.getTiempoPreparacion());
            ps.executeUpdate();
        }
    }

    @Override
    public Articulo obtener(String codigo) throws SQLException {
        String sql = "SELECT * FROM articulos WHERE codigo = ?";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Articulo(rs.getString(1), rs.getString(2), rs.getDouble(3), rs.getDouble(4), rs.getInt(5));
            }
        }
        return null;
    }

    @Override
    public List<Articulo> obtenerTodos() throws SQLException {
        List<Articulo> lista = new ArrayList<>();
        String sql = "SELECT * FROM articulos";
        try (Connection conn = ConexionBD.conectar(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Articulo(rs.getString(1), rs.getString(2), rs.getDouble(3), rs.getDouble(4), rs.getInt(5)));
            }
        }
        return lista;
    }

    @Override
    public void eliminar(String codigo) throws SQLException {
        String sql = "DELETE FROM articulos WHERE codigo = ?";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            ps.executeUpdate();
        }
    }
}