package clancode.modelo.dao;

import clancode.modelo.*;
import clancode.util.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO_MySQL implements DAO<Cliente, String> {

    @Override
    public void insertar(Cliente c) throws SQLException {
        String sql = "INSERT INTO clientes (email, nombre, domicilio, nif, tipo_cliente, cuota_anual, descuento_envio) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getEmail());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getDomicilio());
            ps.setString(4, c.getNif());
            ps.setString(5, (c instanceof ClientePremium) ? "Premium" : "Estandar");
            ps.setDouble(6, (c instanceof ClientePremium) ? ((ClientePremium) c).getCuotaAnual() : 0.0);
            ps.setDouble(7, c.descuentoEnvio());
            ps.executeUpdate();
        }
    }

    @Override
    public Cliente obtener(String email) throws SQLException {
        String sql = "SELECT * FROM clientes WHERE email = ?";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearCliente(rs);
            }
        }
        return null;
    }

    @Override
    public List<Cliente> obtenerTodos() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes";
        try (Connection conn = ConexionBD.conectar(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearCliente(rs));
            }
        }
        return lista;
    }

    // Método auxiliar para evitar repetir código de instanciación
    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        String tipo = rs.getString("tipo_cliente");
        if ("Premium".equals(tipo)) {
            return new ClientePremium(rs.getString("nombre"), rs.getString("domicilio"), rs.getString("nif"), rs.getString("email"));
        } else {
            return new ClienteEstandar(rs.getString("nombre"), rs.getString("domicilio"), rs.getString("nif"), rs.getString("email"));
        }
    }

    @Override
    public void eliminar(String email) throws SQLException {
        String sql = "DELETE FROM clientes WHERE email = ?";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.executeUpdate();
        }
    }
}