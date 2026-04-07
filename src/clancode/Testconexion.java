package clancode;

import clancode.util.ConexionBD;
import java.sql.Connection;

public class Testconexion {

    public static void main(String[] args) {
        try {
            Connection con = ConexionBD.getConnection();
            System.out.println("✅ Conexión correcta a MySQL");
            con.close();
        } catch (Exception e) {
            System.out.println("❌ Error de conexión");
            e.printStackTrace();
        }
    }
}